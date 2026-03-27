import java.time.LocalDateTime;

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
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Gets the end date and time of the event.
     *
     * @return the LocalDateTime of the event end
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the file format representation: E | isDone | description | from | to
     *
     * @return the file format string
     */
    public String toFileFormat() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to;
    }

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


