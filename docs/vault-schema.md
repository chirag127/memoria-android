# Vault Schema

Source of truth = a private Obsidian vault (Markdown + YAML frontmatter) in your GitHub repo. High-volume folders are date-sharded (`YYYY/MM/`); bounded entity sets stay flat for `[[wikilink]]` autocomplete + dedup.

## Folder structure
```
00-Inbox/                     # below-confidence captures → human triage
01-Journal/YYYY/MM/
02-Learning/{Youtube,Articles,Books,Courses}/
03-Meetings/YYYY/MM/
04-Research/<topic>/
05-Projects/<project>/
06-People/<slug>.md           # FLAT entity notes
07-Companies/<slug>.md        # FLAT
08-Concepts/<slug>.md         # FLAT
09-Tasks/{open,done/YYYY}/
10-Health/YYYY/MM/
11-Finance/YYYY/MM/
12-Media/{audio,images,pdf}/YYYY/MM/   # Git LFS
13-Timeline/                  # generated rollups
14-Reviews/{daily,weekly,monthly,yearly}/
15-Analytics/                 # generated dashboards
90-Automation/{templates,schemas}/
99-Meta/entity-aliases.yaml   # dedup source of truth
```

## Frontmatter — base schema (every note)
```yaml
id: 20260724T0914-a3f9        # immutable ULID-ish; filename is mutable, id is the key
title: "Human title"
type: journal                 # journal|youtube|meeting|person|company|concept|task|…
created: 2026-07-24T09:14:03+05:30
modified: 2026-07-24T09:20:11+05:30
tags: [learning/rust]
source: voice                 # voice|manual|share|youtube|web|calendar|import|photo
entities: [Rust Ownership]    # flat mirror of typed entity fields (for Dataview)
links: ["[[Note A]]"]         # curated relations (backlinks are automatic)
status: active
```
Per-type extensions add fields (youtube: `channel`, `url`, `duration_sec`; journal: `mood`, `audio`; meeting: `attendees`, `decisions`; person: `aliases`, `company`, `role`).

## Example — YouTube learning note
`02-Learning/Youtube/2026/07/2026-07-24-rust-ownership-explained.md`
```markdown
---
id: 20260724T1030-b7c2
title: "Rust Ownership Explained"
type: youtube
created: 2026-07-24T10:30:00+05:30
modified: 2026-07-24T10:52:00+05:30
tags: [learning/rust]
source: youtube
channel: "[[Fireship]]"
url: https://youtube.com/watch?v=dQw4w9WgXcQ
concepts: ["[[Rust Ownership]]", "[[Borrow Checker]]"]
entities: [Rust Ownership, Borrow Checker, Fireship]
status: active
---

## Summary
Ownership makes memory safety compile-time; the [[Borrow Checker]] enforces move/borrow rules with no GC.

## Key Takeaways
- Move by default; `.clone()` to copy.
- One `&mut` XOR many `&`.
```

## Knowledge-extraction contract
AI (`EXTRACT_ENTITIES`, JSON-schema mode) returns:
```json
{ "title": "...", "type": "youtube", "tags": ["learning/rust"], "summary": "...",
  "entities": [{"kind":"concept","name":"ownership","canonical":"Rust Ownership"}],
  "tasks": [{"text":"size breaker window","assignee":null,"due":null}],
  "links": ["Rust Ownership"] }
```
`OrganizationEngine`: entities → typed frontmatter + `[[wikilinks]]`; create-or-link entity notes with dedup via `99-Meta/entity-aliases.yaml` (canonical = title, surface forms = `aliases`, one entity = one note). Routing: explicit `type` → `source` hint → capture context → `00-Inbox` below confidence.

## Conventions
- Filename: `YYYY-MM-DD-<slug>.md` for events; bare `<slug>.md` for entities.
- `id` immutable; renames/moves never break links.
- Media in `12-Media/<kind>/YYYY/MM/` (LFS), referenced by wikilink.
