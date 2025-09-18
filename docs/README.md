# DarrenBot — User Guide

---

## Build/Run

- Use Gradle to build and run the application.
- From the project root, run:
```bash
./gradlew run
```

This will compile the project and start the application.

---

## Run the App

### GUI (recommended)

- **Main class:** `seedu.darrenbot.gui.Main`
- A windowed chat interface opens.
- Type a command and press **Enter** (or click **Send**).
- Type `bye` to exit. The window will close after showing the goodbye message.

### CLI

- **Main class:** `seedu.darrenbot.gui.DarrenBot`
- You’ll see a welcome banner in the terminal.
- Type commands and press **Enter**.
- Quit with:
```text
bye
```

---

## Commands

### Summary

| Command            | Format                                      | Example                                     |
|--------------------|---------------------------------------------|---------------------------------------------|
| **list**           | `list`                                      | `list`                                      |
| **todo**           | `todo <description>`                        | `todo read book`                            |
| **deadline**       | `deadline <description> /by <yyyy-mm-dd>`   | `deadline submit report /by 2025-10-01`     |
| **event**          | `event <description> /from <start> /to <end>` | `event team sync /from 10:30 /to 11:15`   |
| **mark**           | `mark <index>`                              | `mark 0`                                    |
| **unmark**         | `unmark <index>`                            | `unmark 0`                                  |
| **delete**         | `delete <index>`                            | `delete 1`                                  |
| **find**           | `find <keyword>`                            | `find report`                               |
| **update (event)** | `update <index> <from> <to>`                | `update 2 14:00 16:00`                      |
| **bye**            | `bye`                                       | `bye`                                       |

> **Indexing:** Commands use **zero-based** indices (the first task is `0`).  
> Use the index shown in `list`.

---

### Details & Examples

#### `list`
Show all tasks.
```text
list
```

#### `todo`
Add a to-do without dates.
```text
todo buy milk
```

#### `deadline`
Add a task with a due date. Date must be `yyyy-mm-dd`.
```text
deadline cs2103t iP /by 2025-10-01
```

#### `event`
Add a task with a start and end (free-form strings).
```text
event project meeting /from 10:00 /to 11:00
```

#### `mark` / `unmark`
Mark a task done / not done.
```text
mark 0
unmark 0
```

#### `delete`
Remove a task.
```text
delete 1
```

#### `find`
Case-insensitive substring search across task text.
```text
find report
```

#### `update` (events only)
Update the **from** and **to** fields of an event.
```text
update 2 14:00 16:00
```
If the task at that index is not an event, an error is shown.

#### `bye`
Exit the app.
```text
bye
```

---

## Data & Persistence

- Data file path: **`data/duke.txt`** (created automatically on first run).
- The app saves after changes (add/delete/mark/unmark/update).
- Storage format is a simple pipe-delimited text:
```text
todo | 0 | read book
deadline | 0 | submit report | 2025-10-01
event | 0 | team sync | 10:30 | 11:15
```
- If the data file is missing or corrupted, the app starts with an **empty list**.

---


## AI Usage Log

This project maintains **`AI.md`** (in the repo root, next to `CONTRIBUTORS.md`) that records use of AI tools for increments and observations on their usefulness. Please keep it updated periodically (e.g., weekly).

---
