package DarrenBot.storage;

import DarrenBot.tasks.Task;
import DarrenBot.tasks.Todo;
import DarrenBot.tasks.Event;
import DarrenBot.tasks.Deadline;
import DarrenBot.tasks.TaskList;
import DarrenBot.exception.UnexpectedCommandException;

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

public class Storage {
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
