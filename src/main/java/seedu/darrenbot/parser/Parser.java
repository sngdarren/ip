package seedu.darrenbot.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.darrenbot.exception.EmptyTaskException;

/**
 * Utility class that translates raw user input strings into structured commands
 * and arguments that the application can understand.
 * <p>
 * The {@code Parser} is responsible for:
 * <ul>
 *   <li>Identifying the command type (e.g., {@code todo}, {@code deadline}, {@code event}, etc.).</li>
 *   <li>Extracting any additional arguments required by that command,
 *       such as task descriptions, deadlines, or date ranges.</li>
 *   <li>Throwing {@link EmptyTaskException} when user input is incomplete or invalid.</li>
 * </ul>
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * Parser.Command cmd = Parser.parseCommand("deadline submit report /by 2025-09-01");
 * Parser.ParsedArgs args = Parser.parseArgs(cmd, "deadline submit report /by 2025-09-01");
 * </pre>
 */
public class Parser {

    /**
     * Parses a raw line of user input and maps it to a supported {@link Command}.
     *
     * @param line the user input line
     * @return the {@link Command} that corresponds to the input; returns
     *         {@link Command#UNKNOWN} if the input is blank or does not match any known command
     */
    public static Command parseCommand(String line) {
        if (line == null || line.isBlank()) {
            return Command.UNKNOWN;
        }
        String first = line.split(" ")[0];
        return switch (first) {
        case "bye" -> Command.BYE;
        case "list" -> Command.LIST;
        case "mark" -> Command.MARK;
        case "unmark" -> Command.UNMARK;
        case "deadline" -> Command.DEADLINE;
        case "todo" -> Command.TODO;
        case "event" -> Command.EVENT;
        case "delete" -> Command.DELETE;
        case "find" -> Command.FIND;
        default -> Command.UNKNOWN;
        };
    }

    /**
     * Extracts arguments from a user input line according to the command type.
     * <p>
     * Supports extracting:
     * <ul>
     *   <li>Index values for {@code mark}, {@code unmark}, and {@code delete}.</li>
     *   <li>Task descriptions for {@code todo}.</li>
     *   <li>Descriptions and due dates for {@code deadline}.</li>
     *   <li>Descriptions and time ranges for {@code event}.</li>
     * </ul>
     * </p>
     *
     * @param cmd  the command type previously identified
     * @param line the full user input line
     * @return a {@link ParsedArgs} instance containing extracted values
     * @throws EmptyTaskException if the input is missing required arguments
     *                            or if the deadline format is invalid
     */
    public static ParsedArgs parseArgs(Command cmd, String line) throws EmptyTaskException {
        try {
            return switch (cmd) {
            case MARK, UNMARK, DELETE -> {
                String[] value = line.split(" ");
                int idx = Integer.parseInt(value[1]); // 0-based index
                yield ParsedArgs.index(idx);
            }
            case TODO -> {
                String desc = line.substring(5).trim();
                if (desc.isEmpty()) {
                    throw new EmptyTaskException("todo");
                }
                yield ParsedArgs.todo(desc);
            }
            case DEADLINE -> {
                int byIndex = line.indexOf("/by ");
                if (byIndex < 0) {
                    throw new EmptyTaskException("deadline");
                }
                String desc = line.substring(9, byIndex).trim();
                String deadlineString = line.substring(byIndex + 4).trim();
                if (desc.isEmpty()) {
                    throw new EmptyTaskException("deadline");
                }
                LocalDate by = LocalDate.parse(deadlineString);
                yield ParsedArgs.deadline(desc, by);
            }
            case EVENT -> {
                int fromIndex = line.indexOf("/from ");
                int toIndex = line.indexOf("/to ");
                if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
                    throw new EmptyTaskException("event");
                }
                String desc = line.substring(6, fromIndex).trim();
                String from = line.substring(fromIndex + 6, toIndex).trim();
                String to = line.substring(toIndex + 4).trim();
                if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                    throw new EmptyTaskException("event");
                }
                yield ParsedArgs.event(desc, from, to);
            }
            case FIND -> {
                String kw = line.substring(5).trim(); // after "find "
                if (kw.isEmpty()) {
                    throw new EmptyTaskException("find");
                }
                yield ParsedArgs.find(kw);
            }

            default -> ParsedArgs.none();
            };
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            // Provide a uniform error message depending on the command
            throw new EmptyTaskException(switch (cmd) {
                case MARK -> "mark";
                case UNMARK -> "unmark";
                case DELETE -> "delete";
                default -> "command";
                });
        } catch (DateTimeParseException e) {
            throw new EmptyTaskException("deadline (use /by yyyy-mm-dd)");
        }
    }

    /**
     * Represents the set of supported command types that the {@code Parser} can
     * recognize from user input.
     *
     * <p>Each constant corresponds to a specific action that the bot can execute.</p>
     */
    public enum Command { BYE, LIST, MARK, UNMARK, DEADLINE, TODO, EVENT, DELETE, FIND, UNKNOWN }


    /**
     * Container class for arguments extracted from user input.
     * <p>
     * Provides factory methods to create different types of parsed arguments,
     * depending on the command.
     * </p>
     */
    public static class ParsedArgs {
        /** Task index, used for mark/unmark/delete commands. */
        private Integer index;
        /** Task description, used for todo/deadline/event. */
        private String desc;
        /** Deadline date, used for deadline. */
        private java.time.LocalDate by;
        /** Start time string, used for event. */
        private String from;
        /** End time string, used for event. */
        private String to;
        private String findKeyword;

        public Integer getIndex() { return this.index; }
        public String getDesc() { return this.desc; }
        public java.time.LocalDate getBy() { return this.by; }
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public String getFindKeyword() { return findKeyword; }
        /** Creates an empty {@link ParsedArgs} object. */
        public static ParsedArgs none() { return new ParsedArgs(); }

        /** Creates {@link ParsedArgs} with an index argument. */
        public static ParsedArgs index(int i) {
            ParsedArgs a = new ParsedArgs();
            a.index = i;
            return a;
        }

        /** Creates {@link ParsedArgs} for a todo with a description. */
        public static ParsedArgs todo(String d) {
            ParsedArgs a = new ParsedArgs();
            a.desc = d;
            return a;
        }

        /** Creates {@link ParsedArgs} for a deadline with description and date. */
        public static ParsedArgs deadline(String d, java.time.LocalDate by) {
            ParsedArgs a = new ParsedArgs();
            a.desc = d;
            a.by = by;
            return a;
        }

        /** Creates {@link ParsedArgs} for an event with description and time range. */
        public static ParsedArgs event(String d, String from, String to) {
            ParsedArgs a = new ParsedArgs();
            a.desc = d;
            a.from = from;
            a.to = to;
            return a;
        }

        /** Creates {@link ParsedArgs} for to find a keyword. */
        public static ParsedArgs find(String keyword) {
            ParsedArgs a = new ParsedArgs();
            a.findKeyword = keyword;
            return a;
        }

    }
}
