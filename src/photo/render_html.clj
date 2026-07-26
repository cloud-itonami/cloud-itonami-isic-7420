(ns photo.render-html
  "Build-time HTML renderer for docs/samples/operator-console.html.
  Closes flagship checklist item 2 (com-junkawasaki/root ADR-2607189300).
  Drives the REAL actor stack (photo.operation -> photo.governor ->
  photo.store). No invented numbers, no timestamps, byte-identical
  across reruns."
  (:require [jp-go-dds.skin]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [photo.store :as store]
            [photo.operation :as op]
            [photo.phase :as phase]
            [photo.governor :as governor]
            [langgraph.graph :as g]))

(def ^:private operator {:actor-id "op-1" :actor-role :studio-manager :phase 3})

(defn- exec! [actor tid request]
  (g/run* actor {:request request :context operator} {:thread-id tid}))

(defn- approve! [actor tid]
  (g/run* actor {:approval {:status :approved :by "op-1"}} {:thread-id tid :resume? true}))

(defn run-demo!
  "Drives the real OperationActor StateGraph through a scenario built
  directly from `photo.store/demo-data` and `photo.governor`'s actual
  rules (this repo's own `photo.sim` was run directly via
  `clojure -M:dev:run` and checked -- its ids/ops match the real seed
  data and rules, so this mirrors the same scenario rather than
  reusing `sim.cljc`'s `-main` directly, to keep this namespace's demo
  self-contained):

    1. `:engagement/intake` engagement-1 (JPN, clean) -- phase-3's
       only auto-eligible op, auto-commits without approval.
    2. `:shootplan/verify` engagement-1 (JPN has a real spec-basis in
       `photo.facts`) -- escalates (not auto-eligible at any phase)
       -> human approval -> commit.
    3. `:consent/screen` engagement-1 -- resolved -- escalates ->
       human approval -> commit.
    4. `:actuation/deliver-image-set` engagement-1 -- a
       `governor/high-stakes` op, ALWAYS escalates even when clean
       -> human approval -> commit (drafts a real image-set-delivery
       record via `photo.registry`).
    5. `:shootplan/verify` engagement-2 with `:no-spec? true` --
       engagement-2's own seeded jurisdiction is \"ATL\", which has NO
       entry in `photo.facts/catalog` -- HARD hold, rule
       `:no-spec-basis`.
    6. `:shootplan/verify` engagement-3 (JPN, has spec-basis) ->
       escalate -> approve -> commit, setting up the model-release
       test.
    7. `:actuation/deliver-image-set` engagement-3 -- engagement-3 is
       seeded with `:subjects-requiring-release #{:subject-a
       :subject-b}` but only `:subjects-with-signed-release
       #{:subject-a}` -- HARD hold, rule
       `:model-release-coverage-insufficient`.
    8. `:consent/screen` engagement-4 -- engagement-4 is seeded with
       `:minor-subject-guardian-consent-unresolved? true` -- HARD
       hold, rule `:minor-subject-guardian-consent-unresolved`.
    9. `:actuation/deliver-image-set` engagement-1 AGAIN -- engagement-1
       was already delivered at step 4 -- HARD hold, rule
       `:already-delivered`.

  Returns the seeded `db` (a `photo.store/MemStore`) after the run, so
  `render` can read every value straight off it."
  []
  (let [db (store/seed-db)
        actor (op/build db)]
    (exec! actor "t1" {:op :engagement/intake :subject "engagement-1"
                       :patch {:id "engagement-1" :client-name "Sato Kenji"}})

    (exec! actor "t2" {:op :shootplan/verify :subject "engagement-1"})
    (approve! actor "t2")

    (exec! actor "t3" {:op :consent/screen :subject "engagement-1"})
    (approve! actor "t3")

    (exec! actor "t4" {:op :actuation/deliver-image-set :subject "engagement-1"})
    (approve! actor "t4")

    (exec! actor "t5" {:op :shootplan/verify :subject "engagement-2" :no-spec? true})

    (exec! actor "t6" {:op :shootplan/verify :subject "engagement-3"})
    (approve! actor "t6")
    (exec! actor "t7" {:op :actuation/deliver-image-set :subject "engagement-3"})

    (exec! actor "t8" {:op :consent/screen :subject "engagement-4"})

    (exec! actor "t9" {:op :actuation/deliver-image-set :subject "engagement-1"})

    db))

;; ----------------------------- render helpers -----------------------------

(defn- esc
  "Minimal HTML-escape -- every rendered string passes through this."
  [v]
  (-> (str v)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn- last-fact-for
  "The most recent ledger fact for `subject-id`, off the real
  subject-key field this repo's `commit-fact`/`hold-fact` records use:
  `:subject` (see `photo.operation/commit-fact` and
  `photo.governor/hold-fact`)."
  [ledger subject-id]
  (last (filter #(= subject-id (:subject %)) ledger)))

(defn- status-cell
  "[css-class label] for the last known ledger fact of a subject --
  the same cond pattern used fleet-wide."
  [fact]
  (cond
    (nil? fact)                                 ["muted" "in progress"]
    (= :committed (:t fact))                    ["ok" "committed"]
    (= :approval-granted (:t fact))              ["ok" "approval-granted"]
    (= :governor-hold (:t fact))                 ["critical" (str "HARD hold: " (str/join "," (map name (:basis fact))))]
    (= :approval-rejected (:t fact))             ["err" "approval-rejected"]
    (= :approval-requested (:t fact))            ["warn" "approval-requested"]
    :else                                        ["muted" "in progress"]))

(defn- engagements-table [db]
  (let [engagements (store/all-engagements db)]
    (str
     "<table>\n<thead><tr>\n"
     "<th>id</th><th>client</th><th>jurisdiction</th><th>subjects requiring release</th>\n"
     "<th>subjects with signed release</th><th>guardian consent unresolved?</th><th>delivered?</th><th>status</th>\n"
     "</tr></thead>\n<tbody>\n"
     (str/join
      "\n"
      (for [e engagements
            :let [ledger (store/ledger db)
                  fact (last-fact-for ledger (:id e))
                  [cls label] (status-cell fact)]]
        (str "<tr>"
             "<td><code>" (esc (:id e)) "</code></td>"
             "<td>" (esc (:client-name e)) "</td>"
             "<td>" (esc (:jurisdiction e)) "</td>"
             "<td><code>" (esc (:subjects-requiring-release e)) "</code></td>"
             "<td><code>" (esc (:subjects-with-signed-release e)) "</code></td>"
             "<td>" (if (:minor-subject-guardian-consent-unresolved? e) "<span class=\"critical\">yes</span>" "no") "</td>"
             "<td>" (if (:image-set-delivered? e) "yes" "no") "</td>"
             "<td class=\"" cls "\">" (esc label) "</td>"
             "</tr>")))
     "\n</tbody></table>")))

(defn- committed-records-table [db]
  (let [deliveries (store/delivery-history db)]
    (str
     "<table>\n<thead><tr>\n"
     "<th>record_id</th><th>kind</th><th>engagement_id</th><th>jurisdiction</th>\n"
     "</tr></thead>\n<tbody>\n"
     (str/join
      "\n"
      (for [r deliveries]
        (str "<tr>"
             "<td><code>" (esc (get r "record_id")) "</code></td>"
             "<td>" (esc (get r "kind")) "</td>"
             "<td><code>" (esc (get r "engagement_id")) "</code></td>"
             "<td>" (esc (get r "jurisdiction")) "</td>"
             "</tr>")))
     "\n</tbody></table>")))

(defn- action-gate-table
  "Static op-contract description, sourced from the real
  `photo.phase/phases` (phase 3, this actor's `default-phase`) and
  `photo.governor/high-stakes` -- not invented, just rendered."
  []
  (let [ph (get phase/phases phase/default-phase)]
    (str
     "<table>\n<thead><tr>\n"
     "<th>op</th><th>phase-" phase/default-phase " write allowed?</th><th>auto-eligible?</th><th>always escalates (high-stakes)?</th>\n"
     "</tr></thead>\n<tbody>\n"
     (str/join
      "\n"
      (for [op (sort phase/write-ops)]
        (str "<tr>"
             "<td><code>" (esc op) "</code></td>"
             "<td>" (if (contains? (:writes ph) op) "yes" "<span class=\"warn\">no</span>") "</td>"
             "<td>" (if (contains? (:auto ph) op) "<span class=\"ok\">yes</span>" "no") "</td>"
             "<td>" (if (contains? governor/high-stakes op) "<span class=\"critical\">yes</span>" "no") "</td>"
             "</tr>")))
     "\n</tbody></table>")))

(defn- audit-ledger-table [db]
  (str
   "<table>\n<thead><tr>\n"
   "<th>t</th><th>op</th><th>subject</th><th>disposition</th><th>basis / rule</th>\n"
   "</tr></thead>\n<tbody>\n"
   (str/join
    "\n"
    (for [f (store/ledger db)]
      (str "<tr>"
           "<td>" (esc (:t f)) "</td>"
           "<td><code>" (esc (:op f)) "</code></td>"
           "<td><code>" (esc (:subject f)) "</code></td>"
           "<td class=\""
           (case (:disposition f) :commit "ok" :hold "critical" "muted")
           "\">" (esc (:disposition f)) "</td>"
           "<td>" (if (seq (:basis f))
                    (str/join ", " (map (comp esc name) (:basis f)))
                    "&mdash;")
           "</td>"
           "</tr>")))
   "\n</tbody></table>"))

(def ^:private css
  "table { width: 100%; border-collapse: collapse; font-size: 14px; }
.ok { color: #137a3f; }
body { font-family: system-ui,-apple-system,sans-serif; margin: 0; color: #1a1a1a; background: #fafafa; }
header.bar { display: flex; align-items: center; gap: 12px; padding: 12px 20px; background: #fff; border-bottom: 1px solid #e5e5e5; }
th, td { text-align: left; padding: 8px 10px; border-bottom: 1px solid #f0f0f0; }
h2 { margin-top: 0; font-size: 15px; }
.warn { color: #b25c00; background: #fff8e1; padding: 2px 6px; border-radius: 4px; }
main { max-width: 980px; margin: 24px auto; padding: 0 20px; }
header.bar h1 { font-size: 18px; margin: 0; font-weight: 600; }
.muted { color: #888; font-size: 13px; }
.critical { color: #fff; background: #b3261e; padding: 2px 6px; border-radius: 4px; font-weight: 600; }
.card { background: #fff; border: 1px solid #e5e5e5; border-radius: 8px; padding: 16px; margin-bottom: 16px; }
.err { color: #b3261e; background: #fbe9e7; padding: 2px 6px; border-radius: 4px; }
th { font-weight: 600; color: #555; font-size: 12px; text-transform: uppercase; letter-spacing: 0.04em; }
header.bar .badge { margin-left: auto; font-size: 12px; color: #666; }
code { font-size: 12px; background: #f4f4f4; padding: 1px 4px; border-radius: 3px; }")

(defn render [db]
  (str
   "<!doctype html>\n"
   "<html lang=\"ja\">\n<head>\n<meta charset=\"utf-8\">\n"
   "<title>photo.render-html -- Shoot Delivery Governor operator console</title>\n"
   "<style>"
   (jp-go-dds.skin/dds+skin)
   "</style>\n"
   "</head>\n<body>\n"
   "<header class=\"bar\"><h1>Shoot Delivery Governor -- Operator Console</h1>"
   "<span class=\"badge\">ISIC 7420 &middot; phase " phase/default-phase " (" (:label (get phase/phases phase/default-phase)) ")</span>"
   "</header>\n"
   "<main>\n"
   "<div class=\"card\">\n<h2>Engagements</h2>\n" (engagements-table db) "\n</div>\n"
   "<div class=\"card\">\n<h2>Committed records (image-set-delivery drafts)</h2>\n" (committed-records-table db) "\n</div>\n"
   "<div class=\"card\">\n<h2>Action gate (photo.phase &middot; photo.governor/high-stakes)</h2>\n" (action-gate-table) "\n</div>\n"
   "<div class=\"card\">\n<h2>Audit ledger</h2>\n" (audit-ledger-table db) "\n</div>\n"
   "</main>\n"
   "</body></html>\n"))

(defn -main [& args]
  (let [out (or (first args) "docs/samples/operator-console.html")
        db (run-demo!)
        html (render db)]
    (io/make-parents out)
    (spit out html)
    (println "wrote" out)))
