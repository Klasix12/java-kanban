package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String fileName = "tasks.csv";
    private static final String FIELDS = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager() {
    }

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public int addTask(Task task) {
        int taskId = super.addTask(task);
        save();
        return taskId;
    }

    @Override
    public int addEpic(Epic epic) {
        int epicId = super.addEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int subtaskId = super.addSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                fileBackedTaskManager.addTaskFromString(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return fileBackedTaskManager;
    }

    private void addTaskFromString(String string) {
        Task task = fromString(string);
        if (task != null) {
            switch (task.getClass().getSimpleName()) {
                case "Task" -> super.addTask(task);
                case "Epic" -> super.addEpic((Epic) task);
                case "Subtask" -> super.addSubtask((Subtask) task);
            }
        }
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String taskType = fields[1];
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        switch (taskType) {
            case "TASK" -> {
                return new Task(id, name, description, status);
            }
            case "EPIC" -> {
                return new Epic(id, name, description, status);
            }
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            }
            default -> {
                return null;
            }
        }
    }

    private void save() {
        try (Writer writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            ArrayList<Task> tasks = getAllTasks();
            writer.write(FIELDS);
            for (Task task : tasks) {
                writer.write(task.toString());
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>(getTasks());
        tasks.addAll(getEpics());
        tasks.addAll(getSubtasks());
        return tasks;
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();

        Task task1 = new Task("Task 1", "Task 1 description");
        Task task2 = new Task("Task 2", "Task 2 description");

        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        Epic epic2 = new Epic("Epic 2", "Epic 2 description");

        int epic1id = fileBackedTaskManager.addEpic(epic1);
        int epic2id = fileBackedTaskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask 1 description", epic1id);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask 2 description", epic2id);

        fileBackedTaskManager.addSubtask(subtask1);
        fileBackedTaskManager.addSubtask(subtask2);

        printFile(fileBackedTaskManager.fileName);
        System.out.println("-".repeat(20));

        FileBackedTaskManager fileBackedTaskManager2 = loadFromFile(new File(fileBackedTaskManager.getFileName()));
        printTasks(fileBackedTaskManager2);
    }

    private static void printFile(String fileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while (bufferedReader.ready()) {
                System.out.println(bufferedReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTasks(FileBackedTaskManager fileBackedTaskManager) {
        for (Task task : fileBackedTaskManager.getAllTasks()) {
            System.out.println(task);
        }
    }
}
