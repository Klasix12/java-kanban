package service;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

public class HistoryManagerTest {
    static TaskManager taskManager;
    final static int HISTORY_SIZE = 10;
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

    @Test public void testHistoryManagerStoryCorrectHistorySize() {
        final int taskId = taskManager.addTask(task);
        taskManager.addTask(task);
        for (int i = 0; i < 20; i++) {
            taskManager.getTaskById(taskId);
        }
        Assertions.assertEquals(HISTORY_SIZE, taskManager.getHistory().size());
    }
}
