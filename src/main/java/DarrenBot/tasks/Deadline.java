package DarrenBot.tasks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
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
