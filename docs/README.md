# Idecapitator User Guide

![Screenshot of Idecapitator running in terminal](https://github.com/user-attachments/assets/59363370-ebc5-4b6e-a063-af6a9ced2d07)

Idecapitator is a **command-line task manager** that helps you track todos, deadlines, and events.
It supports date and time input, keyword/date-based search, and automatically saves your tasks between sessions.

---

## Quick Start

1. Ensure you have **Java 11 or later** installed.
2. Download the latest `Idecapitator.jar` from the releases page.
3. Open a terminal and navigate to the folder containing the JAR.
4. Run the app with:
   ```
   java -jar Idecapitator.jar
   ```
5. Type a command and press Enter to interact with the app.

![Screenshot of welcome message from Idecapitator](https://github.com/user-attachments/assets/26789d4a-996b-49fc-9b0b-4503713f69f7)

---

## Command Summary

| Command | Format |
|--------|--------|
| List tasks | `list` |
| Add todo | `todo DESCRIPTION` |
| Add deadline | `deadline DESCRIPTION /by DATE [TIME]` |
| Add event | `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` |
| Mark done | `mark INDEX` |
| Unmark done | `unmark INDEX` |
| Delete task | `delete INDEX` |
| Find by keyword or date | `find KEYWORD` or `find DATE` |
| Exit | `bye` |

---

## Date & Time Format

- **Date format (input):** `yyyy-MM-dd` e.g. `2019-12-02`
- **Time format (input):** `HHmm` 24-hour e.g. `1800` for 6:00 PM
- **Date format (display):** `MMM dd yyyy` e.g. `Dec 02 2019`
- **Time format (display):** `h:mma` e.g. `6:00PM`
- Time is **optional** — if omitted, it defaults to start of day and only the date is shown.

---

## Features

### `list` — View all tasks

Shows all tasks currently saved with their index, type, status, and details.

```
list
```

Expected output:
```
    ____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 02 2019)
    3.[E][ ] project meeting (from: Jan 10 2025, 2:00PM to: Jan 10 2025, 4:00PM)
    ____________________________________________________________
```

![Screenshot of list command output](https://github.com/user-attachments/assets/4542fffa-6d7f-4837-83f0-516d05921008)

---

### `todo` — Add a Todo

Adds a simple task with no date or time attached.

```
todo DESCRIPTION
```

Example:
```
todo read book
```

Expected output:
```
    ____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
    ____________________________________________________________
```

---

### `deadline` — Add a Deadline

Adds a task with a due date, and optionally a due time.

```
deadline DESCRIPTION /by DATE
deadline DESCRIPTION /by DATE TIME
```

Examples:
```
deadline return book /by 2019-12-02
deadline submit assignment /by 2025-03-15 2359
```

Expected output:
```
    ____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Dec 02 2019)
    Now you have 2 tasks in the list.
    ____________________________________________________________
```

```
    ____________________________________________________________
    Got it. I've added this task:
      [D][ ] submit assignment (by: Mar 15 2025, 11:59PM)
    Now you have 3 tasks in the list.
    ____________________________________________________________
```

---

### `event` — Add an Event

Adds a task with a start and end date/time. The end must not be before the start.

```
event DESCRIPTION /from DATE [TIME] /to DATE [TIME]
```

Examples:
```
event project meeting /from 2025-01-10 1400 /to 2025-01-10 1600
event overseas trip /from 2025-06-01 /to 2025-06-07
```

Expected output:
```
    ____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Jan 10 2025, 2:00PM to: Jan 10 2025, 4:00PM)
    Now you have 4 tasks in the list.
    ____________________________________________________________
```

> ⚠️ If the end date/time is before the start date/time, an error will be shown:
> ```
>     CRITICAL ERROR: Event end date/time cannot be before start date/time
> ```

---

### `mark` — Mark a Task as Done

Marks the task at the given index as completed. The status changes from `[ ]` to `[X]`.

```
mark INDEX
```

Example:
```
mark 1
```

Expected output:
```
    ____________________________________________________________
    Nice! I've marked this task as done:
      [T][X] read book
    ____________________________________________________________
```

---

### `unmark` — Unmark a Task

Reverses a mark, setting the task back to not done.

```
unmark INDEX
```

Example:
```
unmark 1
```

Expected output:
```
    ____________________________________________________________
    Nice! I've unmarked this task:
      [T][ ] read book
    ____________________________________________________________
```

---

### `delete` — Delete a Task

Permanently removes the task at the given index from the list.

```
delete INDEX
```

Example:
```
delete 2
```

Expected output:
```
    ____________________________________________________________
    Noted. I've removed this task:
      [D][ ] return book (by: Dec 02 2019)
    Now you have 3 tasks in the list.
    ____________________________________________________________
```

---

### `find` — Search Tasks

Searches your task list either by **keyword** or by **date**.

#### Search by keyword
Returns all tasks whose description contains the keyword (case-insensitive).

```
find KEYWORD
```

Example:
```
find book
```

Expected output:
```
    ____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 02 2019)
    ____________________________________________________________
```

#### Search by date
If you provide a valid date in `yyyy-MM-dd` format, it returns all deadlines due on that date and all events that span or fall on that date.

```
find DATE
```

Example:
```
find 2025-01-10
```

Expected output:
```
    ____________________________________________________________
    Tasks on Jan 10 2025:
    1.[E][ ] project meeting (from: Jan 10 2025, 2:00PM to: Jan 10 2025, 4:00PM)
    ____________________________________________________________
```

---

### `bye` — Exit the App

Exits the application. All tasks are already saved automatically — no manual save needed.

```
bye
```

Expected output:
```
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________
```

---

## Data Storage

- Tasks are automatically saved to `./data/idecapitator.txt` after every change.
- The file is created automatically if it does not exist.
- You do **not** need to save manually.
- Do **not** manually edit the data file as it may corrupt your task list.

---

## Error Handling

Idecapitator will show a `CRITICAL ERROR` message for invalid commands instead of crashing. Common errors:

| Situation | Error Message |
|-----------|--------------|
| Unknown command | `Unknown command: 'xyz'` |
| Todo with no description | `A todo needs a description.` |
| Deadline missing `/by` | `Deadlines require '/by'.` |
| Event missing `/from` or `/to` | `Events need '/from' and '/to'.` |
| Invalid date format | `Invalid date format. Please use yyyy-MM-dd (e.g., 2019-12-02)` |
| Invalid time format | `Invalid time format. Use HHmm (e.g., 1800)` |
| Event end before start | `Event end date/time cannot be before start date/time` |
| No data file found on startup | `NOTICE: No existing data found. Starting fresh!` |

