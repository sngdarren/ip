package seedu.darrenbot.tasks;

public class Event extends Task {
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
