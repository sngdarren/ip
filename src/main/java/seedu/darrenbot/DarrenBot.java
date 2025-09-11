package seedu.darrenbot;

import seedu.darrenbot.tasks.Task;
import seedu.darrenbot.tasks.Deadline;
import seedu.darrenbot.tasks.Event;
import seedu.darrenbot.tasks.Todo;
import seedu.darrenbot.tasks.TaskList;
import seedu.darrenbot.exception.EmptyTaskException;
import seedu.darrenbot.exception.UnexpectedCommandException;
import seedu.darrenbot.storage.Storage;
import seedu.darrenbot.parser.Parser;
import seedu.darrenbot.ui.Ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Entry point of the darren_bot application.
 * <p>
 * This class coordinates the overall workflow of the program by:
 * <ul>
 *   <li>Initializing the {@link Ui}, {@link Storage}, and {@link TaskList} components.</li>
 *   <li>Reading user input from the console.</li>
 *   <li>Parsing the input into commands using {@link Parser}.</li>
 *   <li>Executing the corresponding task operations (add, list, delete, mark/unmark, etc.).</li>
 *   <li>Persisting changes to the storage file.</li>
 * </ul>
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * $ java seedu.DarrenBot.darren_bot
 * Hello! I'm darren_bot
 * What can I do for you?
 * </pre>
 */

public class DarrenBot {


    /** Default file path where tasks are stored persistently. */
    public static final String FILE_PATH = "data/duke.txt";

    /**
     * Enum representing supported commands in the application.
     * Used for simple command parsing.
     */
    enum Command {
        BYE, LIST, MARK, UNMARK, DEADLINE, TODO, EVENT, DELETE, FIND, UNKNOWN;

        /**
         * Parses a raw input line and returns the corresponding command type.
         *
         * @param line the input line from the user
         * @return the parsed {@code Command}, or {@link #UNKNOWN} if no match
         */
        static Command fromLine(String line) {
            if (line == null || line.isBlank()) {
                return UNKNOWN;
            }
            String first = line.split(" ")[0];
            return switch (first) {
            case "bye" -> BYE;
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "deadline" -> DEADLINE;
            case "todo" -> TODO;
            case "event" -> EVENT;
            case "delete" -> DELETE;
            case "find" -> FIND;
            default -> UNKNOWN;
            };
        }
    }

    /**
     * The main entry point of the program.
     * <p>
     * Starts the user interface, loads existing tasks from storage,
     * then enters the main loop to continuously accept and process commands
     * until the {@code bye} command is entered or input ends.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(FILE_PATH);

        ui.showWelcome();
        TaskList tasks;

        try {
            storage.ensureDataFile();
            tasks = storage.load();
        } catch (IOException | UnexpectedCommandException e) {
            ui.showError("Cannot initialize storage: " + e.getMessage());
            return;
        }

        while (true) {
            String line = ui.readCommand();
            if (line == null) {
                break; // EOF
            }

            Parser.Command cmd = Parser.parseCommand(line);

            try {
                switch (cmd) {
                case BYE -> {
                    ui.showExit();
                    ui.close();
                    return;
                }
                case LIST -> {
                    if (tasks.size() == 0) {
                        ui.showEmptyList();
                    }
                    else {
                        ui.showList(tasks.all());
                    }
                }
                case MARK -> {
                    Parser.ParsedArgs args1 = Parser.parseArgs(cmd, line);
                    Task t = tasks.get(args1.getIndex());
                    t.redo();
                    storage.rewrite(tasks); // persist
                    ui.showMark(t);
                }
                case UNMARK -> {
                    Parser.ParsedArgs args1 = Parser.parseArgs(cmd, line);
                    Task t = tasks.get(args1.getIndex());
                    t.undo();
                    storage.rewrite(tasks); // persist
                    ui.showUnmark(t);
                }
                case DELETE -> {
                    Parser.ParsedArgs args1 = Parser.parseArgs(cmd, line);
                    Task removed = tasks.remove(args1.getIndex());
                    storage.rewrite(tasks); // persist
                    ui.showRemoved(removed, tasks.size());
                }
                case TODO -> {
                    Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                    Todo todo = new Todo(a.getDesc());
                    tasks.add(todo);
                    // append exactly one line
                    storage.appendLine("todo | 0 | " + a.getDesc());
                    ui.showAdded(todo, tasks.size());
                }
                case DEADLINE -> {
                    Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                    Deadline d = new Deadline(a.getDesc(), a.getBy());
                    tasks.add(d);
                    storage.appendLine("deadline | 0 | " + a.getDesc() + " | " + a.getBy());
                    ui.showAdded(d, tasks.size());
                }
                case EVENT -> {
                    Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                    Event e = new Event(a.getDesc(), a.getFrom(), a.getTo());
                    tasks.add(e);
                    storage.appendLine("event | 0 | " + a.getDesc() + " | " + a.getFrom() + " | " + a.getTo());
                    ui.showAdded(e, tasks.size());
                }
                case FIND -> {
                    Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                    TaskList foundTask = new TaskList(new ArrayList<Task>());
                    for (Task t : tasks.all()) {
                        String[] keywords = t.toString().trim().split("\\s+");
                        if (Arrays.asList(keywords).contains(a.getFindKeyword().strip())) {
                            foundTask.add(t);
                        }
                    }
                    if (foundTask.size() != 0) {
                        ui.showList(foundTask.all());
                    } else {
                        System.out.println("Cant find matching keywords");
                    }

                }
                case UNKNOWN -> {
                    throw new UnexpectedCommandException("OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
                default -> {
                    assert false : "This should never be reached because UNKOWN already handles it";
                }
                }
            } catch (EmptyTaskException e) {
                ui.showError(e.getMessage());
            }
            catch (IndexOutOfBoundsException e) {
                ui.showError("OOPS!!! Index out of bounds.");
            } catch (UnexpectedCommandException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Cannot write to data file: " + e.getMessage());
            }
        }
        ui.close();
    }

    public String getResponse(String input) {
        return "DarrenBot heard: " + input;
    }

}
