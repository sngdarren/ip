# AI Usage Log

This document tracks the use of AI tools during the development of this project.  
It records which tools were used, for what purpose, and key observations about their usefulness.

---

## Week of 2025-09-15

### Tools Used
- **ChatGPT (OpenAI GPT-5)**
    - Helped debug parser logic (`parseCommand` missing `list`/`bye` cases).
    - Suggested SLAP-compliant refactors for `UPDATE` command handling.
    - Drafted Javadoc comments for several methods (`updateEvent`, `showList`).

### Observations
- **What worked:**
    - Very effective for spotting missing enum cases and suggesting robust string parsing (`.trim().split("\\s+")`).
    - Javadoc generation saved ~15–20 minutes per method.
    - SLAP guidance helped keep code clean and modular.
- **What didn’t:**
    - Sometimes suggestions were overly detailed (needed trimming down).
    - Needed manual tweaks to match our coding standards (e.g., constant naming).
- **Time saved:** ~2–3 hours this week.

---

## Week of 2025-09-08

### Tools Used
- **ChatGPT (OpenAI GPT-5)**
    - Explained differences between `git revert` and `git reset` when undoing an accidental merge.
    - Provided commit message guidelines to avoid standard violations.

### Observations
- **What worked:**
    - Clear explanations of safe vs destructive Git commands.
    - Helpful commit message templates that are easy to follow.
- **What didn’t:**
    - Some Git commands were Linux-centric; needed small tweaks for macOS environment.
- **Time saved:** ~1–2 hours.

---

## Notes
- File will be updated weekly alongside `CONTRIBUTORS.md`.
- Purpose: transparency on AI tool usage, and reflection on effectiveness.
