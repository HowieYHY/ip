import java.util.Scanner;
import java.util.ArrayList;

// Step 1: Define the custom exception class
class IdecapitatorException extends Exception {
    public IdecapitatorException(String message) {
        super(message);
    }
}

public class Idecapitator {
    public static void main(String[] args) {
        String line = "    ____________________________________________________________";
        ArrayList<Task> tasks = new ArrayList<>();

        System.out.println(line + "\n    Hello! I'm Idecapitator\n    What can I do for you?\n" + line);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Step 2: Wrap the entire command processing in a try block
            try {
                String input = scanner.nextLine();
                if (input.trim().isEmpty()) {
                    continue;
                }
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
                        System.out.println(" Nice! I've marked this task as done:\n " + tasks.get(mIdx));
                        break;

                    case "unmark":
                        int uIdx = Integer.parseInt(input.split(" ")[1]) - 1;
                        tasks.get(uIdx).unmarkAsDone();
                        System.out.println(" OK, I've marked this task as not done yet:\n " + tasks.get(uIdx));
                        break;

                    case "todo":
                        if (input.trim().length() <= 4) {
                            throw new IdecapitatorException("A todo needs a description. Don't leave it headless!");
                        }
                        tasks.add(new Todo(input.substring(5)));
                        printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                        break;

                    case "deadline":
                        if (!input.contains(" /by ")) {
                            throw new IdecapitatorException("Deadlines require a '/by' date to be valid.");
                        }
                        String[] dParts = input.substring(9).split(" /by ");
                        tasks.add(new Deadline(dParts[0], dParts[1]));
                        printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                        break;

                    case "event":
                        if (!input.contains(" /from ") || !input.contains(" /to ")) {
                            throw new IdecapitatorException("Events need both a '/from' and a '/to' time.");
                        }
                        String[] eParts = input.substring(6).split(" /from | /to ");
                        tasks.add(new Event(eParts[0], eParts[1], eParts[2]));
                        printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                        break;

                    default:
                        // Handle unknown commands
                        throw new IdecapitatorException("I don't have a protocol for '" + command + "'.");
                }
            } catch (IdecapitatorException e) {
                // Step 3: Catch and print your custom error messages
                System.out.println("    CRITICAL ERROR: " + e.getMessage());
            } catch (Exception e) {
                // Catch unexpected errors like invalid numbers in mark/unmark
                System.out.println("    CRITICAL ERROR: Invalid input format.");
            } finally {
                System.out.println(line);
            }
        }
    }

    private static void handleMarking(String input, ArrayList<Task> tasks, boolean isMark) throws IdecapitatorException {
        try {
            int idx = Integer.parseInt(input.split(" ")[1]) - 1;
            if (idx < 0 || idx >= tasks.size()) {
                throw new IdecapitatorException("That task index does not exist in my records.");
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IdecapitatorException("Please provide a valid task number.");
        }
    }

    private static void printAddedMessage(Task task, int count) {
        System.out.println("    Got it. I've added this task:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + count + " tasks in the list.");
    }
}