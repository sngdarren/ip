package seedu.darrenbot.tasks;

/**
 * Represents a generic task with a description and completion status.
 * <p>
 * This is the base class for more specific task types such as
 * {@link Todo}, {@link Deadline}, and {@link Event}. Each task
 * has a textual description and a flag to indicate whether it
 * has been completed.
 * </p>
 *
 * <p>Example:</p>
 * <pre>
 * Task t = new Task("read book");
 * System.out.println(t);
 * // Output: [ ] read book
 *
 * t.redo();
 * System.out.println(t);
 * // Output: [X] read book
 * </pre>
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new {@code Task} with the given description.
     * <p>
     * By default, a newly created task is marked as not done.
     * </p>
     *
     * @param description the description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as not done.
     */
    public void undo() {
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void redo() {
        this.isDone = true;
    }

    /**
     * Returns the status icon of the task.
     * <p>
     * An "X" is returned if the task is done,
     * and a space (" ") otherwise.
     * </p>
     *
     * @return the status icon string
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the string representation of the task.
     * <p>
     * The format includes the status icon and the task description.
     * Example: {@code [X] read book}
     * </p>
     *
     * @return the string representation of this task
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
