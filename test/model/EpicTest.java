package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest{
    @Test
    public void testEpicHasAllFieldsAndStatusNewWhenCreated() {
        final int epicId = 123;
        final String epicName = "task";
        final String epicDesc = "task desc";
        Epic epic = new Epic(epicId, epicName, epicDesc);

        assertEquals(epicId, epic.getId());
        assertEquals(epicName, epic.getName());
        assertEquals(epicDesc, epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testEpicHasSubtasks() {
        Epic epic = new Epic("epic", "epic desc");
        Subtask subtask = new Subtask("subtask", "subtask desc", epic.getId());
        epic.addSubtask(subtask);
        assertEquals(1, epic.getSubtasks().size());
        assertEquals(subtask.getId(), epic.getSubtasks().getFirst());
    }

    @Test
    public void testTasksEqualsIfTheirIdsEquals() {
        Epic epic = new Epic(123, "epic", "");
        Epic epic1 = new Epic(123, "epic", "");

        assertEquals(epic, epic1);
    }
}
