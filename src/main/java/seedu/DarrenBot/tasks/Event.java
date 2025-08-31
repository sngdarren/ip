package seedu.DarrenBot.tasks;

/**
 * Represents a task that occurs within a specific time range.
 * <p>
 * An {@code Event} is a type of {@link Task} that includes a start time
 * and an end time. It is displayed with an "[E]" tag when listed,
 * along with the time range.
 * </p>
 *
 * <p>Example:</p>
 * <pre>
 * Event e = new Event("project meeting", "2pm", "4pm");
 * System.out.println(e);
 * // Output: [E][ ] project meeting (from: 2pm to: 4pm)
 * </pre>
 */
public class Event extends Task {
    String from;
    String to;
    String taskType;

    /**
     * Constructs an {@code Event} with the given description and time range.
     *
     * @param description the description of the event
     * @param from        the start time of the event
     * @param to          the end time of the event
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
        this.taskType = "event";
    }

    /**
     * Returns the string representation of the event task.
     * <p>
     * The format includes:
     * <ul>
     *   <li>An "[E]" prefix to indicate it is an event task.</li>
     *   <li>The base task string from {@link Task#toString()}.</li>
     *   <li>The start and end times of the event.</li>
     * </ul>
     * </p>
     *
     * @return the string representation of this event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }
}
