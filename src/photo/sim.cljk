(ns photo.sim
  "Demo driver -- `clojure -M:dev:run`. Walks a clean engagement
  through intake -> shoot-plan verification -> guardian-consent
  screening -> image-set-delivery proposal (always escalates) ->
  human approval -> commit, then shows four HARD holds (a jurisdiction
  with no spec-basis, an engagement whose own model-release coverage
  is insufficient, an unresolved minor-subject guardian-consent status
  screened directly via `:consent/screen` [never via an actuation op
  against an unscreened engagement -- see this actor's own governor ns
  docstring / the lesson `parksafety`'s ADR-2607071922 Decision 5,
  `eldercare`'s, `museum`'s, `conservation`'s, `salon`'s,
  `entertainment`'s, `casework`'s, `hospital`'s, `facility`'s,
  `school`'s, `association`'s, `leasing`'s, `behavioral`'s,
  `secondary`'s, `card`'s, `water`'s, `telecom`'s, `aerospace`'s,
  `recovery`'s, `consulting`'s, `union`'s, `congregation`'s, `fab`'s,
  `energy`'s, `care`'s, `navigator`'s, `learning`'s, `banking`'s,
  `advertising`'s, `polling`'s, `research`'s, `design`'s, `nursing`'s,
  `sports`'s, `alliedhealth`'s, `laundry`'s and `holdco`'s ADR-0001s
  already recorded], and a double delivery of an already-processed
  engagement) that never reach a human at all, and prints the audit
  ledger + the draft image-set-delivery records."
  (:require [langgraph.graph :as g]
            [photo.store :as store]
            [photo.operation :as op]))

(def operator {:actor-id "op-1" :actor-role :studio-principal :phase 3})

(defn- exec! [actor tid request context]
  (g/run* actor {:request request :context context} {:thread-id tid}))

(defn- approve! [actor tid]
  (g/run* actor {:approval {:status :approved :by "op-1"}} {:thread-id tid :resume? true}))

(defn -main [& _]
  (let [db (store/seed-db)
        actor (op/build db)]
    (println "== engagement/intake engagement-1 (JPN, clean; release coverage sufficient, guardian consent resolved) ==")
    (println (exec! actor "t1" {:op :engagement/intake :subject "engagement-1"
                                :patch {:id "engagement-1" :client-name "Sato Kenji"}} operator))

    (println "== shootplan/verify engagement-1 (escalates -- human approves) ==")
    (println (exec! actor "t2" {:op :shootplan/verify :subject "engagement-1"} operator))
    (println (approve! actor "t2"))

    (println "== consent/screen engagement-1 (clean; escalates -- human approves) ==")
    (println (exec! actor "t3" {:op :consent/screen :subject "engagement-1"} operator))
    (println (approve! actor "t3"))

    (println "== actuation/deliver-image-set engagement-1 (always escalates -- actuation/deliver-image-set) ==")
    (let [r (exec! actor "t4" {:op :actuation/deliver-image-set :subject "engagement-1"} operator)]
      (println r)
      (println "-- human studio principal approves --")
      (println (approve! actor "t4")))

    (println "== shootplan/verify engagement-2 (no spec-basis -> HARD hold) ==")
    (println (exec! actor "t5" {:op :shootplan/verify :subject "engagement-2" :no-spec? true} operator))

    (println "== shootplan/verify engagement-3 (escalates -- human approves; sets up the model-release test) ==")
    (println (exec! actor "t6" {:op :shootplan/verify :subject "engagement-3"} operator))
    (println (approve! actor "t6"))

    (println "== actuation/deliver-image-set engagement-3 (subject-b release missing -> HARD hold) ==")
    (println (exec! actor "t7" {:op :actuation/deliver-image-set :subject "engagement-3"} operator))

    (println "== consent/screen engagement-4 (unresolved -> HARD hold, never reaches a human) ==")
    (println (exec! actor "t8" {:op :consent/screen :subject "engagement-4"} operator))

    (println "== actuation/deliver-image-set engagement-1 AGAIN (double-delivery -> HARD hold) ==")
    (println (exec! actor "t9" {:op :actuation/deliver-image-set :subject "engagement-1"} operator))

    (println "== audit ledger ==")
    (doseq [f (store/ledger db)] (println f))

    (println "== draft image-set-delivery records ==")
    (doseq [r (store/delivery-history db)] (println r))))
