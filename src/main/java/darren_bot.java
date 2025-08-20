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

        ArrayList<String> outList = new ArrayList<>();

        while (true) {
            String line = scanner.nextLine();
            if (line.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }

            else if (line.equals("list")) {
                for (int i = 0; i < outList.size(); i++) {
                    System.out.println(i + ": " + outList.get(i));
                }
            }
            else {
                System.out.println("added: " + line);
                outList.add(line);
            }
        }
        scanner.close();
    }
}