# Memoria

[⭐ Star this Repo ⭐](https://github.com/chirag127/memoria-android)

**Memoria is a Life Memory Operating System for Android** — a personal memory, life-capture, journaling, and knowledge-extraction system that transforms your activity into an organized, searchable knowledge vault with minimal manual typing.

Not a journaling app. Not a note-taking app. A life memory OS.

## Source of truth

Your **private GitHub repo of Markdown + YAML frontmatter** (an Obsidian vault). No proprietary storage, no vendor lock-in. The app writes markdown and commits+pushes directly (JGit, on-device). Markdown is canonical; Git is canonical.

## How it works

```
capture (voice / share / quick-note / photo) 
   → AI enrich (summarize + extract entities, tags, tasks)
   → OrganizationEngine routes to the right vault folder + note type
   → VaultWriter renders Obsidian markdown (frontmatter + [[wikilinks]])
   → JGit commits locally, GitSyncWorker pulls --rebase + pushes to your repo
```

## Principles

- **Minimal typing** — voice, share sheet, and automation first.
- **Local-first, private** — captures write locally, sync is eventual; AI runs on-device calling providers directly (BYOK, free-first: Pollinations → OpenRouter-free → Ollama → paid).
- **Realistic on Android** — MVP uses only sanctioned, zero-special-access captures. Policy-restricted sources (notification/accessibility) ship only in the `full` (F-Droid/sideload) flavor.
- **Yours forever** — MIT-licensed, git-native vault outlives the app.

## Build

```bash
./gradlew assembleDebug        # build
./gradlew testDebugUnitTest    # unit tests
./gradlew ktlintCheck detekt   # static analysis
```

Requires JDK 17, Android SDK (compileSdk 35). Convention plugins live in `build-logic/`.

## Docs

- [`ARCHITECTURE.md`](ARCHITECTURE.md) — modules, JGit pipeline, DI
- [`docs/vault-schema.md`](docs/vault-schema.md) — folder structure + frontmatter
- [`docs/ai-providers.md`](docs/ai-providers.md) — provider abstraction + failover
- [`docs/permission-matrix.md`](docs/permission-matrix.md) — Android permissions + Play-policy risk
- [`ROADMAP.md`](ROADMAP.md) — MVP → V3 → 10-year

## License

MIT © 2026 chirag127
