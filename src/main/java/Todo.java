/**
 * Represents a simple to-do task with no deadline or time frame.
 */
class Todo extends Task {
    /**
     * Constructs a Todo with the specified description.
     *
     * @param description the description of the to-do
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the file format representation: T | isDone | description
     *
     * @return the file format string
     */
    public String toFileFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns the string representation prefixed with [T] for to-do type.
     *
     * @return the formatted string
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

