package seedu.darrenbot.tasks;

/**
 * Represents a task without any date or time attached.
 * <p>
 * A {@code Todo} is the simplest type of {@link Task}, containing
 * only a description. It is displayed with a "[T]" tag when listed.
 * </p>
 *
 * <p>Example:</p>
 * <pre>
 * Todo t = new Todo("read book");
 * System.out.println(t);
 * // Output: [T][ ] read book
 * </pre>
 */
public class Todo extends Task {
    String taskType;

    /**
     * Constructs a {@code Todo} task with the given description.
     *
     * @param description the description of the todo task
     */
    public Todo(String description) {
        super(description);
        this.taskType = "todo";
    }

    /**
     * Returns the string representation of the todo task.
     * <p>
     * The format includes:
     * <ul>
     *   <li>A "[T]" prefix to indicate it is a todo task.</li>
     *   <li>The base task string from {@link Task#toString()}.</li>
     * </ul>
     * </p>
     *
     * @return the string representation of this todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
