# AI Providers & Failover

All AI runs **on-device, direct to providers** (no backend). Every adapter is the
one `OpenAiCompatProvider` class, differing only by `ProviderConfig` (+ optional shim).
Keys live in Android Keystore-backed `EncryptedSharedPreferences`, resolved by alias
at request time — never in config, logs, or the repo.

## Default roster (free-first)
| # | Provider | Tier | Modalities | Notes |
|---|---|---|---|---|
| 1 | Pollinations | FREE | text, vision | keyless |
| 2 | OpenRouter `:free` | FREE | text | free routed models |
| 3 | Ollama (local) | FREE | text | offline, keyless, `localhost:11434/v1` |
| 4 | Gemini | PAID | text, audio, vision | OpenAI-compat endpoint |
| 5 | Claude | PAID | text, vision | best summarize/extract |
| 6 | OpenAI | PAID | text, audio (Whisper), embed | audio + embeddings anchor |

## Routing
`AiRouter.chainFor()` filters by capability, sorts **free-first** (`tier` then `priority`);
`preferQuality=true` inverts to the paid-quality order (Claude→Gemini→…). Tries each
until success. TODO: per-provider circuit breaker (open on 401/402), cost/latency-aware
ordering, daily budget cap.

## Task → provider
- `TRANSCRIBE` → providers with `AUDIO_TRANSCRIBE` (OpenAI Whisper, Gemini, local whisper).
- `SUMMARIZE`/`TITLE` → any TEXT provider, free-first.
- `EXTRACT_ENTITIES` → TEXT + JSON-schema (`response_format`); prefers Claude/Gemini/OpenAI, Ollama fallback with JSON repair.
- `EMBED` → OpenAI or local (`nomic-embed`).
