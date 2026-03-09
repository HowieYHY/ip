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

/**
 * Custom exception class for Idecapitator application.
 * Used to handle application-specific errors during task management operations.
 */
class IdecapitatorException extends Exception {
    /**
     * Constructs an IdecapitatorException with the specified error message.
     *
     * @param message the error message
     */
    public IdecapitatorException(String message) { super(message); }
}

/**
 * Handles user interface operations including input and output.
 * Manages display of messages and reading of user commands.
 */
class Ui {
    private final String line = "    ____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Displays the welcome message when the application starts.
     */
    public void showWelcome() {
        System.out.println(line + "\n    Hello! I'm Idecapitator\n    What can I do for you?\n" + line);
    }

    /**
     * Displays a decorative line separator.
     */
    public void showLine() { System.out.println(line); }

    /**
     * Reads a command from the user input.
     *
     * @return the command entered by the user
     */
    public String readCommand() { return scanner.nextLine(); }

    /**
     * Displays an error message with a CRITICAL ERROR prefix.
     *
     * @param message the error message to display
     */
    public void showError(String message) { System.out.println("    CRITICAL ERROR: " + message); }

    /**
     * Displays a loading error message when no existing data is found.
     */
    public void showLoadingError() { System.out.println("    NOTICE: No existing data found. Starting fresh!"); }

    /**
     * Displays a general message to the user.
     *
     * @param message the message to display
     */
    public void showMessage(String message) { System.out.println("    " + message); }
}

/**
 * Manages a collection of tasks.
 * Provides methods to add, retrieve, and remove tasks from the list.
 */
class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() { this.tasks = new ArrayList<>(); }

    /**
     * Constructs a TaskList with an existing ArrayList of tasks.
     *
     * @param tasks the ArrayList of tasks to initialize with
     */
    public TaskList(ArrayList<Task> tasks) { this.tasks = tasks; }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add
     */
    public void addTask(Task task) { tasks.add(task); }

    /**
     * Removes and returns a task at the specified index.
     *
     * @param index the index of the task to remove
     * @return the removed task
     */
    public Task delete(int index) { return tasks.remove(index); }

    /**
     * Retrieves a task at the specified index.
     *
     * @param index the index of the task
     * @return the task at the specified index
     */
    public Task get(int index) { return tasks.get(index); }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the size of the task list
     */
    public int size() { return tasks.size(); }

    /**
     * Retrieves all tasks in the list.
     *
     * @return the ArrayList containing all tasks
     */
    public ArrayList<Task> getAllTasks() { return tasks; }
}

/**
 * Handles persistence of tasks by reading from and writing to a file.
 * Supports loading existing tasks and saving task list updates.
 */
class Storage {
    private final String filePath;

    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath the path to the data file
     */
    public Storage(String filePath) { this.filePath = filePath; }

    /**
     * Loads tasks from the data file.
     *
     * @return an ArrayList of tasks loaded from the file
     * @throws IdecapitatorException if file access fails
     */
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

    /**
     * Parses a task from file format string array.
     * Converts pipe-separated values into Task objects based on task type.
     *
     * @param parts the string array containing task data in file format
     * @return a Task object parsed from the data, or null if parsing fails
     */
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

    /**
     * Saves all tasks to the data file in a pipe-separated format.
     *
     * @param tasks the TaskList to save
     */
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

/**
 * Main application class for the Idecapitator task management system.
 * Manages the core application flow, command processing, and task operations.
 * Supports tasks with dates/times in multiple formats with validation.
 */
public class Idecapitator {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Constructs an Idecapitator instance and initializes storage from the specified file.
     * If the file does not exist or cannot be loaded, starts with an empty task list.
     *
     * @param filePath the path to the data file for persistence
     */
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

    /**
     * Parses a date string in yyyy-MM-dd format to a LocalDate object.
     *
     * @param dateString the date string to parse
     * @return a LocalDate object
     * @throws IdecapitatorException if the date format is invalid
     */
    private LocalDate parseDate(String dateString) throws IdecapitatorException {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IdecapitatorException("Invalid date format. Please use yyyy-MM-dd (e.g., 2019-12-02)");
        }
    }

    /**
     * Parses a date/time string in formats: yyyy-MM-dd or yyyy-MM-dd HHmm.
     * If only date is provided, time defaults to 00:00 (start of day).
     *
     * @param input the date/time string to parse
     * @return a LocalDateTime object
     * @throws IdecapitatorException if the format is invalid or time is out of range
     */
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

    /**
     * Parses a 24-hour time string in HHmm format (e.g., 1800 for 6 PM).
     * Validates that hours are 00-23 and minutes are 00-59.
     *
     * @param timeStr the time string to parse
     * @return a LocalTime object
     * @throws IdecapitatorException if the time format or values are invalid
     */
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

    /**
     * Runs the main application loop.
     * Displays welcome message and processes user commands until "bye" is entered.
     * Supports commands: list, mark, unmark, delete, todo, deadline, event, find, and bye.
     */
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

    /**
     * Displays all tasks currently in the task list with their indices.
     */
    private void listTasks() {
        ui.showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showMessage((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Marks a task as done by its index and saves the updated list.
     *
     * @param index the index of the task to mark as done
     */
    private void markTask(int index) {
        tasks.get(index).markAsDone();
        storage.save(tasks);
        ui.showMessage("Nice! I've marked this task as done:\n      " + tasks.get(index));
    }

    /**
     * Unmarks a task (marks as not done) by its index and saves the updated list.
     *
     * @param index the index of the task to unmark
     */
    private void unmarkTask(int index) {
        tasks.get(index).unmarkAsDone();
        storage.save(tasks);
        ui.showMessage("Nice! I've unmarked this task:\n      " + tasks.get(index));
    }

    /**
     * Deletes a task by its index and saves the updated list.
     *
     * @param index the index of the task to delete
     */
    private void deleteTask(int index) {
        Task removed = tasks.delete(index);
        storage.save(tasks);
        ui.showMessage("Noted. I've removed this task:\n      " + removed);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Adds a new task to the list and saves to file.
     *
     * @param task the task to add
     */
    private void addAndSaveTask(Task task) {
        tasks.addTask(task);
        storage.save(tasks);
        ui.showMessage("Got it. I've added this task:\n      " + task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Handles the find command by searching for tasks by keyword or date.
     * Attempts to parse input as a date (yyyy-MM-dd); if parsing fails, searches by keyword.
     *
     * @param searchInput the search keyword or date string
     * @throws IdecapitatorException if search processing fails
     */
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

    /**
     * Searches for tasks occurring on a specific date.
     * Includes Deadline tasks on the exact date and Event tasks that span or overlap the date.
     *
     * @param searchDate the date to search for
     * @return an ArrayList of tasks matching the date criteria
     */
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

    /**
     * Searches for tasks containing a keyword in their description.
     * Search is case-insensitive.
     *
     * @param keyword the keyword to search for
     * @return an ArrayList of tasks matching the keyword
     */
    private ArrayList<Task> searchByKeyword(String keyword) {
        ArrayList<Task> results = new ArrayList<>();
        for (Task task : tasks.getAllTasks()) {
            if (task.getDescription().toLowerCase().contains(keyword)) {
                results.add(task);
            }
        }
        return results;
    }

    /**
     * Displays search results to the user with an appropriate header.
     * Shows a date-based header if searching by date, or keyword-based header if searching by keyword.
     *
     * @param results the ArrayList of matching tasks to display
     * @param searchDate the date searched for (null if keyword search)
     */
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

    /**
     * Entry point for the Idecapitator application.
     * Initializes the application and starts the main loop.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Idecapitator("./data/idecapitator.txt").run();
    }
}