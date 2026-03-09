import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class representing a task in the Idecapitator system.
 * Provides common functionality for different task types (Todo, Deadline, Event).
 * Each task has a description and a completion status.
 */
abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a Task with the specified description.
     * The task is initialized as not done.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() { this.isDone = true; }

    /**
     * Unmarks this task (marks as not done).
     */
    public void unmarkAsDone() { this.isDone = false; }

    /**
     * Gets the description of this task.
     *
     * @return the task description
     */
    public String getDescription() { return description; }

    /**
     * Returns the string representation of this task in file format.
     * Used for persistence when saving tasks to file.
     * Format is specific to each task type.
     *
     * @return the file format representation of this task
     */
    public abstract String toFileFormat();

    /**
     * Formats a LocalDateTime for display.
     * If the time is 00:00 (midnight), displays only the date in MMM dd yyyy format.
     * Otherwise, displays date and time in MMM dd yyyy, h:mma format.
     *
     * @param dateTime the LocalDateTime to format
     * @return the formatted date/time string
     */
    protected String formatDateTime(LocalDateTime dateTime) {
        if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        }
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma"));
    }

    /**
     * Returns the string representation of this task for display.
     * Format: [X] description for completed tasks, [ ] description for incomplete tasks.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }
}

/**
 * Represents a simple to-do task with no deadline or time frame.
 */
class Todo extends Task {
    /**
     * Constructs a Todo with the specified description.
     *
     * @param description the description of the to-do
     */
    public Todo(String description) { super(description); }

    /**
     * Returns the file format representation: T | isDone | description
     *
     * @return the file format string
     */
    public String toFileFormat() { return "T | " + (isDone ? "1" : "0") + " | " + description; }

    /**
     * Returns the string representation prefixed with [T] for to-do type.
     *
     * @return the formatted string
     */
    @Override
    public String toString() { return "[T]" + super.toString(); }
}

/**
 * Represents a task with a deadline.
 * Stores the deadline as a LocalDateTime for precise date and time handling.
 */
class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Constructs a Deadline with the specified description and deadline date/time.
     *
     * @param description the description of the deadline task
     * @param by the deadline date and time
     */
    public Deadline(String description, LocalDateTime by) { super(description); this.by = by; }

    /**
     * Gets the deadline date and time.
     *
     * @return the LocalDateTime of the deadline
     */
    public LocalDateTime getBy() { return by; }

    /**
     * Returns the file format representation: D | isDone | description | by
     *
     * @return the file format string
     */
    public String toFileFormat() { return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by; }

    /**
     * Returns the string representation prefixed with [D] for deadline type,
     * including the formatted deadline date/time.
     *
     * @return the formatted string
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDateTime(by) + ")";
    }
}

/**
 * Represents an event task with a start and end date/time.
 * Validates that the end date/time is not before the start date/time.
 */
class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Constructs an Event with the specified description and time frame.
     * Validates that the end time is not before the start time.
     *
     * @param description the description of the event
     * @param from the start date and time of the event
     * @param to the end date and time of the event
     * @throws IllegalArgumentException if end date/time is before start date/time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) throws IllegalArgumentException {
        super(description);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date/time cannot be before start date/time");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the start date and time of the event.
     *
     * @return the LocalDateTime of the event start
     */
    public LocalDateTime getFrom() { return from; }

    /**
     * Gets the end date and time of the event.
     *
     * @return the LocalDateTime of the event end
     */
    public LocalDateTime getTo() { return to; }

    /**
     * Returns the file format representation: E | isDone | description | from | to
     *
     * @return the file format string
     */
    public String toFileFormat() { return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to; }

    /**
     * Returns the string representation prefixed with [E] for event type,
     * including the formatted start and end date/times.
     *
     * @return the formatted string
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatDateTime(from) + " to: " + formatDateTime(to) + ")";
    }
}

