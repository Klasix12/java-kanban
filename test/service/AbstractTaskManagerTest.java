package service;

import exception.TaskIntersectionException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//
public abstract class AbstractTaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setTaskManager() {
        taskManager = createTaskManager();
        task = new Task("task", "", 60, LocalDateTime.of(2024, 10, 8, 0, 0, 0));
        epic = new Epic("epic", "");
    }

    @Test
    public void testTaskManagerCanStoreAnyTypeTasks() {
        final int taskId = taskManager.addTask(task);
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0), epicId);
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

        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0), epicId);
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

        Subtask subtask = new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0), epicId);
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
        final int subtaskId = taskManager.addSubtask(new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0), epicId));
        taskManager.removeSubtaskById(subtaskId);
        assertTrue(taskManager.getSubtasks().isEmpty());
        taskManager.removeEpicById(epicId);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void testTaskManagerReturnCorrectEpicSubtasks() {
        final int epicId = taskManager.addEpic(epic);
        Epic epic1 = new Epic("epic1", "");
        taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0), epicId);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("subtask1", "", 60, LocalDateTime.of(2024, 10, 8, 2, 2, 0), epicId);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "", 60, LocalDateTime.of(2024, 10, 8, 3, 3, 0), epicId);
        taskManager.addSubtask(subtask2);

        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int subtaskId : epic.getSubtasks()) {
            subtasks.add(taskManager.getSubtaskById(subtaskId));
        }

        assertEquals(subtasks, taskManager.getEpicSubtasksByEpicId(epicId));
    }

    @Test
    public void testTaskManagerAssignsCorrectStatusToEpic() {
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0), epicId);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("subtask1", "", 60, LocalDateTime.of(2024, 10, 8, 2, 2, 0), epicId);
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
    public void testSubtasksDeletedWhenEpicsDeleted() {
        final int epicId = taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0), epicId));
        taskManager.clearEpics();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void testGeneratedAndSetIdDoNotConflict() {
        final int taskId = taskManager.addTask(task);

        Task task1 = new Task(taskId, "task2", "", Status.NEW, 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0));
        final int task1Id = taskManager.addTask(task1);
        assertEquals(task1Id, task1.getId());
    }

    @Test
    public void testTasksIntersection() {
        final int taskId = taskManager.addTask(task);

        Task task2 = new Task(taskId + 1, "task1", "task1 desc", Status.NEW, 60, LocalDateTime.of(2024, 10, 8, 0, 0, 0));
        assertThrows(TaskIntersectionException.class, () -> taskManager.addTask(task2));
        assertEquals(1, taskManager.getPrioritizedTasks().size());

        Task task3 = new Task(task2.getId() + 1, "task2", "task2 desc", Status.NEW, 60, LocalDateTime.of(2024, 10, 8, 0, 30, 0));
        assertThrows(TaskIntersectionException.class, () -> taskManager.addTask(task3));
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getTasks().size());

        task3.setStartTime(LocalDateTime.of(2024, 10, 8, 2, 0, 0));
        taskManager.addTask(task3);
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    public void testEpicHasCorrectStartFinishAndDurationTime() {
        Epic epic = new Epic("epic", "description");

        final int epicId = taskManager.addEpic(epic);

        LocalDateTime subtaskStartTime = LocalDateTime.of(2024, 10, 8, 0, 30, 0);
        Subtask subtask = new Subtask("subtask", "desc", 60, subtaskStartTime, epicId);
        taskManager.addSubtask(subtask);

        LocalDateTime subtask1StartTime = LocalDateTime.of(2024, 10, 10, 0, 30, 0);
        Subtask subtask1 = new Subtask("subtask1", "desc1", 60, subtask1StartTime, epicId);
        taskManager.addSubtask(subtask1);

        assertEquals(subtaskStartTime, epic.getStartTime());
        assertEquals(subtask1.getEndTime(), epic.getEndTime());
        assertEquals(60 + 60, epic.getDuration());

        taskManager.removeSubtaskById(subtask.getId());

        assertEquals(subtask1StartTime, epic.getStartTime());
        assertEquals(subtask1.getEndTime(), epic.getEndTime());
        assertEquals(60, epic.getDuration());

        taskManager.removeSubtaskById(subtask1.getId());

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(0, epic.getDuration());
    }

    @Test
    public void testTaskListHaveCorrectSizeWhenTaskHasAndNotHaveStartTime() {
        Task task2 = new Task("task2", "");
        taskManager.addTask(task);
        taskManager.addTask(task2);
        assertEquals(2, taskManager.getTasks().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());

    }

    @Test
    public void testSubtasksAndEpicsListsHaveCorrectSize() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "", 60, LocalDateTime.of(2024, 10, 8, 0, 0, 0), epicId);
        taskManager.addSubtask(subtask);
        Subtask subtask1 = new Subtask("subtask1", "", epicId);
        taskManager.addSubtask(subtask1);

        assertEquals(2, taskManager.getSubtasks().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());

        subtask.setStartTime(null);
        taskManager.updateSubtask(subtask);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertNull(taskManager.getEpicById(epicId).getEndTime());
    }

    @Test
    public void testTasksHaveNotIntersectionIfSameStartTimeAndEndTime() {
        taskManager.addTask(task);
        Task task2 = new Task("task2", "", 60, task.getStartTime().plusMinutes(task.getDuration()));
        taskManager.addTask(task2);
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void testPrioritizedTasksHaveCorrectSizeWhenTasksUpdated() {
        taskManager.addTask(task);
        Epic epic = new Epic("epic", "epic desc");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("name", "desc", epicId);
        taskManager.addSubtask(subtask);

        assertEquals(2, taskManager.getPrioritizedTasks().size());
        task.setStartTime(LocalDateTime.of(2024, 10, 8, 0, 0, 0));
        taskManager.updateTask(task);
        subtask.setStartTime(LocalDateTime.of(2024, 10, 8, 0, 0, 0));
        assertThrows(TaskIntersectionException.class, () -> taskManager.updateSubtask(subtask));
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }
}