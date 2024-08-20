package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.util.List;

public class HistoryManagerTest {
    static TaskManager taskManager;
    static Task task;

    @BeforeEach
    public void createTaskManager() {
        taskManager = Managers.getDefault();
        task = new Task("task", "");
    }

    @Test
    public void testHistoryManagerAddTask() {
        final int taskId = taskManager.addTask(task);
        taskManager.getTaskById(taskId);
        Assertions.assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void testHistoryManagerReturnCorrectHistory() {
        final int taskId = taskManager.addTask(task);
        taskManager.addTask(task);
        Task historyTask = taskManager.getTaskById(taskId);
        Assertions.assertEquals(task, historyTask);
    }

    @Test
    public void testHistoryManagerHasCorrectSizeWhenGetOneTask20Times() {
        final int taskId = taskManager.addTask(task);
        taskManager.addTask(task);
        for (int i = 0; i < 20; i++) {
            taskManager.getTaskById(taskId);
        }
        Assertions.assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void testHistoryManagerIsEmptyWhenTasksDeleted() {
        final int taskId = taskManager.addTask(task);
        Task task2 = new Task("task2", "");
        final int task2Id = taskManager.addTask(task2);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(taskId);
        taskManager.clearTasks();
        Assertions.assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryManagerListIsEmptyWhenEpicsDeleted() {
        Epic epic = new Epic("Epic", "");
        Epic epic2 = new Epic("Epic2", "");

        final int epicId = taskManager.addEpic(epic);
        final int epic2Id = taskManager.addEpic(epic2);

        Subtask subtask = new Subtask("Subtask", "", epicId);
        Subtask subtask2 = new Subtask("Subtask2", "", epicId);
        Subtask subtask3 = new Subtask("Subtask3", "", epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "", epic2Id);

        final int subtaskId = taskManager.addSubtask(subtask);
        final int subtask2Id = taskManager.addSubtask(subtask2);
        final int subtask3Id = taskManager.addSubtask(subtask3);
        final int subtask4Id = taskManager.addSubtask(subtask4);

        taskManager.getEpicById(epicId);
        taskManager.getEpicById(epic2Id);

        taskManager.getSubtaskById(subtaskId);
        taskManager.getSubtaskById(subtask2Id);
        taskManager.getSubtaskById(subtask3Id);
        taskManager.getSubtaskById(subtask4Id);

        taskManager.clearEpics();
        Assertions.assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryManagerListHasCorrectSizeWhenSubtasksDeleted() {
        Epic epic = new Epic("Epic", "");
        Epic epic2 = new Epic("Epic2", "");

        final int epicId = taskManager.addEpic(epic);
        final int epic2Id = taskManager.addEpic(epic2);

        Subtask subtask = new Subtask("Subtask", "", epicId);
        Subtask subtask2 = new Subtask("Subtask2", "", epicId);
        Subtask subtask3 = new Subtask("Subtask3", "", epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "", epic2Id);

        final int subtaskId = taskManager.addSubtask(subtask);
        final int subtask2Id = taskManager.addSubtask(subtask2);
        final int subtask3Id = taskManager.addSubtask(subtask3);
        final int subtask4Id = taskManager.addSubtask(subtask4);

        taskManager.getEpicById(epicId);
        taskManager.getEpicById(epic2Id);

        taskManager.getSubtaskById(subtaskId);
        taskManager.getSubtaskById(subtask2Id);
        taskManager.getSubtaskById(subtask3Id);
        taskManager.getSubtaskById(subtask4Id);

        taskManager.clearSubtasks();
        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    public void testHistoryManagerListHasCorrectSizeWhenEpicDeleted() {
        Epic epic = new Epic("Epic", "");
        Epic epic2 = new Epic("Epic2", "");

        final int epicId = taskManager.addEpic(epic);
        final int epic2Id = taskManager.addEpic(epic2);

        Subtask subtask = new Subtask("Subtask", "", epicId);
        Subtask subtask2 = new Subtask("Subtask2", "", epicId);
        Subtask subtask3 = new Subtask("Subtask3", "", epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "", epic2Id);

        final int subtaskId = taskManager.addSubtask(subtask);
        final int subtask2Id = taskManager.addSubtask(subtask2);
        final int subtask3Id = taskManager.addSubtask(subtask3);
        final int subtask4Id = taskManager.addSubtask(subtask4);

        taskManager.getEpicById(epicId);
        taskManager.getEpicById(epic2Id);

        taskManager.getSubtaskById(subtaskId);
        taskManager.getSubtaskById(subtask2Id);
        taskManager.getSubtaskById(subtask3Id);
        taskManager.getSubtaskById(subtask4Id);

        taskManager.removeEpicById(epicId);
        Assertions.assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    public void testHistoryManagerCorrectTaskSequence() {
        final int taskId = taskManager.addTask(task);

        Epic epic = new Epic("Epic", "");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "", epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.getTaskById(taskId);
        taskManager.getSubtaskById(subtaskId);
        taskManager.getEpicById(epicId);

        List<Task> tasks = List.of(task, subtask, epic);

        Assertions.assertIterableEquals(tasks, taskManager.getHistory());
    }

    @Test
    public void testHistoryManagerCorrectTaskSequenceWhenTaskRemove() {
        final int taskId = taskManager.addTask(task);

        Epic epic = new Epic("Epic", "");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "", epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.getTaskById(taskId);
        taskManager.getSubtaskById(subtaskId);
        taskManager.getEpicById(epicId);

        taskManager.removeEpicById(epicId);

        List<Task> tasks = List.of(task);

        Assertions.assertIterableEquals(tasks, taskManager.getHistory());
    }
}
