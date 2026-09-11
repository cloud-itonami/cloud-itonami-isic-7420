(ns photo.registry
  "Pure-function image-set-delivery record construction -- an append-
  only photographic-studio book-of-record draft.

  Like every sibling actor's registry, there is no single
  international check-digit standard for an image-delivery reference
  number -- every studio/jurisdiction assigns its own reference
  format. This namespace does NOT invent one; it builds a
  jurisdiction-scoped sequence number and validates the record's
  required fields, the same honest, non-fabricating discipline
  `photo.facts` uses.

  `model-release-coverage-insufficient?` is the SEVENTH instance of
  this fleet's set-containment/subset check family (`registrar.
  registry/prerequisites-satisfied?`/`casework.registry/eligibility-
  criteria-unsatisfied?`/`secondary.registry/graduation-requirements-
  unsatisfied?` established the first three, all 'sufficiency'
  polarity; `consulting.registry`'s/`congregation.registry`'s/
  `design.registry/deliverable-scope-exceeded?` established the
  fourth through sixth, 'permission/boundary' polarity) -- the FOURTH
  'sufficiency' polarity instance: an engagement's own recorded set of
  photographed subjects requiring a model release must be fully
  COVERED by its own recorded set of subjects who actually signed one,
  a direct, natural mapping onto real commercial-photography practice
  (delivering a commercial image set with even one un-released subject
  is exactly the failure mode a studio must not let an advisor wave
  through).

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real studio-management system. It builds the RECORD a
  studio would keep, not the act of delivering the image set itself
  (that is `photo.operation`'s `:actuation/deliver-image-set`, always
  human-gated -- see README `Actuation`)."
  (:require [clojure.set :as set]
            [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is the
  studio's own act, not this actor's. See README `Actuation`."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn model-release-coverage-insufficient?
  "Does `engagement`'s own `:subjects-requiring-release` set fail to be
  fully covered by its own recorded `:subjects-with-signed-release`
  set? A pure ground-truth SET-CONTAINMENT check against the
  engagement's own permanent fields -- no upstream comparison needed.
  The SEVENTH instance of this fleet's set-containment/subset family
  (see ns docstring)."
  [{:keys [subjects-requiring-release subjects-with-signed-release]}]
  (not (set/subset? (set subjects-requiring-release) (set subjects-with-signed-release))))

(defn register-image-set-delivery
  "Validate + construct the IMAGE-SET-DELIVERY registration DRAFT --
  the studio's own act of delivering/licensing a real final image set
  to a client. Pure function -- does not touch any real studio-
  management system; it builds the RECORD a studio would keep.
  `photo.governor` independently re-verifies the engagement's own
  model-release coverage and blocks a double-delivery for the same
  engagement, before this is ever allowed to commit."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "image-set-delivery: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "image-set-delivery: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "image-set-delivery: sequence must be >= 0" {})))
  (let [delivery-number (str (str/upper jurisdiction) "-DEL-" (zero-pad sequence 6))
        record {"record_id" delivery-number
                "kind" "image-set-delivery-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "delivery_number" delivery-number
     "certificate" (unsigned-certificate "ImageSetDelivery" delivery-number delivery-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
