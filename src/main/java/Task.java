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
    protected String by;
    public Deadline(String description, String by) { super(description); this.by = by; }
    public String toFileFormat() { return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by; }
    @Override
    public String toString() { return "[D]" + super.toString() + " (by: " + by + ")"; }
}

class Event extends Task {
    protected String from;
    protected String to;
    public Event(String description, String from, String to) {
        super(description); this.from = from; this.to = to;
    }
    public String toFileFormat() { return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to; }
    @Override
    public String toString() { return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")"; }
}