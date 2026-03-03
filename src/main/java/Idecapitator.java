import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

//Custom Exceptions
class IdecapitatorException extends Exception {
    public IdecapitatorException(String message) { super(message); }
}

//UI Class
class Ui {
    private final String line = "    ____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        System.out.println(line + "\n    Hello! I'm Idecapitator\n    What can I do for you?\n" + line);
    }
    public void showLine() { System.out.println(line); }
    public String readCommand() { return scanner.nextLine(); }
    public void showError(String message) { System.out.println("    CRITICAL ERROR: " + message); }
    public void showLoadingError() { System.out.println("    NOTICE: No existing data found. Starting fresh!"); }
    public void showMessage(String message) { System.out.println("    " + message); }
}

//TaskList Class
class TaskList {
    private final ArrayList<Task> tasks;
    public TaskList() { this.tasks = new ArrayList<>(); }
    public TaskList(ArrayList<Task> tasks) { this.tasks = tasks; }

    public void addTask(Task task) { tasks.add(task); }
    public Task delete(int index) { return tasks.remove(index); }
    public Task get(int index) { return tasks.get(index); }
    public int size() { return tasks.size(); }
    public ArrayList<Task> getAllTasks() { return tasks; }
}

//Storage Class
class Storage {
    private final String filePath;
    public Storage(String filePath) { this.filePath = filePath; }

    public ArrayList<Task> load() throws IdecapitatorException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) throw new IdecapitatorException("File not found");

        try (Scanner s = new Scanner(f)) {
            while (s.hasNext()) {
                String line = s.nextLine();
                String[] p = line.split(" \\| ");
                Task t;
                switch (p[0]) {
                    case "T": t = new Todo(p[2]); break;
                    case "D": t = new Deadline(p[2], p[3]); break;
                    case "E": t = new Event(p[2], p[3], p[4]); break;
                    default: continue;
                }
                if (p[1].equals("1")) t.markAsDone();
                loadedTasks.add(t);
            }
        } catch (FileNotFoundException e) {
            throw new IdecapitatorException("Could not access file.");
        }
        return loadedTasks;
    }

    public void save(TaskList tasks) {
        try {
            File f = new File(filePath);
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(filePath);
            for (Task t : tasks.getAllTasks()) {
                fw.write(t.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("    ERROR: Save failed.");
        }
    }
}

//Main Program Class
public class Idecapitator {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    public Idecapitator(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IdecapitatorException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand.trim().isEmpty()) continue;
                ui.showLine();

                String[] parts = fullCommand.split(" ", 2);
                String command = parts[0];

                switch (command) {
                    case "bye":
                        isExit = true;
                        ui.showMessage("Bye. Hope to see you again soon!");
                        break;
                    case "list":
                        ui.showMessage("Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            ui.showMessage((i + 1) + "." + tasks.get(i));
                        }
                        break;
                    case "mark":
                        int mIdx = Integer.parseInt(parts[1]) - 1;
                        tasks.get(mIdx).markAsDone();
                        storage.save(tasks);
                        ui.showMessage("Nice! I've marked this task as done:\n      " + tasks.get(mIdx));
                        break;
                    case "delete":
                        int dIdx = Integer.parseInt(parts[1]) - 1;
                        Task removed = tasks.delete(dIdx);
                        storage.save(tasks);
                        ui.showMessage("Noted. I've removed this task:\n      " + removed);
                        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                        break;
                    case "todo":
                        if (parts.length < 2) throw new IdecapitatorException("A todo needs a description.");
                        Task t = new Todo(parts[1].trim());
                        tasks.addTask(t);
                        storage.save(tasks);
                        ui.showMessage("Got it. I've added this task:\n      " + t);
                        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                        break;
                    case "deadline":
                        if (!parts[1].contains(" /by ")) throw new IdecapitatorException("Deadlines require '/by'.");
                        String[] dParts = parts[1].split(" /by ");
                        Task d = new Deadline(dParts[0], dParts[1]);
                        tasks.addTask(d);
                        storage.save(tasks);
                        ui.showMessage("Got it. I've added this task:\n      " + d);
                        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                        break;
                    case "event":
                        if (!parts[1].contains(" /from ") || !parts[1].contains(" /to ")) {
                            throw new IdecapitatorException("Events need '/from' and '/to'.");
                        }
                        String[] eParts = parts[1].split(" /from | /to ");
                        Task e = new Event(eParts[0], eParts[1], eParts[2]);
                        tasks.addTask(e);
                        storage.save(tasks);
                        ui.showMessage("Got it. I've added this task:\n      " + e);
                        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                        break;
                    default:
                        throw new IdecapitatorException("I don't have a protocol for '" + command + "'.");
                }
            } catch (Exception e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) {
        new Idecapitator("./data/idecapitator.txt").run();
    }
}