(ns photo.governor-contract-test
  "The governor contract as executable tests -- the photo-studio
  analog of `cloud-itonami-isic-6512`'s `casualty.governor-contract-
  test`. The single invariant under test:

    PhotoOps-LLM never delivers an image set the Shoot Delivery
    Governor would reject, `:actuation/deliver-image-set` NEVER auto-
    commits at any phase, `:engagement/intake` (no direct capital
    risk) MAY auto-commit when clean, and every decision (commit OR
    hold) leaves exactly one ledger fact."
  (:require [clojure.test :refer [deftest is testing]]
            [langgraph.graph :as g]
            [photo.store :as store]
            [photo.operation :as op]))

(defn- fresh []
  (let [db (store/seed-db)]
    [db (op/build db)]))

(def operator {:actor-id "op-1" :actor-role :studio-principal :phase 3})

(defn- exec-op [actor tid request context]
  (g/run* actor {:request request :context context} {:thread-id tid}))

(defn- approve! [actor tid]
  (g/run* actor {:approval {:status :approved :by "op-1"}} {:thread-id tid :resume? true}))

(defn- verify!
  "Walks `subject` through verify -> approve, leaving a shoot-plan
  assessment on file. Uses distinct thread-ids per call site by
  suffixing `tid-prefix`."
  [actor tid-prefix subject]
  (exec-op actor (str tid-prefix "-verify") {:op :shootplan/verify :subject subject} operator)
  (approve! actor (str tid-prefix "-verify")))

(deftest clean-intake-auto-commits
  (let [[db actor] (fresh)
        res (exec-op actor "t1"
                  {:op :engagement/intake :subject "engagement-1"
                   :patch {:id "engagement-1" :client-name "Sato Kenji"}} operator)]
    (is (= :commit (get-in res [:state :disposition])))
    (is (= "Sato Kenji" (:client-name (store/engagement db "engagement-1"))) "SSoT actually updated")
    (is (= 1 (count (store/ledger db))))))

(deftest shootplan-verify-always-needs-approval
  (testing "verify is never in any phase's :auto set -- always human approval, even when clean"
    (let [[db actor] (fresh)
          res (exec-op actor "t2" {:op :shootplan/verify :subject "engagement-1"} operator)]
      (is (= :interrupted (:status res)))
      (let [r2 (approve! actor "t2")]
        (is (= :commit (get-in r2 [:state :disposition])))
        (is (some? (store/shootplan-of db "engagement-1")))))))

(deftest fabricated-jurisdiction-is-held
  (testing "a shootplan/verify proposal with no official spec-basis -> HOLD, never reaches a human"
    (let [[db actor] (fresh)
          res (exec-op actor "t3"
                    {:op :shootplan/verify :subject "engagement-1" :no-spec? true} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:no-spec-basis} (-> (store/ledger db) first :basis)))
      (is (nil? (store/shootplan-of db "engagement-1")) "no shoot-plan written"))))

(deftest deliver-image-set-without-shootplan-is-held
  (testing "actuation/deliver-image-set before any shoot-plan verification -> HOLD (evidence incomplete)"
    (let [[db actor] (fresh)
          res (exec-op actor "t4" {:op :actuation/deliver-image-set :subject "engagement-1"} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:evidence-incomplete} (-> (store/ledger db) first :basis))))))

(deftest model-release-coverage-insufficient-is-held
  (testing "an engagement whose own model-release coverage doesn't cover all required subjects -> HOLD"
    (let [[db actor] (fresh)
          _ (verify! actor "t5pre" "engagement-3")
          res (exec-op actor "t5" {:op :actuation/deliver-image-set :subject "engagement-3"} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:model-release-coverage-insufficient} (-> (store/ledger db) last :basis)))
      (is (empty? (store/delivery-history db))))))

(deftest minor-subject-guardian-consent-unresolved-is-held-and-unoverridable
  (testing "an unresolved minor-subject guardian-consent status on an engagement -> HOLD, and never reaches request-approval -- exercised via :consent/screen DIRECTLY, not via the actuation op against an unscreened engagement (see this actor's governor ns docstring / parksafety's ADR-2607071922 Decision 5 / eldercare's, museum's, conservation's, salon's, entertainment's, casework's, hospital's, facility's, school's, association's, leasing's, behavioral's, secondary's, card's, water's, telecom's, aerospace's, recovery's, consulting's, union's, congregation's, fab's, energy's, care's, navigator's, learning's, banking's, advertising's, polling's, research's, design's, nursing's, sports's, alliedhealth's, laundry's and holdco's ADR-0001s)"
    (let [[db actor] (fresh)
          res (exec-op actor "t6" {:op :consent/screen :subject "engagement-4"} operator)]
      (is (= :hold (get-in res [:state :disposition])) "settles immediately, no interrupt")
      (is (not= :interrupted (:status res)))
      (is (some #{:minor-subject-guardian-consent-unresolved} (-> (store/ledger db) first :basis)))
      (is (nil? (store/consent-of db "engagement-4")) "no clearance written"))))

(deftest deliver-image-set-always-escalates-then-human-decides
  (testing "a clean, fully-assessed engagement still ALWAYS interrupts for human approval -- actuation/deliver-image-set is never auto"
    (let [[db actor] (fresh)
          _ (verify! actor "t7pre" "engagement-1")
          r1 (exec-op actor "t7" {:op :actuation/deliver-image-set :subject "engagement-1"} operator)]
      (is (= :interrupted (:status r1)) "pauses for human approval even when governor-clean")
      (testing "approve -> commit, delivery record drafted"
        (let [r2 (approve! actor "t7")]
          (is (= :commit (get-in r2 [:state :disposition])))
          (is (true? (:image-set-delivered? (store/engagement db "engagement-1"))))
          (is (= 1 (count (store/delivery-history db))) "one draft delivery record"))))))

(deftest deliver-image-set-double-delivery-is-held
  (testing "delivering the same engagement's image set twice -> HOLD on the second attempt"
    (let [[db actor] (fresh)
          _ (verify! actor "t8pre" "engagement-1")
          _ (exec-op actor "t8a" {:op :actuation/deliver-image-set :subject "engagement-1"} operator)
          _ (approve! actor "t8a")
          res (exec-op actor "t8" {:op :actuation/deliver-image-set :subject "engagement-1"} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:already-delivered} (-> (store/ledger db) last :basis)))
      (is (= 1 (count (store/delivery-history db))) "still only the one earlier delivery"))))

(deftest every-decision-leaves-one-ledger-fact
  (testing "write-only-through-ledger: N operations -> N ledger facts"
    (let [[db actor] (fresh)]
      (exec-op actor "a" {:op :engagement/intake :subject "engagement-1"
                          :patch {:id "engagement-1" :client-name "Sato Kenji"}} operator)
      (exec-op actor "b" {:op :shootplan/verify :subject "engagement-1" :no-spec? true} operator)
      (is (= 2 (count (store/ledger db)))
          "one commit + one hold, both recorded"))))
