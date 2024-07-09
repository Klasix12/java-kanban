import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;
import util.Managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskManagerTest {
    static TaskManager taskManager;

    @BeforeEach
    public void createTaskManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void managerReturnsCorrectInMemoryManagers() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        assertEquals(inMemoryTaskManager.getClass(), Managers.getDefault().getClass());
        assertEquals(inMemoryHistoryManager.getClass(), Managers.getDefaultHistory().getClass());
    }

    @Test
    public void taskExistsWhenAddedToManager() {
        Task task = new Task("task", "task desc");
        taskManager.addTask(task);
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    public void tasksWithEqualsIdsIsEquals() {
        Task task1 = new Task("task1", "task1 desc");
        task1.setId(1);
        Task task2 = new Task("task2", "task2 desc");
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    public void epicExistsWhenAddedToManager() {
        Epic epic = new Epic("epic", "epic desc");
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    public void subtaskExistsWhenAddedToManager() {
        Epic epic = new Epic("epic", "epic desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "subtask desc", epic.getId());
        taskManager.addSubtask(subtask);
        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    public void addTaskSubtaskAndEpicToManager() {
        Task task = new Task("task", "task desc");
        taskManager.addTask(task);
        Epic epic = new Epic("epic", "epic desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "subtask desc", epic.getId());
        taskManager.addSubtask(subtask);
        assertTrue(taskManager.getTasks().size() == 1 &&
                taskManager.getSubtasks().size() == 1 &&
                taskManager.getEpics().size() == 1);
    }
}
