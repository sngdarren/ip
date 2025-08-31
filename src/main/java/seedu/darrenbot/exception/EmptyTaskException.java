package seedu.darrenbot.exception;

public class EmptyTaskException extends Exception {
    public EmptyTaskException(String taskType) {
        super("OOPS!!! The description of a " + taskType + " cannot be empty.");
    }
}
