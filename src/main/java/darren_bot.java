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

            else if (line.startsWith("deadline")) {
                int byIndex = line.indexOf("/by ");
                String deadline = line.substring(byIndex + 4);
                String description = line.substring(9, byIndex);
                Deadline newDeadline = new Deadline(description, deadline);
                outList.add(newDeadline);
                System.out.println("Got it. I've added this task: \n" +
                        newDeadline + "\n" +
                        "Now you have " + outList.size() + " tasks in the list");
            }

            else if (line.startsWith("event")) {
                int fromIndex = line.indexOf("/from ");
                int toIndex = line.indexOf("/to ");
                String from = line.substring(fromIndex + 6, toIndex);
                String to = line.substring(toIndex + 4);
                String description = line.substring(6, fromIndex);
                Event newEvent = new Event(description, from, to);
                outList.add(newEvent);
                System.out.println("Got it. I've added this task: \n" +
                        newEvent + "\n" +
                        "Now you have " + outList.size() + " tasks in the list");
            }

            else if (line.startsWith("todo")) {

                int stringSize = line.split(" ").length;
                String description = line.substring(5);
                Todo newTodo = new Todo(description);
                outList.add(newTodo);
                System.out.println("Got it. I've added this task: \n" +
                        newTodo + "\n" +
                        "Now you have " + outList.size() + " tasks in the list");
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

    public static class Todo extends Task {
        public Todo(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    public static class Deadline extends Task {
        String deadline;
        public Deadline(String description, String deadline) {
            super(description);
            this.deadline = deadline;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + this.deadline + ")";
        }
    }

    public static class Event extends Task {
        String from;
        String to;
        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
        }
    }

}