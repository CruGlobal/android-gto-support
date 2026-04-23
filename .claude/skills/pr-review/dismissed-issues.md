## Jetbrains Compose library versions not sharing a version ref
**Pattern**: Flagging that `compose-foundation`, `compose-runtime`, and `compose-ui` (Jetbrains Compose) use separate inline version strings or independent version refs rather than sharing a single version ref.
**Reason**: Google announced these libraries may be versioned independently of each other, so keeping them as separate entries is intentional.
**Dismissed**: 2026-04-23
**Dismissed by**: Daniel Frett