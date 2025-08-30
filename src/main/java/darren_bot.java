import javafx.fxml.LoadListener;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
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
//
        Scanner scanner = new Scanner(System.in);
        System.out.println("____________________________________________________________\n" +
                "Hello! I'm darren_bot\n" +
                "What can I do for you?\n" +
                "____________________________________________________________\n"
        );

        String dataFile = FILE_PATH;
        Path p = Paths.get(dataFile);

        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            System.out.println("Cannot create data directory: " + e.getMessage());
        }

        createDataFile(dataFile);
        ArrayList<Task> outList = makeOutList(dataFile);
        while (true) {
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine();
            Command c = Command.fromLine(line);
            String newLine = "";

            try {
                switch (c) {
                    case BYE -> {
                        System.out.println("Bye. Hope to see you again soon!");
                        return;
                    }
                    case LIST -> {
                        if (outList.size() == 0) {
                            System.out.println("Oops the list is empty!");
                        } else {
                            for (int i = 0; i < outList.size(); i++) {
                                System.out.println(i + ". " + outList.get(i)); // keep your 0-based display
                            }
                        }
                    }
                    case MARK -> {
                        try {
                            String[] value = line.split(" ");
                            int second = Integer.parseInt(value[1]);
                            Task currTask = outList.get(second);
                            currTask.isDone = true;
                            markWrite(dataFile, second, true);
                            System.out.println("Nice! I've marked this task as done: " + currTask);
                        } catch (IndexOutOfBoundsException e) {
                            throw new EmptyTaskException("mark");
                        }
                    }
                    case UNMARK -> {
                        try {
                            String[] value = line.split(" ");
                            int second = Integer.parseInt(value[1]);
                            Task currTask = outList.get(second);
                            currTask.isDone = false;
                            markWrite(dataFile, second, false);
                            System.out.println("OK, I've marked this task as not done yet:" + currTask);
                        } catch (IndexOutOfBoundsException e) {
                            throw new EmptyTaskException("unmark");
                        }
                    }
                    case DEADLINE -> {
                        try {
                            int byIndex = line.indexOf("/by ");
                            String deadlineString = line.substring(byIndex + 4).strip();
                            LocalDate deadline = LocalDate.parse(deadlineString);
                            String description = line.substring(9, byIndex);
                            Deadline newDeadline = new Deadline(description, deadline);
                            outList.add(newDeadline);
                            newLine = "deadline | 0 | " + description + " | " + deadline;
                            System.out.println("Got it. I've added this task:\n" +
                                    newDeadline + "\n" +
                                    "Now you have " + outList.size() + " tasks in the list");

                        } catch (IndexOutOfBoundsException e) {
                            throw new EmptyTaskException("deadline");
                        } catch (DateTimeParseException e) {
                            System.out.println("Deadline should be in the format '/by yyyy-mm-dd'");
                        }
                    }
                    case EVENT -> {
                        try {
                            int fromIndex = line.indexOf("/from ");
                            int toIndex = line.indexOf("/to ");
                            String from = line.substring(fromIndex + 6, toIndex);
                            String to = line.substring(toIndex + 4);
                            String description = line.substring(6, fromIndex);
                            Event newEvent = new Event(description, from, to);
                            outList.add(newEvent);
                            newLine = "event | 0 | " + description + " | " + from + " | " + to;
                            System.out.println("Got it. I've added this task:\n" +
                                    newEvent + "\n" +
                                    "Now you have " + outList.size() + " tasks in the list");
                        } catch (IndexOutOfBoundsException e) {
                            throw new EmptyTaskException("event");
                        }
                    }
                    case TODO -> {
                        try {
                            String description = line.substring(5);
                            Todo newTodo = new Todo(description);
                            outList.add(newTodo);
                            newLine = "todo | 0 | " + description;
                            System.out.println("Got it. I've added this task:\n" +
                                    newTodo + "\n" +
                                    "Now you have " + outList.size() + " tasks in the list");
                        } catch (IndexOutOfBoundsException e) {
                            throw new EmptyTaskException("todo");
                        }
                    }
                    case DELETE -> {
                        try {
                            String[] value = line.split(" ");
                            int second = Integer.parseInt(value[1]);
                            Task currTask = outList.remove(second);
                            System.out.println("Noted. I've removed this task:" + currTask + "\n"
                                    + "Now you have " + outList.size() + " tasks in the list.");
                        } catch (IndexOutOfBoundsException e) {
                            throw new EmptyTaskException("delete");
                        }
                    }
                    case UNKNOWN -> {
                        throw new UnexpectedCommandException("OOPS!!! I'm sorry, but I don't know what that means :-(");
                    }
                }
                try {
                    if (newLine != null && !newLine.isBlank()) {
                        Files.writeString(p, newLine + System.lineSeparator(),
                                StandardCharsets.UTF_8,
                                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    }
                } catch (IOException e) {
                    System.out.println("Cannot write new Line: " + e.getMessage());
                }

            } catch (UnexpectedCommandException | EmptyTaskException e) {
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }

    public static void createDataFile(String filePath) {
        File dataFile = new File(filePath);
        try {
            dataFile.createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }

    public static void markWrite(String filePath, int index, boolean mark) {
        Path p = Paths.get(filePath);

        try {
            // Read all lines into a List
            List<String> lines = Files.readAllLines(p);

            // Change line 3 (index 2)
            String targetLine = lines.get(index);
            String[] parts = targetLine.split("\\|");
            parts[1] = mark ? "1" : "0";
            String newTarget = String.join(" | ", parts);
            lines.set(index, newTarget);

            // Write back to the file
            Files.write(p, lines);

            System.out.println("Line updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Task> makeOutList(String filePath) {
        ArrayList<Task> tempList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split line by '|'
                String[] parts = line.split("\\|");
                Command c = Command.fromLine(parts[0]);
                Task t = null;

                try {
                    switch (c) {
                        case TODO -> {
                            t = new Todo(parts[2]);
                            t.isDone = (parts[1].strip().equals("1"));
                        }

                        case DEADLINE -> {
                            LocalDate deadline = LocalDate.parse(parts[3].strip());
                            t = new Deadline(parts[2], deadline);
                            t.isDone = (parts[1].strip().equals("1"));
                        }

                        case EVENT -> {
                            t = new Event(parts[2], parts[3], parts[4]);
                            t.isDone = (parts[1].strip().equals("1"));
                        }

                        case UNKNOWN -> {
                            throw new UnexpectedCommandException("Tried to initialize an UNKNOWN Task");
                        }
                    }
                    tempList.add(t);
                } catch (UnexpectedCommandException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return tempList;
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