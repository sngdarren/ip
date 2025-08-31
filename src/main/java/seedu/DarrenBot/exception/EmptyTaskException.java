package seedu.DarrenBot.exception;

/**
 * Exception thrown when a task is created without a valid description.
 * <p>
 * This is used to indicate that the user attempted to add a {@code todo}
 */
public class EmptyTaskException extends Exception {
    public EmptyTaskException(String taskType) {
        super("OOPS!!! The description of a " + taskType + " cannot be empty.");
    }
}
