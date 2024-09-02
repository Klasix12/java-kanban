package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import util.Managers;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int generatedId;

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        historyManager.remove(tasks.keySet());
        clearTasksInPrioritizedTasks();
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        historyManager.remove(epics.keySet());
        historyManager.remove(subtasks.keySet());
        clearSubtasksInPrioritizedTasks();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic.getId());
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
        }
        historyManager.remove(subtasks.keySet());
        clearSubtasksInPrioritizedTasks();
        subtasks.clear();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return Optional.ofNullable(epic);
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return Optional.ofNullable(subtask);
    }

    @Override
    public int addTask(Task task) {
        if (task != null) {
            final int taskId = generateId();
            task.setId(taskId);
            tasks.put(taskId, task);
            addTaskToPrioritizedTasks(task);
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
            addTaskToPrioritizedTasks(subtask);
            updateEpicDuration(subtask.getEpicId());
            return subtask.getId();
        }
        return -1;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            addTaskToPrioritizedTasks(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
            updateEpicDuration(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            addTaskToPrioritizedTasks(subtask);
            updateEpicDuration(subtask.getEpicId());
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        removeTaskFromPrioritizedTasksById(id);
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasks()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                removeTaskFromPrioritizedTasksById(subtaskId);
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
            removeTaskFromPrioritizedTasksById(id);
            updateEpicDuration(epic.getId());
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addTaskToPrioritizedTasks(Task task) {
        if (isValidTask(task)) {
            prioritizedTasks.add(task);
        }
    }

    private void removeTaskFromPrioritizedTasksById(int id) {
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    private void clearTasksInPrioritizedTasks() {
        prioritizedTasks.removeIf(task -> task.getClass().equals(Task.class));
    }

    private void clearSubtasksInPrioritizedTasks() {
        prioritizedTasks.removeIf(task -> task.getClass().equals(Subtask.class));
    }

    private boolean isValidTask(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        for (Task prioritizedTask : prioritizedTasks) {
            if (task.getId() == prioritizedTask.getId()) {
                continue;
            }
            if (isTasksHaveIntersection(task, prioritizedTask)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTasksHaveIntersection(Task task1, Task task2) {
        LocalDateTime task1startTime = task1.getStartTime();
        LocalDateTime task2startTime = task2.getStartTime();

        if (task1startTime == null || task2startTime == null) {
            return false;
        }
        if (task1.getEndTime().isBefore(task2.getStartTime()) || task2.getEndTime().isBefore(task1.getStartTime())) {
            return false;
        }
        return true;
    }

    private int generateId() {
        return ++generatedId;
    }

    protected void updateEpicDuration(int epicId) {
        Optional<Epic> epic = getEpicById(epicId);
        if (epic.isEmpty()) {
            return;
        }

        List<Subtask> epicSubtasks = getEpicSubtasksByEpicId(epicId);
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        int duration = 0;

        for (Subtask subtask : epicSubtasks) {
            if (startTime == null) {
                startTime = subtask.getStartTime();
            }
            if (endTime == null && subtask.getStartTime() != null) {
                endTime = subtask.getEndTime();
            }
            if (startTime != null && subtask.getStartTime() != null) {
                if (subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
                duration += subtask.getDuration();
            }
        }

        epic.get().setStartTime(startTime);
        epic.get().setEndTime(endTime);
        epic.get().setDuration(duration);
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
