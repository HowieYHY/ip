import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() { this.isDone = true; }
    public void unmarkAsDone() { this.isDone = false; }
    public String getDescription() { return description; }
    public abstract String toFileFormat();

    protected String formatDateTime(LocalDateTime dateTime) {
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        }
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma"));
    }

    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }
}

class Todo extends Task {
    public Todo(String description) { super(description); }
    public String toFileFormat() { return "T | " + (isDone ? "1" : "0") + " | " + description; }
    @Override
    public String toString() { return "[T]" + super.toString(); }
}

class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) { super(description); this.by = by; }
    public LocalDateTime getBy() { return by; }
    public String toFileFormat() { return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by; }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDateTime(by) + ")";
    }
}

class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, LocalDateTime from, LocalDateTime to) throws IllegalArgumentException {
        super(description);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date/time cannot be before start date/time");
        }
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() { return from; }
    public LocalDateTime getTo() { return to; }
    public String toFileFormat() { return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to; }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatDateTime(from) + " to: " + formatDateTime(to) + ")";
    }
}

