package seedu.darrenbot.tasks;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of {@link Task} objects.
 * <p>
 * Provides methods to add, remove, access, and list tasks,
 * as well as to export them into a storage-friendly format.
 * This class is the in-memory model of all tasks managed
 * during the execution of the program.
 * </p>
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs a {@code TaskList} with the given list of tasks.
     *
     * @param tasks the initial tasks to populate the list
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the size of the task list
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param i the index of the task to retrieve
     * @return the task at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task get(int i) {
        return tasks.get(i);
    }

    /**
     * Adds a new task to the list.
     *
     * @param t the task to add
     */
    public void add(Task t) {
        tasks.add(t);
    }

    /**
     * Removes the task at the specified index from the list.
     *
     * @param i the index of the task to remove
     * @return the removed task
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task remove(int i) {
        return tasks.remove(i);
    }

    /**
     * Returns all tasks in this list.
     *
     * @return a list of all tasks
     */
    public List<Task> all() {
        return tasks;
    }

    /**
     * Converts all tasks in the list into their string representation
     * suitable for storage in a text file.
     * <p>
     * The format is consistent with how tasks are written to and read from storage:
     * <ul>
     *   <li>{@code todo | isDone | description}</li>
     *   <li>{@code deadline | isDone | description | yyyy-MM-dd}</li>
     *   <li>{@code event | isDone | description | from | to}</li>
     * </ul>
     * </p>
     *
     * @return a list of storage-formatted strings representing all tasks
     */
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
