import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public abstract String toFileFormat();

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
    protected LocalDate by;
    public Deadline(String description, LocalDate by) { super(description); this.by = by; }
    public LocalDate getBy() { return by; }
    public String toFileFormat() { return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by; }
    @Override
    public String toString() {
        String formattedDate = by.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        return "[D]" + super.toString() + " (by: " + formattedDate + ")";
    }
}

class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;
    public Event(String description, LocalDate from, LocalDate to) {
        super(description); this.from = from; this.to = to;
    }
    public LocalDate getFrom() { return from; }
    public LocalDate getTo() { return to; }
    public String toFileFormat() { return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to; }
    @Override
    public String toString() {
        String formattedFrom = from.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        String formattedTo = to.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        return "[E]" + super.toString() + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }
}