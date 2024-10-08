package service;

import exception.NotFoundException;
import exception.TaskIntersectionException;
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
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
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
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException();
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException();
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException();
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int addTask(Task task) {
        if (task == null) {
            return -1;
        }
        if (!isValidTask(task)) {
            throw new TaskIntersectionException();
        }
        final int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
        addTaskToPrioritizedTasks(task);
        return taskId;
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
        if (subtask == null || !(epics.containsKey(subtask.getEpicId()))) {
            return -1;
        }
        if (!isValidTask(subtask)) {
            throw new TaskIntersectionException();
        }
        final int subtaskId = generateId();
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        addTaskToPrioritizedTasks(subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
        updateEpicDetails(subtask.getEpicId());
        return subtask.getId();
    }


    @Override
    public void updateTask(Task task) {
        if (task == null || !(tasks.containsKey(task.getId()))) {
            return;
        }
        if (!(isValidTask(task))) {
            throw new TaskIntersectionException();
        }
        tasks.put(task.getId(), task);
        removeTaskFromPrioritizedTasksById(task.getId());
        addTaskToPrioritizedTasks(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicDetails(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !(subtasks.containsKey(subtask.getId()))) {
            return;
        }
        if (!(isValidTask(subtask))) {
            throw new TaskIntersectionException();
        }
        subtasks.put(subtask.getId(), subtask);
        removeTaskFromPrioritizedTasksById(subtask.getId());
        addTaskToPrioritizedTasks(subtask);
        updateEpicDetails(subtask.getEpicId());
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
            removeTaskFromPrioritizedTasksById(id);
            updateEpicDetails(epic.getId());
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
        prioritizedTasks.add(task);
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
            return true;
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

    private boolean isTasksHaveIntersection(Task newTask, Task oldTask) {
        LocalDateTime newTaskStartTime = newTask.getStartTime();
        LocalDateTime oldTaskStartTime = oldTask.getStartTime();
        if (newTaskStartTime == null || oldTaskStartTime == null) {
            return false;
        }

        LocalDateTime newTaskEndTime = newTask.getEndTime();
        LocalDateTime oldTaskEndTime = oldTask.getEndTime();
        if (newTaskEndTime.isBefore(oldTaskStartTime) || oldTaskEndTime.isBefore(newTaskStartTime) ||
                oldTaskEndTime.isEqual(newTaskStartTime) || newTaskStartTime.isEqual(oldTaskEndTime)) {
            return false;
        }
        return true;
    }

    private int generateId() {
        return ++generatedId;
    }

    protected void updateEpicDetails(int epicId) {
        updateEpicStatus(epicId);
        updateEpicDuration(epicId);
    }

    protected void updateEpicDuration(int epicId) {
        Optional<Epic> epic = Optional.ofNullable(epics.get(epicId));
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
