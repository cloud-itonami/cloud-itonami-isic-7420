# Business Model: Photographic activities

## Classification

- Repository: `cloud-itonami-isic-7420`
- ISIC Rev.5: `7420`
- Activity: photographic activities -- portrait, commercial and event photography, film processing, and related services
- Social impact: professional standards, data sovereignty, transparent audit

## Customer

- independent photography studios
- cooperative photographer collectives
- community photo-service programs

## Offer

- booking intake
- shoot-plan proposal
- final-image-delivery proposal
- immutable audit ledger

## Revenue

- self-host setup: one-time implementation fee
- managed hosting: monthly subscription per studio
- support: monthly retainer with SLA
- migration: import from an incumbent studio-management system
- per-shoot fee

## Trust Controls

- no final image set is delivered/licensed to a client without human sign-off
- a consent/model-release gap forces a hold, not an override
- every delivery path is auditable
- emergency manual override paths remain outside LLM control
- insufficient model-release coverage for a photographed subject, or an
  unresolved minor-subject guardian-consent status, forces a hold, not
  an override
- image-set delivery is logged and escalated, and cannot be finalized
  twice for the same engagement: a double-delivery attempt is held off
  this actor's own engagement facts alone, with no upstream comparison
  needed

## Shoot Delivery Governor: decision rule

`blueprint.edn` fixes `:itonami.blueprint/governor` to `:shoot-
delivery-governor` -- this is not a generic "review step," it is the
one gate the ONE real-world act this business performs (delivering/
licensing a final image set to a client) must pass. The governor sits
between the PhotoOps-LLM and execution, per the README's Core
Contract:

```text
PhotoOps-LLM -> Shoot Delivery Governor -> hold, proceed, or human approval
```

**Approves**: routine photographic actions proposed against an
engagement that already has a consented shoot plan on file, model-
release coverage for every subject requiring one, and no unresolved
minor-subject guardian-consent status. These proceed straight to the
engagement ledger.

**Rejects or escalates**: the governor refuses to let the advisor
deliver an image set on its own authority when any of the following
hold -- a fabricated jurisdiction spec-basis; incomplete evidence;
insufficient model-release coverage; an unresolved minor-subject
guardian-consent status. A clean delivery proposal still always
routes to a human -- `:actuation/deliver-image-set` is never auto-
committed, at any rollout phase.
