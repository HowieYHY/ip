import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
        ArrayList<Task> tasks = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) throw new IdecapitatorException("File not found");

        try (Scanner s = new Scanner(f)) {
            while (s.hasNext()) {
                String[] parts = s.nextLine().split(" \\| ");
                Task t = parseTask(parts);
                if (t != null) {
                    if (parts[1].equals("1")) t.markAsDone();
                    tasks.add(t);
                }
            }
        } catch (FileNotFoundException e) {
            throw new IdecapitatorException("Could not access file.");
        }
        return tasks;
    }

    private Task parseTask(String[] parts) {
        try {
            switch (parts[0]) {
                case "T": return new Todo(parts[2]);
                case "D": return new Deadline(parts[2], LocalDateTime.parse(parts[3]));
                case "E": return new Event(parts[2], LocalDateTime.parse(parts[3]), LocalDateTime.parse(parts[4]));
                default: return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
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

    //Parse date string in yyyy-MM-dd format
    private LocalDate parseDate(String dateString) throws IdecapitatorException {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IdecapitatorException("Invalid date format. Please use yyyy-MM-dd (e.g., 2019-12-02)");
        }
    }

    private LocalDateTime parseDateTime(String input) throws IdecapitatorException {
        try {
            String[] parts = input.trim().split(" ");
            LocalDate date = LocalDate.parse(parts[0]);
            if (parts.length == 1) return date.atStartOfDay();
            if (parts.length == 2) return date.atTime(parseTime(parts[1]));
            throw new IdecapitatorException("Invalid date/time format. Use: yyyy-MM-dd or yyyy-MM-dd HHmm");
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IdecapitatorException("Invalid date/time format. Use: yyyy-MM-dd or yyyy-MM-dd HHmm");
        }
    }

    private LocalTime parseTime(String timeStr) throws IdecapitatorException {
        if (timeStr.length() != 4 || !timeStr.matches("\\d{4}")) {
            throw new IdecapitatorException("Invalid time format. Use HHmm (e.g., 1800)");
        }
        int hours = Integer.parseInt(timeStr.substring(0, 2));
        int minutes = Integer.parseInt(timeStr.substring(2, 4));
        if (hours > 23 || minutes > 59) {
            throw new IdecapitatorException("Invalid time. Hours: 00-23, Minutes: 00-59");
        }
        return LocalTime.of(hours, minutes);
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
                        listTasks();
                        break;
                    case "mark":
                        markTask(Integer.parseInt(parts[1]) - 1);
                        break;
                    case "unmark":
                        unmarkTask(Integer.parseInt(parts[1]) - 1);
                        break;
                    case "delete":
                        deleteTask(Integer.parseInt(parts[1]) - 1);
                        break;
                    case "todo":
                        if (parts.length < 2) throw new IdecapitatorException("A todo needs a description.");
                        addAndSaveTask(new Todo(parts[1].trim()));
                        break;
                    case "deadline":
                        if (!parts[1].contains(" /by ")) throw new IdecapitatorException("Deadlines require '/by'.");
                        String[] dParts = parts[1].split(" /by ");
                        addAndSaveTask(new Deadline(dParts[0].trim(), parseDateTime(dParts[1].trim())));
                        break;
                    case "event":
                        if (!parts[1].contains(" /from ") || !parts[1].contains(" /to ")) {
                            throw new IdecapitatorException("Events need '/from' and '/to'.");
                        }
                        String[] eParts = parts[1].split(" /from | /to ");
                        try {
                            addAndSaveTask(new Event(eParts[0].trim(), parseDateTime(eParts[1].trim()), parseDateTime(eParts[2].trim())));
                        } catch (IllegalArgumentException iae) {
                            throw new IdecapitatorException(iae.getMessage());
                        }
                        break;
                    case "find":
                        if (parts.length < 2) throw new IdecapitatorException("Provide keyword or date (yyyy-MM-dd).");
                        handleFind(parts[1].trim());
                        break;
                    default:
                        throw new IdecapitatorException("Unknown command: '" + command + "'");
                }
            } catch (Exception e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    private void listTasks() {
        ui.showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage((i + 1) + "." + tasks.get(i));
        }
    }

    private void markTask(int index) {
        tasks.get(index).markAsDone();
        storage.save(tasks);
        ui.showMessage("Nice! I've marked this task as done:\n      " + tasks.get(index));
    }

    private void unmarkTask(int index) {
        tasks.get(index).unmarkAsDone();
        storage.save(tasks);
        ui.showMessage("Nice! I've unmarked this task:\n      " + tasks.get(index));
    }

    private void deleteTask(int index) {
        Task removed = tasks.delete(index);
        storage.save(tasks);
        ui.showMessage("Noted. I've removed this task:\n      " + removed);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private void addAndSaveTask(Task task) {
        tasks.addTask(task);
        storage.save(tasks);
        ui.showMessage("Got it. I've added this task:\n      " + task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private void handleFind(String searchInput) throws IdecapitatorException {
        LocalDate searchDate = null;
        try {
            searchDate = parseDate(searchInput);
        } catch (IdecapitatorException ex) {
            // Not a date, search by keyword
        }

        ArrayList<Task> results = searchDate != null ?
            searchByDate(searchDate) : searchByKeyword(searchInput.toLowerCase());

        displayResults(results, searchDate);
    }

    private ArrayList<Task> searchByDate(LocalDate searchDate) {
        ArrayList<Task> results = new ArrayList<>();
        for (Task task : tasks.getAllTasks()) {
            if (task instanceof Deadline &&
                ((Deadline) task).getBy().toLocalDate().equals(searchDate)) {
                results.add(task);
            } else if (task instanceof Event) {
                Event evt = (Event) task;
                LocalDate from = evt.getFrom().toLocalDate();
                LocalDate to = evt.getTo().toLocalDate();
                if (searchDate.equals(from) || searchDate.equals(to) ||
                    (searchDate.isAfter(from) && searchDate.isBefore(to))) {
                    results.add(task);
                }
            }
        }
        return results;
    }

    private ArrayList<Task> searchByKeyword(String keyword) {
        ArrayList<Task> results = new ArrayList<>();
        for (Task task : tasks.getAllTasks()) {
            if (task.getDescription().toLowerCase().contains(keyword)) {
                results.add(task);
            }
        }
        return results;
    }

    private void displayResults(ArrayList<Task> results, LocalDate searchDate) {
        if (results.isEmpty()) {
            if (searchDate != null) {
                ui.showMessage("No tasks found on " +
                    searchDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ".");
            } else {
                ui.showMessage("No matching tasks found.");
            }
            return;
        }

        String header = searchDate != null ?
            "Tasks on " + searchDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ":" :
            "Here are the matching tasks in your list:";
        ui.showMessage(header);
        for (int i = 0; i < results.size(); i++) {
            ui.showMessage((i + 1) + "." + results.get(i));
        }
    }

    public static void main(String[] args) {
        new Idecapitator("./data/idecapitator.txt").run();
    }
}