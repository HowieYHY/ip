import java.time.LocalDateTime;
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


