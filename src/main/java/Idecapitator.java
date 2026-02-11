import java.util.Scanner;
import java.util.ArrayList;

public class Idecapitator {
    public static void main(String[] args) {
        String line = "    ____________________________________________________________";
        ArrayList<Task> tasks = new ArrayList<>();

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
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println("    " + (i + 1) + "." + tasks.get(i));
                    }
                    break;
                case "mark":
                    int mIdx = Integer.parseInt(input.split(" ")[1]) - 1;
                    tasks.get(mIdx).markAsDone();
                    System.out.println("    Nice! I've marked this task as done:\n      " + tasks.get(mIdx));
                    break;
                case "unmark":
                    int uIdx = Integer.parseInt(input.split(" ")[1]) - 1;
                    tasks.get(uIdx).unmarkAsDone();
                    System.out.println("    OK, I've marked this task as not done yet:\n      " + tasks.get(uIdx));
                    break;
                case "todo":
                    tasks.add(new Todo(input.substring(5)));
                    printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                    break;
                case "deadline":
                    String[] dParts = input.substring(9).split(" /by ");
                    tasks.add(new Deadline(dParts[0], dParts[1]));
                    printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                    break;
                case "event":
                    String[] eParts = input.substring(6).split(" /from | /to ");
                    tasks.add(new Event(eParts[0], eParts[1], eParts[2]));
                    printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
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