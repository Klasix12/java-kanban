package service;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String fileName = "tasks.csv";
    private static final String FIELDS = "id,type,name,status,description,duration,startTime,epicId\n";

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
            fileBackedTaskManager.addSubtasksToEpics();
        } catch (IOException | NumberFormatException e) {
            throw new ManagerLoadException("Ошибка при загрузке из файла.");
        }
        return fileBackedTaskManager;
    }

    private void addTaskFromString(String string) {
        Task task = fromString(string);
        int taskId = task.getId();
        if (taskId > generatedId) {
            generatedId = task.getId();
        }
        switch (task.getClass().getSimpleName()) {
            case "Task" -> tasks.put(taskId, task);
            case "Epic" -> epics.put(taskId, (Epic) task);
            case "Subtask" -> subtasks.put(taskId, (Subtask) task);
        }
    }

    private void addSubtasksToEpics() {
        for (Subtask subtask : subtasks.values()) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask);
            }
        }
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].equals("0") ? null : LocalDateTime.parse(fields[5], Task.DATE_TIME_FORMATTER);
        int duration = Integer.parseInt(fields[6]);
        switch (taskType) {
            case TASK -> {
                return new Task(id, name, description, status, duration, startTime);
            }
            case EPIC -> {
                return new Epic(id, name, description, status, duration, startTime);
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[7]);
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            }
            default -> {
                throw new ManagerLoadException("Ошибка при получении задачи из строки.");
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
            throw new ManagerSaveException("Ошибка при сохранении в файл.");
        }
    }

    private ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>(getTasks());
        tasks.addAll(getEpics());
        tasks.addAll(getSubtasks());
        return tasks;
    }
}
