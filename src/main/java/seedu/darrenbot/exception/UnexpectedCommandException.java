package seedu.darrenbot.exception;


/**
 * Exception thrown when the user enters a command that the program
 * does not recognize or support.
 * <p>
 * This is typically raised by the parser when it cannot map the
 * user’s input to a valid command. The provided error message
 * is passed directly to the exception for display.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * if (!isValidCommand(input)) {
 *     throw new UnexpectedCommandException(
 *         "OOPS!!! I'm sorry, but I don't know what that means :-("
 *     );
 * }
 * </pre>
 */
public class UnexpectedCommandException extends Exception {
    /**
     * Constructs a new {@code UnexpectedCommandException} with
     * the specified detail message.
     *
     * @param message the detail message explaining why the command
     *                was considered invalid
     */
    public UnexpectedCommandException(String message) {
        super(message);
    }
}
