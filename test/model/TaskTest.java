package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void testTaskHasAllFieldsAndStatusNewWhenCreated() {
        final int taskId = 123;
        final String taskName = "task";
        final String taskDesc = "task desc";
        Task task = new Task(taskId, taskName, taskDesc);

        assertEquals(taskId, task.getId());
        assertEquals(taskName, task.getName());
        assertEquals(taskDesc, task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    public void testTasksEqualsIfTheirIdsEquals() {
        Task task = new Task(123, "task", "task desc");
        Task task1 = new Task(123, "task1", "task desc");

        assertEquals(task, task1);
    }
}
