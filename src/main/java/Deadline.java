import java.time.LocalDateTime;

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
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Gets the deadline date and time.
     *
     * @return the LocalDateTime of the deadline
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the file format representation: D | isDone | description | by
     *
     * @return the file format string
     */
    public String toFileFormat() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by;
    }

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


