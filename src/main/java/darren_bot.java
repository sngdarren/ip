import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.Scanner;

public class darren_bot {
    public static void main(String[] args) {
//
        Scanner scanner = new Scanner(System.in);


        System.out.println("____________________________________________________________ \n"
                + "Hello! I'm darren_bot \n"
                + "What can I do for you? \n"
                + "____________________________________________________________ \n"
        );

        ArrayList<Task> outList = new ArrayList<>();
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }

            else if (line.equals("list")) {
                for (int i = 0; i < outList.size(); i++) {
                    System.out.println(i + ". " + outList.get(i));
                }
            }

            else if (line.startsWith("mark")) {
                String[] value = line.split(" ");
                int second = Integer.parseInt(value[1]);
                Task currTask = outList.get(second);
                currTask.isDone = true;
                System.out.println("Nice! I've marked this task as done: " + currTask);

            }

            else if (line.startsWith("unmark")) {
                String[] value = line.split(" ");
                int second = Integer.parseInt(value[1]);
                Task currTask = outList.get(second);
                currTask.isDone = false;
                System.out.println("OK, I've marked this task as not done yet:" + currTask);
            }

            else {
                System.out.println("added: " + line);
                Task currTask = new Task(line);
                outList.add(currTask);
            }
        }
        scanner.close();
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

}