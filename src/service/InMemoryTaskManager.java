package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int taskId;

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int addTask(Task task) {
        if (task != null) {
            final int taskId = generateId();
            task.setId(taskId);
            tasks.put(taskId, task);
            return task.getId();
        }
        return -1;
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic != null) {
            final int taskId = generateId();
            epic.setId(taskId);
            epics.put(taskId, epic);
            return epic.getId();
        }
        return -1;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            final int taskId = generateId();
            subtask.setId(taskId);
            subtasks.put(taskId, subtask);
            epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            return subtask.getId();
        }
        return -1;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasks()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.getSubtasks().remove((Integer) id);
            subtasks.remove(id);
            historyManager.remove(id);
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Subtask subtask : this.subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        return ++taskId;
    }

    private void updateEpicStatus(int epicId) {
        Status status = calculateStatus(getEpicSubtasksByEpicId(epicId));
        epics.get(epicId).setStatus(status);
    }

    private Status calculateStatus(ArrayList<Subtask> epicSubtasks) {
        if (epicSubtasks.isEmpty()) {
            return Status.NEW;
        }

        Status firstStatus = epicSubtasks.getFirst().getStatus();
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus() != firstStatus) {
                return Status.IN_PROGRESS;
            }
        }
        return firstStatus;
    }
}
