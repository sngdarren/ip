package seedu.darrenbot.parser;

import java.time.LocalDate;

import seedu.darrenbot.exception.EmptyTaskException;
import seedu.darrenbot.exception.UnexpectedCommandException;

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
        case "list" -> Command.LIST;
        case "bye" -> Command.BYE;
        case "mark" -> Command.MARK;
        case "unmark" -> Command.UNMARK;
        case "deadline" -> Command.DEADLINE;
        case "todo" -> Command.TODO;
        case "event" -> Command.EVENT;
        case "delete" -> Command.DELETE;
        case "find" -> Command.FIND;
        case "update" -> Command.UPDATE;
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
    public static ParsedArgs parseArgs(Command cmd, String line) throws EmptyTaskException, UnexpectedCommandException {
        return switch (cmd) {
        case MARK, UNMARK, DELETE -> parseIndexOnly(line);
        case TODO -> parseTodo(line);
        case DEADLINE -> parseDeadline(line);
        case EVENT -> parseEvent(line);
        case FIND -> parseFind(line);
        case UPDATE -> parseUpdate(line);
        default -> ParsedArgs.none();
        };
    }

    /* -------------------------
     * Per-command helpers
     * ------------------------- */

    private static ParsedArgs parseIndexOnly(String line) {
        String[] value = line.split(" ");
        int idx = Integer.parseInt(value[1]);
        return ParsedArgs.index(idx);
    }

    private static ParsedArgs parseTodo(String line) throws EmptyTaskException {
        String desc = line.substring(5).trim();
        if (desc.isEmpty()) {
            throw new EmptyTaskException("todo");
        }
        return ParsedArgs.todo(desc);
    }

    private static ParsedArgs parseDeadline(String line) throws EmptyTaskException, UnexpectedCommandException {
        int byIndex = line.indexOf("/by ");
        if (byIndex < 0) {
            throw new UnexpectedCommandException("deadline should be in the format: "
                    + "deadline <desc> /by <yyyy-mm-dd>");
        }
        String desc = line.substring(9, byIndex).trim();
        String deadlineString = line.substring(byIndex + 4).trim();
        if (desc.isEmpty()) {
            throw new EmptyTaskException("deadline");
        }

        java.time.LocalDate by;
        try {
            by = java.time.LocalDate.parse(deadlineString);
        } catch (java.time.format.DateTimeParseException e) {
            throw new UnexpectedCommandException("deadline (date must be yyyy-mm-dd)");
        }
        return ParsedArgs.deadline(desc, by);
    }

    private static ParsedArgs parseEvent(String line) throws EmptyTaskException, UnexpectedCommandException {
        int fromIndex = line.indexOf("/from ");
        int toIndex = line.indexOf("/to ");
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
            throw new UnexpectedCommandException("event should be in the format: "
                    + "event <desc> /from <from> /to <to>");
        }
        String desc = line.substring(6, fromIndex).trim();
        String from = line.substring(fromIndex + 6, toIndex).trim();
        String to = line.substring(toIndex + 4).trim();
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new EmptyTaskException("event");
        }
        return ParsedArgs.event(desc, from, to);
    }

    private static ParsedArgs parseFind(String line) throws EmptyTaskException {
        String kw = line.substring(5).trim();
        if (kw.isEmpty()) {
            throw new EmptyTaskException("find");
        }
        return ParsedArgs.find(kw);
    }

    private static ParsedArgs parseUpdate(String line) throws UnexpectedCommandException {
        String[] value = line.split(" ");
        if (value.length != 4) {
            throw new UnexpectedCommandException(
                    "Updates are only for Events, try the format 'update <index> <from> <to>"
            );
        }
        final int idx;
        try {
            idx = Integer.parseInt(value[1]);
        } catch (NumberFormatException e) {
            throw new UnexpectedCommandException("Index must be an integer.");
        }
        String from = value[2];
        String to = value[3];
        return ParsedArgs.updateEvent(idx, from, to);
    }


    /**
     * Represents the set of supported command types that the {@code Parser} can
     * recognize from user input.
     *
     * <p>Each constant corresponds to a specific action that the bot can execute.</p>
     */
    public enum Command { BYE, LIST, MARK, UNMARK, DEADLINE, TODO, EVENT, DELETE, FIND, UPDATE, UNKNOWN }


    /**
     * Container class for arguments extracted from user input.
     * <p>
     * Provides factory methods to create different types of parsed arguments,
     * depending on the command.
     * </p>
     */
    public static class ParsedArgs {
        private Integer index;
        private String desc;
        private java.time.LocalDate by;
        private String from;
        private String to;
        private String findKeyword;

        // --- Getters ---
        public Integer getIndex() {
            return this.index;
        }
        public String getDesc() {
            return this.desc;
        }
        public java.time.LocalDate getBy() {
            return this.by;
        }
        public String getFrom() {
            return this.from;
        }
        public String getTo() {
            return this.to;
        }
        public String getFindKeyword() {
            return this.findKeyword;
        }
        /** Creates an empty {@link ParsedArgs} object. */
        public static ParsedArgs none() {
            return new ParsedArgs();
        }

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

        /** Creates {@link ParsedArgs} to update an Event */
        public static ParsedArgs updateEvent(Integer index, String from, String to) {
            ParsedArgs a = new ParsedArgs();
            a.index = index;
            a.from = from;
            a.to = to;
            return a;
        }

    }
}
