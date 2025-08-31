package seedu.DarrenBot.tasks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a specific deadline.
 * <p>
 * A {@code Deadline} is a type of {@link Task} that includes a due date
 * represented by a {@link LocalDate}. It is displayed with a "[D]" tag
 * when listed, along with the formatted deadline date.
 * </p>
 *
 * <p>Example:</p>
 * <pre>
 * Deadline d = new Deadline("submit report", LocalDate.of(2025, 9, 1));
 * System.out.println(d);
 * // Output: [D][ ] submit report (by: Sep 1 2025)
 * </pre>
 */
public class Deadline extends Task {
    LocalDate deadline;
    String taskType;

    /**
     * Constructs a {@code Deadline} with the given description and due date.
     *
     * @param description the description of the task
     * @param deadline    the due date of the task
     */
    public Deadline(String description, LocalDate deadline) {
        super(description);
        this.deadline = deadline;
        this.taskType = "deadline";
    }

    /**
     * Returns the string representation of the deadline task.
     * <p>
     * The format includes:
     * <ul>
     *   <li>A "[D]" prefix to indicate it is a deadline task.</li>
     *   <li>The base task string from {@link Task#toString()}.</li>
     *   <li>The deadline date formatted as "MMM d yyyy".</li>
     * </ul>
     * </p>
     *
     * @return the string representation of this deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " +
                this.deadline.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }
}
