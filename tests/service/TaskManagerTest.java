package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;
import util.Managers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskManagerTest {
    static TaskManager taskManager;
    static Task task;
    static Epic epic;

    @BeforeEach
    public void createTaskManager() {
        taskManager = Managers.getDefault();
        task = new Task("task", "");
        epic = new Epic("epic", "");
    }

    @Test
    public void testTaskManagerCanStoreAnyTypeTasks() {
        final int taskId = taskManager.addTask(task);
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask", "", epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        assertEquals(task, taskManager.getTaskById(taskId));
        assertEquals(epic, taskManager.getEpicById(epicId));
        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
    }

    @Test
    public void testTaskManagerGetAndDeleteAnyTasks() {
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
        taskManager.clearTasks();
        assertTrue(taskManager.getTasks().isEmpty());

        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "", epic.getId());
        taskManager.addSubtask(subtask);
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void testTaskManagerReturnCorrectAnyTaskById() {
        final int taskId = taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));

        final int epicId = taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epicId));

        Subtask subtask = new Subtask("subtask", "", epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
    }

    @Test
    public void testTaskManagerUpdateAnyTask() {
        taskManager.addTask(task);
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        assertEquals(task.getStatus(), taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void testTaskManagerDeleteAnyTask() {
        final int taskId = taskManager.addTask(task);
        taskManager.removeTaskById(taskId);
        assertTrue(taskManager.getTasks().isEmpty());

        final int epicId = taskManager.addEpic(epic);
        final int subtaskId = taskManager.addSubtask(new Subtask("subtask", "", epicId));
        taskManager.removeSubtaskById(subtaskId);
        assertTrue(taskManager.getSubtasks().isEmpty());
        taskManager.removeEpicById(epicId);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void testTaskManagerReturnCorrectEpicSubtasks() {
        final int epicId = taskManager.addEpic(epic);
        Epic epic1 = new Epic("epic1", "");
        final int epic2Id = taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("subtask", "", epicId);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("subtask1", "", epicId);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "", epic2Id);
        taskManager.addSubtask(subtask2);

        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int subtaskId : epic.getSubtasks()) {
            subtasks.add(taskManager.getSubtaskById(subtaskId));
        }

        assertEquals(subtasks, taskManager.getEpicSubtasksByEpicId(epicId));
    }

    @Test
    public void testTaskManagerAssignsCorrectStatusToEpic() {
        final int epicId =  taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask", "", epicId);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("subtask1", "", epicId);
        taskManager.addSubtask(subtask1);
        assertEquals(Status.NEW, epic.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        subtask.setStatus(Status.DONE);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testGeneratedAndSetIdDoNotConflict() {
        final int taskId = taskManager.addTask(task);

        Task task1 = new Task(taskId,"task1", "");
        final int task1Id = taskManager.addTask(task1);
        assertEquals(task1Id, task1.getId());
    }
}
