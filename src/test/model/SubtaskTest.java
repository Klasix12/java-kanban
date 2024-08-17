package test.model;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    Epic epic;
    @BeforeEach
    public void createEpic() {
        epic = new Epic(123, "epic", "");
    }

    @Test
    public void testSubtaskHasAllFieldsAndStatusWhenCreated() {
        final int subtaskId = 123;
        final String subtaskName = "task";
        final String subtaskDesc = "task desc";
        Subtask subtask = new Subtask(subtaskId, subtaskName, subtaskDesc, epic.getId());

        assertEquals(subtaskId, subtask.getId());
        assertEquals(subtaskName, subtask.getName());
        assertEquals(subtaskDesc, subtask.getDescription());
        Assertions.assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    public void testTasksEqualsIfTheirIdsEquals() {
        Subtask subtask = new Subtask(123, "subtask", "", epic.getId());
        Subtask subtask1 = new Subtask(123, "subtask", "", epic.getId());

        assertEquals(subtask, subtask1);
    }
}
