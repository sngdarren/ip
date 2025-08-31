package DarrenBot.tasks;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskList {
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
