package DarrenBot.ui;

import java.util.List;
import java.util.Scanner;
import DarrenBot.tasks.Task;

public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        System.out.println("____________________________________________________________");
        System.out.println("Hello! I'm darren_bot");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");
    }

    public void showExit() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public String readCommand() {
        if (!this.scanner.hasNextLine()) return null;
        return this.scanner.nextLine();
    }

    public void close() {
        this.scanner.close();
    }

    public void showEmptyList() {
        System.out.println("Oops the list is empty!");
    }

    public void showList(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(i + ". " + tasks.get(i));
        }
    }

    public void showAdded(Task t, int newSize) {
        System.out.println("Got it. I've added this task:\n" + t + "\n" +
                "Now you have " + newSize + " tasks in the list");
    }

    public void showRemoved(Task t, int newSize) {
        System.out.println("Noted. I've removed this task:" + t + "\n" +
                "Now you have " + newSize + " tasks in the list.");
    }

    public void showMark(Task t) {
        System.out.println("Nice! I've marked this task as done: " + t);
    }

    public void showUnmark(Task t) {
        System.out.println("OK, I've marked this task as not done yet:" + t);
    }

    public void showError(String msg) {
        System.out.println(msg);
    }
}
