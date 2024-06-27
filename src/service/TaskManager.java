package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int taskId;

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void addTask(Task task) {
        task.setId(taskId);
        tasks.put(taskId++, task);
    }

    public void addEpic(Epic epic) {
        epic.setId(taskId);
        epics.put(taskId++, epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(taskId);
        subtasks.put(taskId++, subtask);
        subtask.getEpicTask().addSubtask(subtask);
        updateEpicStatus(subtask.getEpicTask());
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())){
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicTask());
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        Epic epic = subtasks.get(id).getEpicTask();
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    public HashMap<Integer, Subtask> getEpicSubtasks(Epic epic) {
        HashMap<Integer, Subtask> epicSubtasks = new HashMap<>();
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            if (entry.getValue().getEpicTask().equals(epic)) {
                epicSubtasks.put(entry.getKey(), entry.getValue());
            }
        }
        return epicSubtasks;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (int i = 1; i < epic.getSubtasks().size(); i++) {
            if (epic.getSubtasks().get(i).getStatus() !=
                    epic.getSubtasks().get(i - 1).getStatus()) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        epic.setStatus(epic.getSubtasks().getFirst().getStatus());
    }
}
