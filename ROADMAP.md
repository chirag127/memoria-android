# Roadmap

## MVP (v0.1–1.0) — the spine
Sanctioned zero-special-access captures → AI enrich → JGit commit to vault.
- Captures: **Share Sheet, Voice, Quick Note**, Photo Picker + OCR, SAF file/URL.
- On-device AI (free-first BYOK), OpenAI-compat provider abstraction + failover.
- Room cache, EncryptedSharedPreferences key storage, offline commit queue.
- Ships to Play (clean) + F-Droid.

## V1 (1.x)
Search + timeline/browse UI, on-device semantic search, multi-device sync (pull/rebase/push conflict resolution), backup/restore, SQLCipher default-on, accessibility polish.

## V2 (2.x) — disclosed standard permissions
Camera/OCR, clipboard (foreground), calendar, on-demand location geotag, UsageStats (prominent-disclosure consent), Health Connect. Pluggable providers, widgets, QS tile.

## V3 (3.x) — restricted (`full` flavor / F-Droid-first)
NotificationListenerService, always-on location. AccessibilityService NEVER on Play. On-device RAG assistant over the vault, automations/rules engine.

## 10-year phases
- **P1 (yr 1–2):** rock-solid capture + vault + search.
- **P2 (yr 3–4):** automation + assistant + multi-device.
- **P3 (yr 5–7):** plugin ecosystem, desktop companion (same git vault), local-model upgrades.
- **P4 (yr 8–10):** portable open-vault format spec, interop, longevity guarantees (git-native = data outlives the app).
