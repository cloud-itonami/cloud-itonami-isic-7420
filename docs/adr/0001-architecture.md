# ADR-0001: PhotoOps-LLM ⊣ Shoot Delivery Governor architecture

## Status

Accepted. `cloud-itonami-isic-7420` promoted from `:blueprint` to
`:implemented` in the `kotoba-lang/industry` registry.

## Context

`cloud-itonami-isic-7420` publishes an OSS business blueprint for
photographic activities: portrait, commercial and event photography,
film processing, and related services. Like every prior actor in this
fleet, the blueprint alone is not an implementation: this ADR records
the governed-actor architecture that promotes it to real, tested code,
following the same langgraph-clj StateGraph + independent Governor +
Phase 0→3 rollout pattern established by `cloud-itonami-isic-6511`
(life insurance) and applied across sixty-two prior siblings, most
recently `cloud-itonami-isic-6420` (activities of holding companies).

## Decision

### Decision 1: single-actuation shape

This blueprint's own README, business-model.md and operator-guide.md
consistently name only ONE real-world act: "delivering/licensing a
final image set to a client." Matching `leasing`/`underwriting`/
`testlab`/`clinic`/`veterinary`/`funeral`/`parksafety`/`salon`/
`entertainment`/`facility`/`consulting`/`advertising`/`polling`/
`research`/`design`/`sports`/`alliedhealth`'s single-actuation shape,
`high-stakes` here is a one-member set, `#{:actuation/deliver-image-
set}`.

### Decision 2: entity and op shape

The primary entity is an `engagement`, matching the README's own Core
Contract language ("intake + identity + engagement records"). Four
ops: `:engagement/intake` (directory upsert, no capital risk),
`:shootplan/verify` (per-jurisdiction image-rights evidence checklist,
never auto), `:consent/screen` (minor-subject-guardian-consent
screening, unconditional-evaluation discipline, never auto), and
`:actuation/deliver-image-set` (POSITIVE, high-stakes -- delivering a
real final image set to a client).

### Decision 3: `model-release-coverage-insufficient?` -- the 7th set-containment/subset check

Following `registrar.registry`'s (1st, "sufficiency" polarity),
`casework.registry`'s (2nd), `secondary.registry`'s (3rd),
`consulting.registry`'s (4th, first "permission/boundary" polarity),
`congregation.registry`'s (5th) and `design.registry/deliverable-
scope-exceeded?`'s (6th) instances, `photo.registry/model-release-
coverage-insufficient?` recomputes `(not (set/subset? subjects-
requiring-release subjects-with-signed-release))` directly from the
engagement's own recorded fields -- the 7th instance overall, and the
FOURTH "sufficiency" polarity instance (after `registrar`/`casework`/
`secondary`): the engagement's own set of subjects requiring a model
release must be fully COVERED by its own set of subjects with a
signed release. Gates only `:actuation/deliver-image-set`.

### Decision 4: `minor-subject-guardian-consent-unresolved-violations` -- the 47th unconditional-evaluation screening grounding, a genuinely new concept

Before writing this check, every prior sibling's governor/registry
namespaces were grepped for "model-release", "guardian-consent",
"minor-subject" and "underage" -- the only hits were `learning.
facts`'s plain evidence-checklist STRING item ("guardian-consent-
record"), never a dedicated governor CHECK FUNCTION, confirming this
is a genuinely new unconditional-evaluation concept, avoiding the
false-precedent-claim risk `leasing`'s ADR-0001 documents.
`minor-subject-guardian-consent-unresolved-violations` reuses the
unconditional-evaluation DISCIPLINE (`casualty.governor/sanctions-
violations`'s original fix) for the 47th distinct application
overall, continuing the count established across this window's builds
(most recently `holdco.governor/beneficial-ownership-verification-
unresolved-violations` at 46th). Grounded directly in real image-
rights law protecting minors (e.g. Germany's KUG §22-23, US state
right-of-publicity statutes) and this blueprint's own Trust Control
"a consent/model-release gap forces a hold, not an override." Gates
`:consent/screen` and `:actuation/deliver-image-set`.

### Decision 5: dedicated double-actuation-guard boolean

`:image-set-delivered?` is a dedicated boolean on the `engagement`
record, never a single `:status` value -- the same discipline every
prior sibling governor's guards establish, informed by `cloud-
itonami-isic-6492`'s real status-lifecycle bug (ADR-2607071320).

### Decision 6: Store protocol, MemStore + DatomicStore parity

`photo.store/Store` is implemented by both `MemStore` (atom-backed,
default for dev/tests/demo) and `DatomicStore` (`langchain.db`-
backed), proven to satisfy the same contract in `test/photo/
store_contract_test.clj` -- the same seam every sibling actor uses so
swapping the SSoT backend is a configuration change, not a rewrite.
The protocol's per-entity accessor is named `engagement` directly --
not a Clojure special form, so no `-of` suffix workaround was needed.

### Decision 7: Phase 0→3 rollout

Phase 3's `:auto` set has exactly one member, `:engagement/intake`
(no capital risk). `:shootplan/verify` and `:consent/screen` are
never auto-eligible at any phase (matching every sibling's screening-
op posture), and `:actuation/deliver-image-set` is permanently
excluded from every phase's `:auto` set -- a structural fact, not a
rollout milestone, enforced by BOTH `photo.phase` and `photo.
governor`'s `high-stakes` set independently.

### Decision 8: no bespoke domain capability lib

This blueprint's own `:itonami.blueprint/required-technologies` names
no domain-specific capability beyond the generic robotics/identity/
forms/dmn/bpmn/audit-ledger stack -- there was no capability-lib
decision to make at all.

### Decision 9: mock + LLM advisor pair

`photo.photoadvisor` provides `mock-advisor` (deterministic, default
everywhere -- the actor graph and governor contract run offline) and
`llm-advisor` (backed by `langchain.model/ChatModel`, with a
defensive EDN-proposal parser so a malformed LLM response degrades to
a safe low-confidence noop rather than ever auto-delivering an image
set).

### Decision 10: no `blueprint.edn` field-sync fixes needed

Matching `advertising`/7310's, `polling`/7320's, `research`/7210's,
`design`/7410's, `nursing`/8710's, `sports`/8541's, `alliedhealth`/
8690's, `laundry`/9601's and `holdco`/6420's own experience, this
repo's `blueprint.edn` already had the correct `isic-` prefixed `:id`
and correctly populated `:required-technologies`/`:optional-
technologies` matching the `kotoba-lang/industry` registry's own
entry for `"7420"` exactly -- only the `:maturity` field itself
needed adding.

## Alternatives considered

- **A dual-actuation shape** (e.g. splitting "final delivery" and
  "licensing" into two acts). Rejected: the blueprint's own text
  consistently names only ONE real-world act; inventing a second
  would not be grounded in the blueprint's own text.
- **Reusing the existing 'conflict of interest' or 'IP-licensing-
  conflict' concepts for the model-release concern.** Rejected: model-
  release coverage is a subject-consent concern (does the photographed
  person's own consent cover this use), genuinely distinct from a
  professional's divided loyalty (conflict of interest) or a
  deliverable's own IP/licensing-clearance conflict (`design`/7410's
  concept) -- confirmed via grep to have zero prior instances under
  either name.
- **Treating guardian-consent purely as an evidence-checklist item**
  (as `learning`/8569 already does), without a dedicated unconditional
  check. Rejected: this blueprint's own Trust Control text singles out
  "a consent/model-release gap" as forcing a hold -- a dedicated,
  unconditionally-evaluated check (screenable directly via `:consent/
  screen`) more precisely matches the blueprint's own emphasis than
  folding it into the generic evidence-completeness checklist alone.

## Consequences

- Sixty-third actor in this fleet (62 implemented before this build).
- Confirms the set-containment/subset check family generalizes to a
  7th instance, and its "sufficiency" polarity to a 4th instance.
- Establishes a genuinely NEW unconditional-evaluation-screening
  concept (minor-subject-guardian-consent-unresolved), grep-verified
  absent as a dedicated check from every prior sibling before the
  claim was finalized.
- `MemStore` ‖ `DatomicStore` parity is proven by `test/photo/
  store_contract_test.clj`, the same `:db-api`-driven swap pattern
  every sibling actor uses.
- `blueprint.edn` required no field-sync fixes this time (already
  correct) -- only the `:maturity` flip itself.

## References

- `orgs/cloud-itonami/cloud-itonami-isic-7420/README.md`
- `orgs/cloud-itonami/cloud-itonami-isic-7420/docs/business-model.md`
- `orgs/kotoba-lang/industry/resources/kotoba/industry/registry.edn` (entry `"7420"`)
