import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

class IdecapitatorException extends Exception {
    public IdecapitatorException(String message) {
        super(message);
    }
}

public class Idecapitator {
    private static final String FILE_PATH = "./data/idecapitator.txt";

    public static void main(String[] args) {
        String line = "    ____________________________________________________________";
        ArrayList<Task> tasks = new ArrayList<>();

        // Load existing tasks from hard disk
        loadTasksFromFile(tasks);

        System.out.println(line + "\n    Hello! I'm Idecapitator\n    What can I do for you?\n" + line);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                String input = scanner.nextLine();
                if (input.trim().isEmpty()) continue;

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
                        int mIdx = validateIndex(input, tasks.size());
                        tasks.get(mIdx).markAsDone();
                        saveTasksToFile(tasks);
                        System.out.println("    Nice! I've marked this task as done:\n      " + tasks.get(mIdx));
                        break;

                    case "unmark":
                        int uIdx = validateIndex(input, tasks.size());
                        tasks.get(uIdx).unmarkAsDone();
                        saveTasksToFile(tasks);
                        System.out.println("    OK, I've marked this task as not done yet:\n      " + tasks.get(uIdx));
                        break;

                    case "delete":
                        int dIdx = validateIndex(input, tasks.size());
                        Task removedTask = tasks.remove(dIdx);
                        saveTasksToFile(tasks);
                        System.out.println("    Noted. I've removed this task:");
                        System.out.println("      " + removedTask);
                        System.out.println("    Now you have " + tasks.size() + " tasks in the list.");
                        break;

                    case "todo":
                        if (input.trim().length() <= 4) throw new IdecapitatorException("A todo needs a description.");
                        tasks.add(new Todo(input.substring(5).trim()));
                        saveTasksToFile(tasks);
                        printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                        break;

                    case "deadline":
                        if (!input.contains(" /by ")) throw new IdecapitatorException("Deadlines require '/by'.");
                        String[] dParts = input.substring(9).split(" /by ");
                        tasks.add(new Deadline(dParts[0], dParts[1]));
                        saveTasksToFile(tasks);
                        printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                        break;

                    case "event":
                        if (!input.contains(" /from ") || !input.contains(" /to ")) throw new IdecapitatorException("Events need '/from' and '/to'.");
                        String[] eParts = input.substring(6).split(" /from | /to ");
                        tasks.add(new Event(eParts[0], eParts[1], eParts[2]));
                        saveTasksToFile(tasks);
                        printAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
                        break;

                    default:
                        throw new IdecapitatorException("I don't have a protocol for '" + command + "'.");
                }
            } catch (IdecapitatorException e) {
                System.out.println("    CRITICAL ERROR: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("    CRITICAL ERROR: Invalid input format.");
            } finally {
                System.out.println(line);
            }
        }
    }

    // --- File Storage Logic ---

    private static void saveTasksToFile(ArrayList<Task> tasks) {
        try {
            File f = new File(FILE_PATH);
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs(); // Create directory if missing

            FileWriter fw = new FileWriter(FILE_PATH);
            for (Task t : tasks) {
                fw.write(t.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("    ERROR: Could not save tasks to disk.");
        }
    }

    private static void loadTasksFromFile(ArrayList<Task> tasks) {
        File f = new File(FILE_PATH);
        if (!f.exists()) return; // Handle file-does-not-exist case

        try {
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String line = s.nextLine();
                String[] parts = line.split(" \\| ");
                Task task;
                switch (parts[0]) {
                    case "T":
                        task = new Todo(parts[2]);
                        break;
                    case "D":
                        task = new Deadline(parts[2], parts[3]);
                        break;
                    case "E":
                        task = new Event(parts[2], parts[3], parts[4]);
                        break;
                    default: continue;
                }
                if (parts[1].equals("1")) task.markAsDone();
                tasks.add(task);
            }
        } catch (FileNotFoundException e) {
            // Already handled by f.exists()
        }
    }

    private static int validateIndex(String input, int listSize) throws IdecapitatorException {
        try {
            int idx = Integer.parseInt(input.split(" ")[1]) - 1;
            if (idx < 0 || idx >= listSize) throw new IdecapitatorException("Task index out of bounds.");
            return idx;
        } catch (Exception e) {
            throw new IdecapitatorException("Please provide a valid task number.");
        }
    }

    private static void printAddedMessage(Task task, int count) {
        System.out.println("    Got it. I've added this task:\n      " + task);
        System.out.println("    Now you have " + count + " tasks in the list.");
    }
}