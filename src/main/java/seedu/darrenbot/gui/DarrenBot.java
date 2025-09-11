package seedu.darrenbot.gui;

import java.io.IOException;
import java.util.Scanner;

import seedu.darrenbot.exception.EmptyTaskException;
import seedu.darrenbot.exception.UnexpectedCommandException;
import seedu.darrenbot.parser.Parser;
import seedu.darrenbot.storage.Storage;
import seedu.darrenbot.tasks.Deadline;
import seedu.darrenbot.tasks.Event;
import seedu.darrenbot.tasks.Task;
import seedu.darrenbot.tasks.TaskList;
import seedu.darrenbot.tasks.Todo;
import seedu.darrenbot.ui.Ui;

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

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Constructs DarrenBot Object.
     */
    public DarrenBot() {
        this.ui = new Ui();
        this.storage = new Storage(FILE_PATH);
        TaskList loaded;
        try {
            this.storage.ensureDataFile();
            loaded = this.storage.load();
        } catch (IOException | UnexpectedCommandException e) {
            // If load fails, start with an empty list but keep the error visible in responses
            loaded = new TaskList(new java.util.ArrayList<>());
            // Optional: stash an initialization error message if you want to surface it
            // or log it. For now we just proceed with an empty list.
        }
        this.tasks = loaded;
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
        TaskList tasks;

        try {
            storage.ensureDataFile();
            tasks = storage.load();
        } catch (IOException | UnexpectedCommandException e) {
            ui.showError("Cannot initialize storage: " + e.getMessage());
            return;
        }

        DarrenBot bot = new DarrenBot();

        ui.showWelcome();
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String response = bot.getResponse(line);
            System.out.println(response);
            if (line.equals("bye")) {
                break;
            }
        }
        sc.close();
    }


    @SuppressWarnings("checkstyle:Indentation")
    public String getResponse(String line) {
        try {
            Parser.Command cmd = Parser.parseCommand(line);
            switch (cmd) {
            case BYE -> {
                return "Bye. Hope to see you again soon!";
            }
            case LIST -> {
                if (tasks.size() == 0) {
                    return "Your task list is empty.";
                }
                return ui.formatList(tasks.all()); // <-- change ui.showList to a string-returning method
            }
            case TODO -> {
                Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                Todo todo = new Todo(a.getDesc());
                tasks.add(todo);
                storage.appendLine("todo | 0 | " + a.getDesc());
                return "Added todo: " + todo;
            }
            case MARK -> {
                Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                Task t = tasks.get(a.index);
                t.redo();
                storage.rewrite(tasks);
                return "Nice! I’ve marked this task as done:\n  " + t;
            }

            case UNMARK -> {
                Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                Task t = tasks.get(a.index);
                t.undo();
                storage.rewrite(tasks);
                return "OK, I’ve marked this task as not done yet:\n  " + t;
            }

            case DELETE -> {
                Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                Task removed = tasks.remove(a.index);
                storage.rewrite(tasks);
                final String s = "Noted. I’ve removed this task:\n  " + removed
                        + "\nNow you have " + tasks.size() + " tasks in the list.";
                return s;
            }

            case DEADLINE -> {
                Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                Deadline d = new Deadline(a.getDesc(), a.getBy());
                tasks.add(d);
                storage.appendLine("deadline | 0 | " + a.getDesc() + " | " + a.getBy());
                final String s = "Got it. I’ve added this task:\n  " + d
                        + "\n Now you have " + tasks.size() + " tasks in the list.";
                return s;
            }

            case EVENT -> {
                Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                Event e = new Event(a.getDesc(), a.getFrom(), a.getTo());
                tasks.add(e);
                storage.appendLine("event | 0 | " + a.getDesc() + " | " + a.getFrom() + " | " + a.getTo());
                return "Got it. I’ve added this task:\n  " + e
                        + "\nNow you have " + tasks.size() + " tasks in the list.";
            }

            case FIND -> {
                Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                TaskList found = new TaskList(new java.util.ArrayList<>());
                for (Task t : tasks.all()) {
                    // simple contains; refine as needed
                    if (t.toString().toLowerCase().contains(a.findKeyword.toLowerCase().trim())) {
                        found.add(t);
                    }
                }
                return found.size() == 0
                        ? "I couldn’t find any matching tasks."
                        : "Here are the matching tasks in your list:\n" + ui.formatList(found.all());
            }

            // … same pattern for DEADLINE, EVENT, DELETE, MARK, UNMARK, FIND
            case UNKNOWN -> {
                throw new UnexpectedCommandException("OOPS!!! I don't know what that means :-(");
            }

            default -> {
                assert false : "Unhandled command: " + cmd;
                throw new UnexpectedCommandException("OOPS!!! I don't know what that means :-(");
            }
            }
        } catch (UnexpectedCommandException | EmptyTaskException | IOException e) {
            return "Error: " + e.getMessage();
        }
    }


}
