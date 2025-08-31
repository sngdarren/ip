package seedu.darrenbot.parser;

import seedu.darrenbot.exception.EmptyTaskException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Parser {

    public static Command parseCommand(String line) {
        if (line == null || line.isBlank()) return Command.UNKNOWN;
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
            default -> Command.UNKNOWN;
        };
    }

    /** Extracts arguments for commands that need them. Throws on invalid format. */
    public static ParsedArgs parseArgs(Command cmd, String line) throws EmptyTaskException {
        try {
            return switch (cmd) {
                case MARK, UNMARK, DELETE -> {
                    String[] value = line.split(" ");
                    int idx = Integer.parseInt(value[1]); // 0-based as per your display
                    yield ParsedArgs.index(idx);
                }
                case TODO -> {
                    String desc = line.substring(5).trim();
                    if (desc.isEmpty()) throw new EmptyTaskException("todo");
                    yield ParsedArgs.todo(desc);
                }
                case DEADLINE -> {
                    int byIndex = line.indexOf("/by ");
                    if (byIndex < 0) throw new EmptyTaskException("deadline");
                    String desc = line.substring(9, byIndex).trim();
                    String deadlineString = line.substring(byIndex + 4).trim();
                    if (desc.isEmpty()) throw new EmptyTaskException("deadline");
                    LocalDate by = LocalDate.parse(deadlineString);
                    yield ParsedArgs.deadline(desc, by);
                }
                case EVENT -> {
                    int fromIndex = line.indexOf("/from ");
                    int toIndex = line.indexOf("/to ");
                    if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) throw new EmptyTaskException("event");
                    String desc = line.substring(6, fromIndex).trim();
                    String from = line.substring(fromIndex + 6, toIndex).trim();
                    String to = line.substring(toIndex + 4).trim();
                    if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) throw new EmptyTaskException("event");
                    yield ParsedArgs.event(desc, from, to);
                }
                default -> ParsedArgs.none();
            };
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            // uniform message as before
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

    public enum Command { BYE, LIST, MARK, UNMARK, DEADLINE, TODO, EVENT, DELETE, UNKNOWN }

    /** Small POJO for parsed params. */
    public static class ParsedArgs {
        public Integer index; // for mark/unmark/delete
        public String desc;
        public java.time.LocalDate by;
        public String from;
        public String to;

        public static ParsedArgs none() { return new ParsedArgs(); }
        public static ParsedArgs index(int i) { ParsedArgs a = new ParsedArgs(); a.index = i; return a; }
        public static ParsedArgs todo(String d) { ParsedArgs a = new ParsedArgs(); a.desc = d; return a; }
        public static ParsedArgs deadline(String d, java.time.LocalDate by) { ParsedArgs a = new ParsedArgs(); a.desc = d; a.by = by; return a; }
        public static ParsedArgs event(String d, String from, String to) { ParsedArgs a = new ParsedArgs(); a.desc = d; a.from = from; a.to = to; return a; }
    }
}
