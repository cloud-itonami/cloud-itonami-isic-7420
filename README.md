# cloud-itonami-isic-7420

Open Business Blueprint for **ISIC Rev.5 7420**: Photographic
activities.

This repository publishes a photographic-studio actor -- engagement
intake, image-rights regulatory assessment, minor-subject guardian-
consent screening and final-image-set delivery -- as an OSS business
that any qualified, licensed operator can fork, deploy, run, improve
and sell, so a community or independent professional never surrenders
customer data and ledgers to a closed SaaS.

Built on this workspace's
[`langgraph-clj`](https://github.com/com-junkawasaki/langgraph-clj)
StateGraph runtime (portable `.cljc`, supervised superstep loop,
interrupts, Datomic/in-mem checkpoints) -- the same actor pattern as
every prior actor in this fleet
([`cloud-itonami-isic-6511`](https://github.com/cloud-itonami/cloud-itonami-isic-6511),
[`6512`](https://github.com/cloud-itonami/cloud-itonami-isic-6512),
[`6621`](https://github.com/cloud-itonami/cloud-itonami-isic-6621),
[`6622`](https://github.com/cloud-itonami/cloud-itonami-isic-6622),
[`6629`](https://github.com/cloud-itonami/cloud-itonami-isic-6629),
[`6520`](https://github.com/cloud-itonami/cloud-itonami-isic-6520),
[`6530`](https://github.com/cloud-itonami/cloud-itonami-isic-6530),
[`6820`](https://github.com/cloud-itonami/cloud-itonami-isic-6820),
[`6612`](https://github.com/cloud-itonami/cloud-itonami-isic-6612),
[`6492`](https://github.com/cloud-itonami/cloud-itonami-isic-6492),
[`6920`](https://github.com/cloud-itonami/cloud-itonami-isic-6920),
[`6611`](https://github.com/cloud-itonami/cloud-itonami-isic-6611),
[`7120`](https://github.com/cloud-itonami/cloud-itonami-isic-7120),
[`8620`](https://github.com/cloud-itonami/cloud-itonami-isic-8620),
[`8530`](https://github.com/cloud-itonami/cloud-itonami-isic-8530),
[`9200`](https://github.com/cloud-itonami/cloud-itonami-isic-9200),
[`7500`](https://github.com/cloud-itonami/cloud-itonami-isic-7500),
[`9603`](https://github.com/cloud-itonami/cloud-itonami-isic-9603),
[`9521`](https://github.com/cloud-itonami/cloud-itonami-isic-9521),
[`9321`](https://github.com/cloud-itonami/cloud-itonami-isic-9321),
[`8730`](https://github.com/cloud-itonami/cloud-itonami-isic-8730),
[`9102`](https://github.com/cloud-itonami/cloud-itonami-isic-9102),
[`9103`](https://github.com/cloud-itonami/cloud-itonami-isic-9103),
[`9602`](https://github.com/cloud-itonami/cloud-itonami-isic-9602),
[`9000`](https://github.com/cloud-itonami/cloud-itonami-isic-9000),
[`8890`](https://github.com/cloud-itonami/cloud-itonami-isic-8890),
[`8610`](https://github.com/cloud-itonami/cloud-itonami-isic-8610),
[`9311`](https://github.com/cloud-itonami/cloud-itonami-isic-9311),
[`8510`](https://github.com/cloud-itonami/cloud-itonami-isic-8510),
[`9412`](https://github.com/cloud-itonami/cloud-itonami-isic-9412),
[`6491`](https://github.com/cloud-itonami/cloud-itonami-isic-6491),
[`8720`](https://github.com/cloud-itonami/cloud-itonami-isic-8720),
[`8521`](https://github.com/cloud-itonami/cloud-itonami-isic-8521),
[`6619`](https://github.com/cloud-itonami/cloud-itonami-isic-6619),
[`3600`](https://github.com/cloud-itonami/cloud-itonami-isic-3600),
[`6190`](https://github.com/cloud-itonami/cloud-itonami-isic-6190),
[`3030`](https://github.com/cloud-itonami/cloud-itonami-isic-3030),
[`3830`](https://github.com/cloud-itonami/cloud-itonami-isic-3830),
[`7020`](https://github.com/cloud-itonami/cloud-itonami-isic-7020),
[`9420`](https://github.com/cloud-itonami/cloud-itonami-isic-9420),
[`9491`](https://github.com/cloud-itonami/cloud-itonami-isic-9491),
[`2610`](https://github.com/cloud-itonami/cloud-itonami-isic-2610),
[`3512`](https://github.com/cloud-itonami/cloud-itonami-isic-3512),
[`8810`](https://github.com/cloud-itonami/cloud-itonami-isic-8810),
[`8691`](https://github.com/cloud-itonami/cloud-itonami-isic-8691),
[`8569`](https://github.com/cloud-itonami/cloud-itonami-isic-8569),
[`6419`](https://github.com/cloud-itonami/cloud-itonami-isic-6419),
[`7310`](https://github.com/cloud-itonami/cloud-itonami-isic-7310),
[`7320`](https://github.com/cloud-itonami/cloud-itonami-isic-7320),
[`7210`](https://github.com/cloud-itonami/cloud-itonami-isic-7210),
[`7410`](https://github.com/cloud-itonami/cloud-itonami-isic-7410),
[`8710`](https://github.com/cloud-itonami/cloud-itonami-isic-8710),
[`8541`](https://github.com/cloud-itonami/cloud-itonami-isic-8541),
[`8690`](https://github.com/cloud-itonami/cloud-itonami-isic-8690),
[`9601`](https://github.com/cloud-itonami/cloud-itonami-isic-9601),
[`6420`](https://github.com/cloud-itonami/cloud-itonami-isic-6420)) --
here it is **PhotoOps-LLM ⊣ Shoot Delivery Governor**.

> **Why an actor layer at all?** An LLM is great at drafting an
> engagement-intake summary, normalizing records, and checking
> whether an engagement's own recorded model releases actually cover
> every photographed subject requiring one -- but it has **no notion
> of which jurisdiction's image-rights law is official, no license to
> deliver/license a real final image set, and no way to know on its
> own whether a minor subject's own guardian-consent status has
> actually stayed resolved**. Letting it deliver an image set directly
> invites fabricated regulatory citations, a commercial image set
> shipping with an un-released subject, and an unresolved guardian
> consent for a minor being quietly overlooked -- and liability, and
> right-of-publicity/privacy risk, for whoever runs it. This project
> seals the PhotoOps-LLM into a single node and wraps it with an
> independent **Shoot Delivery Governor**, a human **approval
> workflow**, and an immutable **audit ledger**.

## Scope: what this actor does and does not do

This actor covers engagement intake through image-rights regulatory
assessment, minor-subject guardian-consent screening and final-image-
set delivery. It does **not**, by itself, hold any license required
to operate as a photography studio in a given jurisdiction, and it
does not claim to. It also does not create the photographs themselves,
or judge the creative/artistic merit of a shoot -- `photo.registry/
model-release-coverage-insufficient?` is a pure set-containment
recompute against the engagement's own recorded fields, not a
creative review. Whoever deploys and operates a live instance (a
licensed photography studio) supplies any jurisdiction-specific
license, the real creative work and the real studio-management/
asset-licensing integrations, and bears that jurisdiction's liability
-- the software supplies the governed, spec-cited, audited execution
scaffold so that studio does not have to build the compliance layer
from scratch.

### Actuation

**Delivering/licensing a real final image set to a client is never
autonomous, at any phase, by construction.** Two independent layers
enforce this (`photo.governor`'s `:actuation/deliver-image-set`
high-stakes gate and `photo.phase`'s phase table, which never puts
`:actuation/deliver-image-set` in any phase's `:auto` set) -- see
`photo.phase`'s docstring and `test/photo/phase_test.cljk`'s `deliver-
image-set-never-auto-at-any-phase`. The actor may draft, check and
recommend; a human studio principal is always the one who actually
delivers an image set. Matching `leasing`'s/`underwriting`'s/
`testlab`'s/`clinic`'s/`veterinary`'s/`funeral`'s/`parksafety`'s/
`salon`'s/`entertainment`'s/`facility`'s/`consulting`'s/
`advertising`'s/`polling`'s/`research`'s/`design`'s/`sports`'s/
`alliedhealth`'s single-actuation shape, grounded directly in this
blueprint's own README text ("No automated proposal, by itself, can
complete the following without governor approval and audit evidence:
delivering/licensing a final image set to a client") -- a POSITIVE
actuation (delivering a real record), matching this fleet's majority
actuation shape (`3600`/`6190` are the fleet's two NEGATIVE-actuation
exceptions).

## The core contract

```
engagement intake + jurisdiction facts (photo.facts, spec-cited)
        |
        v
   ┌──────────────┐   proposal      ┌───────────────────────┐
   │ PhotoOps-LLM │ ─────────────▶ │ Shoot Delivery                 │  (independent system)
   │ (sealed)     │  + citations    │ Governor:                    │
   └──────────────┘                 │ spec-basis · evidence-       │
          │                 commit ◀┼ incomplete · model-release-    │
          │                         │ coverage-insufficient (set-      │
    record + ledger        escalate ┼ containment) · minor-subject-      │
          │              (ALWAYS for│ guardian-consent-unresolved          │
          │               :actuation│ (unconditional) · already-             │
          │               /deliver- │ delivered                                │
          ▼               image-set)└───────────────────────┘
      human approval
```

**The PhotoOps-LLM never delivers an image set the Shoot Delivery
Governor would reject, and never does so without a human sign-off.**
Hard violations (fabricated regulatory requirements; unsupported
evidence; insufficient model-release coverage; an unresolved minor-
subject guardian-consent status; a double delivery) force **hold**
and *cannot* be approved past; a clean delivery proposal still always
routes to a human.

## Run

```bash
clojure -M:dev:run     # walk one clean single-actuation lifecycle + four HARD-hold cases through the actor
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot
performs the physical domain work**. Here a studio-lighting/capture
robot assists physical shoot setup under the actor, gated by the
independent **Shoot Delivery Governor**. The governor never dispatches
hardware itself; `:high`/`:safety-critical` actions require human
sign-off.

## Open business

This repository is not only source code. It is a public, forkable
business model:

| Layer | What is open |
|---|---|
| OSS core | Actor runtime, Shoot Delivery Governor, image-set-delivery draft records, audit ledger |
| Business blueprint | Customer, offer, pricing, unit economics, sales motion |
| Operator playbook | How to fork, license, deploy and support the service in a jurisdiction |
| Trust controls | Governance, security reporting, actuation invariant, audit requirements |

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md) to start this as an
open business on itonami.cloud, and
[`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) for the
full architecture and decision record.

## Capability layer

This blueprint resolves its technology stack via
[`kotoba-lang/industry`](https://github.com/kotoba-lang/industry) (ISIC
`7420`). This vertical's engagement records are practice-specific
rather than a shared cross-operator data contract, so `photo.*` runs
on the generic robotics/identity/forms/dmn/bpmn/audit-ledger stack
only -- no bespoke domain capability lib to reference at all.

## Layout

| File | Role |
|---|---|
| `src/photo/store.cljk` | **Store** protocol -- `MemStore` ‖ `DatomicStore` (`langchain.db`) + append-only audit ledger + image-set-delivery history. No dynamically-filed sub-record -- the actuation op acts directly on a pre-seeded engagement, and the double-actuation guard checks a dedicated `:image-set-delivered?` boolean rather than a `:status` value |
| `src/photo/registry.cljk` | Image-set-delivery draft records, plus `model-release-coverage-insufficient?` -- the SEVENTH instance of this fleet's set-containment/subset check family (`registrar`/`casework`/`secondary` established the first three "sufficiency" instances, `consulting`/`congregation`/`design` the fourth through sixth "permission/boundary" instances), the FOURTH "sufficiency" polarity instance |
| `src/photo/facts.cljk` | Per-jurisdiction image-rights catalog with an official spec-basis citation per entry, honest coverage reporting |
| `src/photo/photoadvisor.cljk` | **PhotoOps-LLM** -- `mock-advisor` ‖ `llm-advisor`; intake/shoot-plan-verification/guardian-consent-screening/image-set-delivery proposals |
| `src/photo/governor.cljk` | **Shoot Delivery Governor** -- 3 HARD checks (spec-basis · evidence-incomplete · model-release-coverage-insufficient, ground-truth set-containment recompute · minor-subject-guardian-consent-unresolved, unconditional evaluation, a GENUINELY NEW concept, the 47th grounding of this discipline, explicitly distinct from the fleet's existing 'guardian-consent-record' evidence-checklist item) + already-delivered guard + 1 soft (confidence/actuation gate) |
| `src/photo/phase.cljk` | **Phase 0→3** -- read-only → assisted intake → assisted verify → supervised (image-set delivery always human; engagement intake is the ONLY auto-eligible op, no direct capital risk) |
| `src/photo/operation.cljk` | **OperationActor** -- langgraph-clj StateGraph |
| `src/photo/sim.cljk` | demo driver |
| `test/photo/*_test.clj` | governor contract · phase invariants · store parity · registry conformance · facts coverage |

## Business-process coverage (honest)

This actor covers engagement intake through image-rights regulatory
assessment, minor-subject guardian-consent screening and final-image-
set delivery -- the core governed lifecycle this blueprint's own
`docs/business-model.md` names as its Offer:

| Covered | Not covered (out of scope for this R0) |
|---|---|
| Engagement intake + per-jurisdiction image-rights checklisting, HARD-gated on an official spec-basis citation (`:engagement/intake`/`:shootplan/verify`) | Real studio-management/asset-licensing integration, real creative production itself (see `photo.facts`'s docstring) |
| Minor-subject guardian-consent screening, evaluated unconditionally so the screening op itself can HARD-hold on its own finding (`:consent/screen`) | Any aesthetic/creative judgment itself -- deliberately outside this actor's competence |
| Image-set delivery, HARD-gated on full evidence and the engagement's own model-release coverage, plus a double-delivery guard (`:actuation/deliver-image-set`) | |
| Immutable audit ledger for every intake/verification/screening/delivery decision | |

Extending coverage is additive: add the next gate (e.g. a usage-
rights-scope check) as its own governed op with its own HARD checks
and tests, following the SAME "an independent governor re-verifies
against the actor's own records before any real-world act" pattern
this repo's flagship op already establishes.

## Jurisdiction coverage (honest)

`photo.facts/coverage` reports how many requested jurisdictions
actually have an official spec-basis in `photo.facts/catalog` --
currently 4 seeded (JPN, USA, GBR, DEU) out of ~194 jurisdictions
worldwide. This is a starting catalog to prove the governor contract
end-to-end, not a claim of global coverage. Adding a jurisdiction is
additive: one map entry in `photo.facts/catalog`, citing a real
official source -- never fabricate a jurisdiction's requirements to
make coverage look bigger.

## Maturity

`:implemented` -- `PhotoOps-LLM` + `Shoot Delivery Governor` run as
real, tested code (see `Run` above), promoted from the originally-
published `:blueprint`-tier scaffold, modeled closely on the sixty-
two prior actors' architecture. See `docs/adr/0001-architecture.md`
for the history and design.

## License

Code and implementation templates are AGPL-3.0-or-later.
