# Permission Matrix (Android 15/16, targetSdk 35)

Risk: 🟢 clean · 🟡 declaration/disclosure · 🔴 policy-restricted.

| Permission | Why | Special screen | Tier | Play risk |
|---|---|---|---|---|
| `RECORD_AUDIO` | Voice | No | MVP | 🟢 |
| `INTERNET` | AI, fetch, git sync | No | MVP | 🟢 |
| `POST_NOTIFICATIONS` | FGS/status | No | MVP | 🟢 |
| `FOREGROUND_SERVICE(_MICROPHONE)` | long voice record | No | MVP | 🟢 |
| SAF `OPEN_DOCUMENT_TREE` | vault root | No | MVP | 🟢 |
| Photo Picker | image import | No | MVP | 🟢 |
| `CAMERA` | CameraX (optional) | No | V2 | 🟢 |
| `READ_CALENDAR` | calendar | No | V2 | 🟢 |
| `ACCESS_COARSE/FINE_LOCATION` | geotag | No | V2 | 🟢 |
| `PACKAGE_USAGE_STATS` | app usage | Usage access | V2 | 🟡 |
| Health Connect | health metrics | HC consent | V2 | 🟡 |
| `ACCESS_BACKGROUND_LOCATION` | continuous trail | Bg settings | V3 | 🔴 |
| `BIND_NOTIFICATION_LISTENER_SERVICE` | notification log | Notification access | V3 (full) | 🔴🔴 |
| `BIND_ACCESSIBILITY_SERVICE` | screen scrape | Accessibility | never on Play | 🔴🔴🔴 |
| `MANAGE_EXTERNAL_STORAGE` | — | All-files | never | 🔴🔴 |

## Forbidden / rejection triggers (with alternatives)
- **AccessibilityService for logging** — forbidden on Play. Use Share Sheet + QS Tile + widget.
- **NotificationListener for a general logger** — near-certain rejection. `full` flavor / F-Droid only.
- **Background clipboard polling** — blocked A10+; foreground-paste only.
- **All-files access** — rejected; use SAF + Photo Picker.
- **Always-on background location** — hard gate; on-demand geotag instead.
- **Silent background mic** — blocked; requires visible FGS `microphone`.
