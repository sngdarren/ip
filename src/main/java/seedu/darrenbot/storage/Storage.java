package seedu.darrenbot.storage;

import seedu.darrenbot.tasks.Task;
import seedu.darrenbot.tasks.Todo;
import seedu.darrenbot.tasks.Event;
import seedu.darrenbot.tasks.Deadline;
import seedu.darrenbot.tasks.TaskList;
import seedu.darrenbot.exception.UnexpectedCommandException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading from and writing to the storage file that persists user tasks.
 * <p>
 * The {@code Storage} class is responsible for:
 * <ul>
 *     <li>Ensuring that the data directory and file exist before use.</li>
 *     <li>Loading saved tasks from the file into memory as a {@link TaskList}.</li>
 *     <li>Appending new tasks to the file when they are added.</li>
 *     <li>Rewriting the entire file to reflect updates such as deletes or marks/unmarks.</li>
 * </ul>
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * Storage storage = new Storage("data/duke.txt");
 * storage.ensureDataFile();
 * TaskList tasks = storage.load();
 * storage.appendLine("todo | 0 | read book");
 * storage.rewrite(tasks);
 * </pre>
 */
public class Storage {
    private final Path path;

    /**
     * Constructs a new {@code Storage} instance pointing to the given file path.
     *
     * @param filePath the file path where tasks should be stored and loaded from
     */
    public Storage(String filePath) {
        this.path = Paths.get(filePath);
    }

    /**
     * Ensures that the data file exists.
     * <p>
     * Creates the parent directories if they do not exist,
     * and creates the data file itself if it does not already exist.
     * </p>
     *
     * @throws IOException if the directories or file cannot be created
     */
    public void ensureDataFile() throws IOException {
        Files.createDirectories(this.path.getParent());
        if (!Files.exists(this.path)) {
            Files.createFile(this.path);
        }
    }

    /**
     * Loads all tasks from the storage file into a {@link TaskList}.
     * <p>
     * Parses each line in the file and reconstructs the appropriate
     * {@link Todo}, {@link Deadline}, or {@link Event} object,
     * restoring their completion status as recorded.
     * </p>
     *
     * @return a {@code TaskList} containing all tasks found in the file
     * @throws IOException if an I/O error occurs while reading the file
     * @throws UnexpectedCommandException if a line in the file does not match a known task type
     */
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
                        if (isDone) {
                            t.redo();
                        } else {
                            t.undo();
                        }
                        tasks.add(t);
                    }
                    case "deadline" -> {
                        LocalDate by = LocalDate.parse(parts[3].trim());
                        Deadline d = new Deadline(parts[2].trim(), by);
                        if (isDone) {
                            d.redo();
                        } else {
                            d.undo();
                        }
                        tasks.add(d);
                    }
                    case "event" -> {
                        Event e = new Event(parts[2].trim(), parts[3].trim(), parts[4].trim());
                        if (isDone) {
                            e.redo();
                        } else {
                            e.undo();
                        }
                        tasks.add(e);
                    }
                    default -> throw new UnexpectedCommandException("Tried to initialize an UNKNOWN Task");
                }
            }
        }
        return new TaskList(tasks);
    }

    /**
     * Appends a single line representing a task to the storage file.
     * <p>
     * This is typically called when a new task is created.
     * </p>
     *
     * @param line the formatted line to append to the file
     * @throws IOException if the line cannot be written
     */
    public void appendLine(String line) throws IOException {
        Files.writeString(this.path, line + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Rewrites the entire storage file with the current snapshot of tasks.
     * <p>
     * This method is used for destructive operations such as deleting a task
     * or updating its completion status, ensuring the file stays consistent
     * with the in-memory {@link TaskList}.
     * </p>
     *
     * @param tasks the current list of tasks to persist
     * @throws IOException if the file cannot be written
     */
    public void rewrite(TaskList tasks) throws IOException {
        List<String> lines = tasks.asStorageLines();
        Files.write(this.path, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }
}
