(ns photo.governor
  "Shoot Delivery Governor -- the independent compliance layer that
  earns the PhotoOps-LLM the right to commit. The LLM has no notion of
  jurisdictional image-rights/personal-data law, whether an
  engagement's own recorded model releases actually cover every
  photographed subject requiring one, whether a minor subject's own
  guardian-consent status has actually stayed resolved, or when an
  act stops being a draft and becomes a real-world image-set delivery,
  so this MUST be a separate system able to *reject* a proposal and
  fall back to HOLD -- the photographic-studio analog of `cloud-
  itonami-isic-8620`'s ClinicGovernor.

  Five checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them (you don't get to approve your way
  past a fabricated jurisdiction spec-basis, incomplete evidence,
  insufficient model-release coverage, an unresolved minor-subject
  guardian consent, or a double delivery). The confidence/actuation
  gate is SOFT: it asks a human to look (low confidence / actuation),
  and the human may approve -- but see `photo.phase`: for `:stake
  :actuation/deliver-image-set` (a real image-set delivery) NO phase
  ever allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the shoot-plan proposal cite
                                       an OFFICIAL source (`photo.
                                       facts`), or invent one?
    2. Evidence incomplete         -- for `:actuation/deliver-image-
                                       set`, has the engagement
                                       actually been assessed with a
                                       full client-consent-record/
                                       shoot-plan-record/model-
                                       release-verification-record/
                                       image-delivery-record evidence
                                       checklist on file?
    3. Model-release coverage
       insufficient                   -- for `:actuation/deliver-
                                       image-set`, INDEPENDENTLY
                                       recompute whether the
                                       engagement's own recorded set
                                       of subjects requiring a model
                                       release is fully covered by its
                                       own recorded set of subjects
                                       with a signed release
                                       (`photo.registry/model-
                                       release-coverage-insufficient?`)
                                       -- needs no proposal inspection
                                       at all. The SEVENTH instance of
                                       this fleet's set-containment/
                                       subset check family
                                       (`registrar`/`casework`/
                                       `secondary` established the
                                       first three 'sufficiency'
                                       instances, `consulting`/
                                       `congregation`/`design` the
                                       fourth through sixth
                                       'permission/boundary'
                                       instances), the FOURTH
                                       'sufficiency' polarity
                                       instance.
    4. Minor-subject guardian
       consent unresolved             -- reported by THIS proposal
                                       itself (a `:consent/screen`
                                       that just found an unresolved
                                       guardian-consent status), or
                                       already on file for the
                                       engagement (`:consent/screen`/
                                       `:actuation/deliver-image-
                                       set`). Evaluated
                                       UNCONDITIONALLY (not scoped to
                                       a specific op), the SAME
                                       discipline `casualty.governor/
                                       sanctions-violations`/...(forty-
                                       six prior siblings, most
                                       recently `holdco.governor/
                                       beneficial-ownership-
                                       verification-unresolved-
                                       violations`)...established -- a
                                       GENUINELY NEW concept in this
                                       fleet, grep-verified absent from
                                       every prior sibling, the 47th
                                       distinct application of this
                                       discipline overall, grounded in
                                       real image-rights law protecting
                                       minors (e.g. Germany's KUG
                                       §22-23, US state right-of-
                                       publicity statutes) and
                                       explicitly distinct from the
                                       'guardian-consent-record'
                                       evidence-checklist item this
                                       fleet already uses elsewhere (an
                                       evidence-completeness item, not
                                       a dedicated unconditional-
                                       evaluation check).
    5. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:actuation/
                                       deliver-image-set` (a REAL
                                       delivery act) -> escalate.

  One more guard, double-delivery prevention, is enforced but NOT
  listed as a numbered HARD check above because it needs no upstream
  comparison at all -- `already-delivered-violations` refuses to
  deliver an image set for the SAME engagement twice, off a dedicated
  `:image-set-delivered?` fact (never a `:status` value) -- the SAME
  'check a dedicated boolean, not status' discipline every prior
  sibling governor's guards establish, informed by `cloud-itonami-
  isic-6492`'s status-lifecycle bug (ADR-2607071320)."
  (:require [photo.facts :as facts]
            [photo.registry :as registry]
            [photo.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Delivering/licensing a real final image set to a client is the ONE
  real-world actuation event this actor performs -- a single-member
  set, matching `cloud-itonami-isic-6511`'s/`6621`'s/`6629`'s/`6612`'s/
  `6492`'s/`7120`'s/`8620`'s single-actuation shape."
  #{:actuation/deliver-image-set})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:shootplan/verify` (or `:actuation/deliver-image-set`) proposal
  with no spec-basis citation is a HARD violation -- never invent a
  jurisdiction's image-rights requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:shootplan/verify :actuation/deliver-image-set} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は肖像権基準として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:actuation/deliver-image-set`, the jurisdiction's required
  client-consent-record/shoot-plan-record/model-release-verification-
  record/image-delivery-record evidence must actually be satisfied --
  do not trust the advisor's self-reported confidence alone."
  [{:keys [op subject]} st]
  (when (= op :actuation/deliver-image-set)
    (let [e (store/engagement st subject)
          shootplan (store/shootplan-of st subject)]
      (when-not (and shootplan
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist shootplan)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(顧客同意記録/撮影計画記録/肖像使用許諾確認記録/画像納品記録等)が充足していない状態での提案"}]))))

(defn- model-release-coverage-insufficient-violations
  "For `:actuation/deliver-image-set`, INDEPENDENTLY recompute whether
  the engagement's own recorded set of subjects requiring a release is
  fully covered by its own recorded set of subjects with a signed
  release via `photo.registry/model-release-coverage-insufficient?`
  -- needs no proposal inspection at all, since its inputs are
  permanent ground-truth fields already on the engagement."
  [{:keys [op subject]} st]
  (when (= op :actuation/deliver-image-set)
    (let [e (store/engagement st subject)]
      (when (registry/model-release-coverage-insufficient? e)
        [{:rule :model-release-coverage-insufficient
          :detail (str subject " の要許諾被写体" (:subjects-requiring-release e)
                      "が許諾済み被写体" (:subjects-with-signed-release e) "で充足されていない")}]))))

(defn- minor-subject-guardian-consent-unresolved-violations
  "An unresolved minor-subject guardian-consent status -- reported by
  THIS proposal (e.g. a `:consent/screen` that itself just found an
  unresolved status), or already on file in the store for the
  engagement (`:consent/screen`/`:actuation/deliver-image-set`) -- is
  a HARD, un-overridable hold. Evaluated UNCONDITIONALLY (not scoped
  to a specific op) so the screening op itself can HARD-hold on its
  own finding."
  [{:keys [op subject]} proposal st]
  (let [hit-in-proposal? (true? (get-in proposal [:value :minor-subject-guardian-consent-unresolved?]))
        engagement-id (when (contains? #{:consent/screen :actuation/deliver-image-set} op) subject)
        hit-on-file? (and engagement-id (true? (:minor-subject-guardian-consent-unresolved? (store/consent-of st engagement-id))))]
    (when (or hit-in-proposal? hit-on-file?)
      [{:rule :minor-subject-guardian-consent-unresolved
        :detail "未成年被写体の保護者同意が未解決の状態での納品提案は進められない"}])))

(defn- already-delivered-violations
  "For `:actuation/deliver-image-set`, refuses to deliver an image set
  for the SAME engagement twice, off a dedicated `:image-set-
  delivered?` fact (never a `:status` value)."
  [{:keys [op subject]} st]
  (when (= op :actuation/deliver-image-set)
    (when (store/engagement-already-delivered? st subject)
      [{:rule :already-delivered
        :detail (str subject " は既に画像納品済み")}])))

(defn check
  "Censors a PhotoOps-LLM proposal against the governor rules. Returns
  {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (model-release-coverage-insufficient-violations request st)
                           (minor-subject-guardian-consent-unresolved-violations request proposal st)
                           (already-delivered-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
