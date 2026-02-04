import java.util.Scanner;

public class Idecapitator {
    public static void main(String[] args) {
        String line = "    ____________________________________________________________";
        Task[] tasks = new Task[100];
        int taskCount = 0;

        System.out.println(line + "\n    Hello! I'm Idecapitator\n    What can I do for you?\n" + line);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            String command = input.split(" ")[0];
            System.out.println(line);

            switch (command) {
                case "bye":
                    System.out.println("    Bye. Hope to see you again soon!");
                    System.out.println(line);
                    scanner.close();
                    return;
                case "list":
                    System.out.println("    Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println("    " + (i + 1) + "." + tasks[i]);
                    }
                    break;
                case "mark":
                    int mIdx = Integer.parseInt(input.split(" ")[1]) - 1;
                    tasks[mIdx].markAsDone();
                    System.out.println("    Nice! I've marked this task as done:\n      " + tasks[mIdx]);
                    break;
                case "unmark":
                    int uIdx = Integer.parseInt(input.split(" ")[1]) - 1;
                    tasks[uIdx].unmarkAsDone();
                    System.out.println("    OK, I've marked this task as not done yet:\n      " + tasks[uIdx]);
                    break;
                case "todo":
                    tasks[taskCount] = new Todo(input.substring(5));
                    taskCount++;
                    printAddedMessage(tasks[taskCount - 1], taskCount);
                    break;
                case "deadline":
                    String[] dParts = input.substring(9).split(" /by ");
                    tasks[taskCount] = new Deadline(dParts[0], dParts[1]);
                    taskCount++;
                    printAddedMessage(tasks[taskCount - 1], taskCount);
                    break;
                case "event":
                    String[] eParts = input.substring(6).split(" /from | /to ");
                    tasks[taskCount] = new Event(eParts[0], eParts[1], eParts[2]);
                    taskCount++;
                    printAddedMessage(tasks[taskCount - 1], taskCount);
                    break;
                default:
                    System.out.println("    I'm sorry, I don't know what that means.");
                    break;
            }
            System.out.println(line);
        }
    }

    private static void printAddedMessage(Task task, int count) {
        System.out.println("    Got it. I've added this task:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + count + " tasks in the list.");
    }
}