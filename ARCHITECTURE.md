# Architecture

## Modules (downward deps: `app → feature → domain ← data`)

`:domain` is pure `kotlin/jvm` (no Android — compile-enforced by `memoria.jvm.library`). Convention plugins in `build-logic/`; versions in `gradle/libs.versions.toml`.

| Module | Type | Responsibility |
|---|---|---|
| `:app` | application | DI wiring, nav host, `MemoriaApplication` (HiltWorkerFactory), 2 flavors |
| `:core:common` | jvm | Result, dispatchers, DI qualifiers, clock |
| `:core:model` | jvm | shared value types |
| `:core:ui` | android-lib | M3 theme, shared composables |
| `:core:testing` | android-lib | fakes, Turbine/MockK helpers |
| `:core:database` | android-lib | Room (cache) + FTS |
| `:core:datastore` | android-lib | settings + sync cursor |
| `:core:security` | android-lib | Keystore wrapper, PAT/key storage |
| `:domain` | jvm | entities, repo interfaces, use cases |
| `:data:vault` | android-lib | `VaultWriter`, `MarkdownSerializer`, `OrganizationEngine` |
| `:data:git` | android-lib | `JGitVaultEngine`, `CommitQueue`, `GitSyncWorker`, `ConflictResolver` |
| `:data:ai` | android-lib | `LlmProvider`, `OpenAiCompatProvider`, `AiRouter` |
| `:data:repository` | android-lib | repo impls wiring vault+git+ai+room+datastore |
| `:feature:{capture,timeline,search,settings}` | android-lib | presentation (Compose + VM) |

## Vault-write pipeline (local-first, sync-eventual)

1. **Capture** (no network): `VaultWriter.write()` renders markdown to a date-sharded path (source of truth written now, atomic temp+rename) → Room upsert (UI cache) → `CommitQueue.enqueue` → return immediately.
2. **`GitSyncWorker`** (`@HiltWorker`, WorkManager, `NetworkType.CONNECTED`, exponential backoff): drain queue → `stageAndCommit` each (offline-safe) → `pull --rebase` → `push` with Keystore token.
3. **Conflicts:** per-memory timestamped/uuid filenames make true conflicts rare. Non-fast-forward → rebase. Content conflict → last-writer-wins + `.conflict.md` sidecar (never drop). Bad/expired token → `SyncResult.AuthFailed` → route to Settings.

## Room-as-cache boundary

Git vault markdown = **source of truth**; Room = disposable, rebuildable cache (indexed columns + FTS + embeddings). DataStore holds settings + last-pushed SHA. `VaultIndexer` rebuilds Room from the vault after clone/pull; a crash between vault-write and Room-upsert self-heals on next index.

## DI

One Hilt module per Gradle module (mostly `SingletonComponent`). `:domain` has no Hilt module (use cases are `@Inject` constructor). `GitSyncWorker` via `@HiltWorker` + `HiltWorkerFactory` in `:app`.

## Product flavors

- `play` — green/yellow captures only; clean Play submission.
- `full` — adds restricted services (NotificationListener/Accessibility/always-on-location) in `src/full/` only; F-Droid/sideload.
