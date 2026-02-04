import java.util.Scanner;

public class Idecapitator {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        Task[] tasks = new Task[100];
        int taskCount = 0;

        System.out.println(line);
        System.out.println("Hello! I'm Idecapitator");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            String command = input.split(" ")[0];
            System.out.println(line);

            switch (command) {
                case "bye":
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(line);
                    scanner.close();
                    return;
                case "list":
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i].toString());
                    }
                    break;
                case "mark":
                    int markIdx = Integer.parseInt(input.split(" ")[1]) - 1;
                    tasks[markIdx].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[markIdx]);
                    break;
                case "unmark":
                    int unmarkIdx = Integer.parseInt(input.split(" ")[1]) - 1;
                    tasks[unmarkIdx].unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[unmarkIdx]);
                    break;
                default:
                    tasks[taskCount] = new Task(input);
                    taskCount++;
                    System.out.println("added: " + input);
                    break;
            }
            System.out.println(line);
        }
    }
}