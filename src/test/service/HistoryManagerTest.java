package test.service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;
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
