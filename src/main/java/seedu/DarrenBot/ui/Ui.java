package seedu.DarrenBot.ui;

import java.util.List;
import java.util.Scanner;
import seedu.DarrenBot.tasks.Task;

/**
 * Handles all interactions with the user through the command line interface.
 * <p>
 * The {@code Ui} class is responsible for displaying messages,
 * reading user input, and showing task-related feedback.
 * It acts as the presentation layer of the application.
 * </p>
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Displays the welcome message when the program starts.
     */
    public void showWelcome() {
        System.out.println("____________________________________________________________");
        System.out.println("Hello! I'm darren_bot");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");
    }

    /**
     * Displays the exit message when the program terminates.
     */
    public void showExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Reads the next line of user input from the console.
     *
     * @return the input line entered by the user, or {@code null}
     *         if no input is available
     */
    public String readCommand() {
        if (!this.scanner.hasNextLine()) return null;
        return this.scanner.nextLine();
    }

    /**
     * Closes the underlying {@link Scanner} used for input.
     */
    public void close() {
        this.scanner.close();
    }

    /**
     * Displays a message indicating that the task list is empty.
     */
    public void showEmptyList() {
        System.out.println("Oops the list is empty!");
    }

    /**
     * Displays all tasks currently in the task list.
     *
     * @param tasks the list of tasks to display
     */
    public void showList(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(i + ". " + tasks.get(i));
        }
    }

    /**
     * Displays a confirmation message when a task is added.
     *
     * @param t       the task that was added
     * @param newSize the new size of the task list after addition
     */
    public void showAdded(Task t, int newSize) {
        System.out.println("Got it. I've added this task:\n" + t + "\n" +
                "Now you have " + newSize + " tasks in the list");
    }

    /**
     * Displays a confirmation message when a task is removed.
     *
     * @param t       the task that was removed
     * @param newSize the new size of the task list after removal
     */
    public void showRemoved(Task t, int newSize) {
        System.out.println("Noted. I've removed this task:" + t + "\n" +
                "Now you have " + newSize + " tasks in the list.");
    }

    /**
     * Displays a confirmation message when a task is marked as done.
     *
     * @param t the task that was marked as completed
     */
    public void showMark(Task t) {
        System.out.println("Nice! I've marked this task as done: " + t);
    }

    /**
     * Displays a confirmation message when a task is unmarked (set as not done).
     *
     * @param t the task that was unmarked
     */
    public void showUnmark(Task t) {
        System.out.println("OK, I've marked this task as not done yet:" + t);
    }

    /**
     * Displays an error message to the user.
     *
     * @param msg the error message to display
     */
    public void showError(String msg) {
        System.out.println(msg);
    }
}
