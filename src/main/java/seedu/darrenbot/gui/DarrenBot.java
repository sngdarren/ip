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

    public String getResponse(String line) {
        try {
            Parser.Command cmd = Parser.parseCommand(line);
            return switch (cmd) {
            case BYE -> handleBye();
            case LIST -> handleList();
            case TODO -> handleTodo(line);
            case MARK -> handleMark(line);
            case UNMARK -> handleUnmark(line);
            case DELETE -> handleDelete(line);
            case DEADLINE -> handleDeadline(line);
            case EVENT -> handleEvent(line);
            case FIND -> handleFind(line);
            case UPDATE -> handleUpdate(line);
            case UNKNOWN -> throwUnknown();
            default -> handleUnhandled(cmd);
            };
        } catch (UnexpectedCommandException | EmptyTaskException | IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    /* =========================
     * Command Handlers (1 level)
     * ========================= */

    private String handleBye() {
        return "Bye. Hope to see you again soon!";
    }

    private String handleList() {
        if (tasks.size() == 0) {
            return "Your task list is empty.";
        }
        return ui.formatList(tasks.all());
    }

    private String handleTodo(String line) throws EmptyTaskException, IOException, UnexpectedCommandException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.TODO, line);
        Todo todo = new Todo(a.getDesc());
        tasks.add(todo);
        storage.appendLine("todo | 0 | " + a.getDesc());
        return "Added todo: " + todo;
    }

    private String handleMark(String line) throws EmptyTaskException, IOException, UnexpectedCommandException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.MARK, line);
        if (a.getIndex() < 0 || a.getIndex() >= tasks.size()) {
            throw new UnexpectedCommandException("Index out of bounds!");
        }
        Task t = tasks.get(a.getIndex());
        t.redo();
        storage.rewrite(tasks);
        return "Nice! I’ve marked this task as done:\n  " + t;
    }

    private String handleUnmark(String line) throws EmptyTaskException, IOException, UnexpectedCommandException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.UNMARK, line);
        if (a.getIndex() < 0 || a.getIndex() >= tasks.size()) {
            throw new UnexpectedCommandException("Index out of bounds!");
        }
        Task t = tasks.get(a.getIndex());
        t.undo();
        storage.rewrite(tasks);
        return "OK, I’ve marked this task as not done yet:\n  " + t;
    }

    private String handleDelete(String line) throws EmptyTaskException, IOException, UnexpectedCommandException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.DELETE, line);
        if (a.getIndex() < 0 || a.getIndex() >= tasks.size()) {
            throw new UnexpectedCommandException("Index out of bounds!");
        }
        Task removed = tasks.remove(a.getIndex());
        storage.rewrite(tasks);
        return "Noted. I’ve removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String handleDeadline(String line) throws EmptyTaskException, IOException, UnexpectedCommandException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.DEADLINE, line);
        Deadline d = new Deadline(a.getDesc(), a.getBy());
        tasks.add(d);
        storage.appendLine("deadline | 0 | " + a.getDesc() + " | " + a.getBy());
        return "Got it. I’ve added this task:\n  " + d
                + "\n Now you have " + tasks.size() + " tasks in the list.";
    }

    private String handleEvent(String line) throws EmptyTaskException, IOException, UnexpectedCommandException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.EVENT, line);
        Event e = new Event(a.getDesc(), a.getFrom(), a.getTo());
        tasks.add(e);
        storage.appendLine("event | 0 | " + a.getDesc() + " | " + a.getFrom() + " | " + a.getTo());
        return "Got it. I’ve added this task:\n  " + e
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String handleFind(String line) throws EmptyTaskException, UnexpectedCommandException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.FIND, line);
        String needle = a.getFindKeyword().toLowerCase().trim();

        TaskList found = new TaskList(new java.util.ArrayList<>());
        for (Task t : tasks.all()) {
            if (t.toString().toLowerCase().contains(needle)) {
                found.add(t);
            }
        }

        return found.size() == 0
                ? "I couldn’t find any matching tasks."
                : "Here are the matching tasks in your list:\n" + ui.formatList(found.all());
    }

    private String handleUpdate(String line) throws EmptyTaskException, UnexpectedCommandException, IOException {
        Parser.ParsedArgs a = Parser.parseArgs(Parser.Command.UPDATE, line);
        Task t = tasks.get(a.getIndex());
        if (!(t instanceof Event)) {
            throw new UnexpectedCommandException("Task of index " + a.getIndex() + " is not an Event!");
        }
        Event e = (Event) t;
        e.updateEvent(a.getFrom(), a.getTo());
        storage.rewrite(tasks);
        return "Updated Event " + a.getIndex() + " successfully!";
    }

    /* =========================
     * Tiny helpers for routing
     * ========================= */

    private String throwUnknown() throws UnexpectedCommandException {
        throw new UnexpectedCommandException("OOPS!!! I don't know what that means :-(");
    }

    private String handleUnhandled(Parser.Command cmd) throws UnexpectedCommandException {
        assert false : "Unhandled command: " + cmd;
        throw new UnexpectedCommandException("OOPS!!! I don't know what that means :-(");
    }
}
