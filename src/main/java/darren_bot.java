import javafx.fxml.LoadListener;

import java.io.*;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class darren_bot {

    public static final String FILE_PATH = "data/duke.txt";

    enum Command {
        BYE, LIST, MARK, UNMARK, DEADLINE, TODO, EVENT, DELETE, UNKNOWN;

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
                default -> UNKNOWN;
            };
        }
    }

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
            if (line == null) break; // EOF

            Parser.Command cmd = Parser.parseCommand(line);

            try {
                switch (cmd) {
                    case BYE -> {
                        ui.showExit();
                        ui.close();
                        return;
                    }
                    case LIST -> {
                        if (tasks.size() == 0) ui.showEmptyList();
                        else ui.showList(tasks.all());
                    }
                    case MARK -> {
                        Parser.ParsedArgs args1 = Parser.parseArgs(cmd, line);
                        Task t = tasks.get(args1.index);
                        t.isDone = true;
                        storage.rewrite(tasks); // persist
                        ui.showMark(t);
                    }
                    case UNMARK -> {
                        Parser.ParsedArgs args1 = Parser.parseArgs(cmd, line);
                        Task t = tasks.get(args1.index);
                        t.isDone = false;
                        storage.rewrite(tasks); // persist
                        ui.showUnmark(t);
                    }
                    case DELETE -> {
                        Parser.ParsedArgs args1 = Parser.parseArgs(cmd, line);
                        Task removed = tasks.remove(args1.index);
                        storage.rewrite(tasks); // persist
                        ui.showRemoved(removed, tasks.size());
                    }
                    case TODO -> {
                        Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                        Todo todo = new Todo(a.desc);
                        tasks.add(todo);
                        // append exactly one line
                        storage.appendLine("todo | 0 | " + a.desc);
                        ui.showAdded(todo, tasks.size());
                    }
                    case DEADLINE -> {
                        Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                        Deadline d = new Deadline(a.desc, a.by);
                        tasks.add(d);
                        storage.appendLine("deadline | 0 | " + a.desc + " | " + a.by);
                        ui.showAdded(d, tasks.size());
                    }
                    case EVENT -> {
                        Parser.ParsedArgs a = Parser.parseArgs(cmd, line);
                        Event e = new Event(a.desc, a.from, a.to);
                        tasks.add(e);
                        storage.appendLine("event | 0 | " + a.desc + " | " + a.from + " | " + a.to);
                        ui.showAdded(e, tasks.size());
                    }
                    default -> throw new UnexpectedCommandException("OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (EmptyTaskException e) {
                ui.showError(e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                ui.showError("OOPS!!! Index out of bounds.");
            } catch (UnexpectedCommandException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Cannot write to data file: " + e.getMessage());
            }
        }
        ui.close();
    }

    public static class Parser {

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

    public static class Ui {
        private final Scanner scanner = new Scanner(System.in);

        public void showWelcome() {
            System.out.println("____________________________________________________________");
            System.out.println("Hello! I'm darren_bot");
            System.out.println("What can I do for you?");
            System.out.println("____________________________________________________________");
        }

        public void showExit() {
            System.out.println("Bye. Hope to see you again soon!");
        }

        public String readCommand() {
            if (!this.scanner.hasNextLine()) return null;
            return this.scanner.nextLine();
        }

        public void close() {
            this.scanner.close();
        }

        public void showEmptyList() {
            System.out.println("Oops the list is empty!");
        }

        public void showList(List<Task> tasks) {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(i + ". " + tasks.get(i));
            }
        }

        public void showAdded(Task t, int newSize) {
            System.out.println("Got it. I've added this task:\n" + t + "\n" +
                    "Now you have " + newSize + " tasks in the list");
        }

        public void showRemoved(Task t, int newSize) {
            System.out.println("Noted. I've removed this task:" + t + "\n" +
                    "Now you have " + newSize + " tasks in the list.");
        }

        public void showMark(Task t) {
            System.out.println("Nice! I've marked this task as done: " + t);
        }

        public void showUnmark(Task t) {
            System.out.println("OK, I've marked this task as not done yet:" + t);
        }

        public void showError(String msg) {
            System.out.println(msg);
        }
    }

    public static class Storage {
        private final Path path;

        public Storage(String filePath) {
            this.path = Paths.get(filePath);
        }

        public void ensureDataFile() throws IOException {
            Files.createDirectories(this.path.getParent());
            if (!Files.exists(this.path)) {
                Files.createFile(this.path);
            }
        }

        public TaskList load() throws IOException, UnexpectedCommandException {
            ArrayList<Task> tasks = new ArrayList<>();
            if (!Files.exists(this.path)) return new TaskList(tasks);

            try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    // parts are like: type | 0/1 | desc | (extra...)
                    String type = parts[0].trim().toLowerCase();
                    boolean isDone = parts.length > 1 && parts[1].trim().equals("1");

                    switch (type) {
                        case "todo" -> {
                            Todo t = new Todo(parts[2].trim());
                            t.isDone = isDone;
                            tasks.add(t);
                        }
                        case "deadline" -> {
                            LocalDate by = LocalDate.parse(parts[3].trim());
                            Deadline d = new Deadline(parts[2].trim(), by);
                            d.isDone = isDone;
                            tasks.add(d);
                        }
                        case "event" -> {
                            Event e = new Event(parts[2].trim(), parts[3].trim(), parts[4].trim());
                            e.isDone = isDone;
                            tasks.add(e);
                        }
                        default -> throw new UnexpectedCommandException("Tried to initialize an UNKNOWN Task");
                    }
                }
            }
            return new TaskList(tasks);
        }

        /** Append a new task line to file (for add). */
        public void appendLine(String line) throws IOException {
            Files.writeString(this.path, line + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        /** Rewrite whole file from current TaskList snapshot (for delete/mark/unmark). */
        public void rewrite(TaskList tasks) throws IOException {
            List<String> lines = tasks.asStorageLines();
            Files.write(this.path, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        }
    }

    public static class TaskList {
        private final ArrayList<Task> tasks;

        public TaskList(ArrayList<Task> tasks) {
            this.tasks = tasks;
        }

        public int size() {
            return tasks.size();
        }

        public Task get(int i) {
            return tasks.get(i);
        }

        public void add(Task t) {
            tasks.add(t);
        }

        public Task remove(int i) {
            return tasks.remove(i);
        }

        public List<Task> all() {
            return tasks;
        }

        public List<String> asStorageLines() {
            ArrayList<String> lines = new ArrayList<>();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

            for (Task t : tasks) {
                if (t instanceof Todo todo) {
                    lines.add(todo.taskType + " | " + (t.isDone ? "1" : "0") + " | " + todo.description);
                } else if (t instanceof Deadline dl) {
                    lines.add(dl.taskType + " | " + (t.isDone ? "1" : "0") + " | " + dl.description + " | " + dl.deadline.format(fmt));
                } else if (t instanceof Event ev) {
                    lines.add(ev.taskType + " | " + (t.isDone ? "1" : "0") + " | " + ev.description + " | " + ev.from + " | " + ev.to);
                }
            }
            return lines;
        }
    }

    public static class Task {
        protected String description;
        protected boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        public String getStatusIcon() {
            return (isDone ? "X" : " "); // mark done task with X
        }

        @Override
        public String toString() {
            return "[" + this.getStatusIcon() + "] " + this.description;
        }

    }

    public static class Todo extends Task {
        String taskType;
        public Todo(String description) {
            super(description);
            this.taskType = "todo";
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    public static class Deadline extends Task {
        LocalDate deadline;
        String taskType;
        public Deadline(String description, LocalDate deadline) {
            super(description);
            this.deadline = deadline;
            this.taskType = "deadline";
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " +
                    this.deadline.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
        }
    }

    public static class Event extends Task {
        String from;
        String to;
        String taskType;
        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
            this.taskType = "event";
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
        }
    }

    public static class UnexpectedCommandException extends Exception {
        public UnexpectedCommandException(String message) {
            super(message);
        }
    }

    public static class EmptyTaskException extends Exception {
        public EmptyTaskException(String taskType) {
            super("OOPS!!! The description of a " + taskType + " cannot be empty.");
        }
    }

}