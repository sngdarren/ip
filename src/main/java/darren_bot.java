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

        while (true) {
            String line = scanner.nextLine();
            if (line.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }
            else {
                System.out.println(line);
            }
        }
        scanner.close();
    }
}