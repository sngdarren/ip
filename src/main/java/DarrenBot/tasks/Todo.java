package DarrenBot.tasks;

public class Todo extends Task {
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
