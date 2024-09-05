package service;

import exception.ManagerLoadException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTaskManager(tempFile.getAbsolutePath());
    }

    @Test
    public void testFileManagerLoadEmptyFile() {
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void testFileManagerSaveFewTasks() throws IOException {
        Task task1 = new Task("task 1", "description 1", 60, LocalDateTime.of(2024, 10, 8, 0, 0, 0));
        Task task2 = new Task("task 2", "description 2", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0));
        Task task3 = new Task("task 3", "description 3", 60, LocalDateTime.of(2024, 10, 8, 2, 2, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        BufferedReader reader = new BufferedReader(new FileReader(tempFile, StandardCharsets.UTF_8));
        List<Task> tasks = taskManager.getTasks();
        int index = 0;
        reader.readLine();
        while (reader.ready()) {
            assertEquals(tasks.get(index).toString(), reader.readLine());
            index++;
        }
    }

    @Test
    public void testFileManagerLoadFewTasks() {
        Task task1 = new Task("task 1", "description 1", 60, LocalDateTime.of(2024, 10, 8, 0, 0, 0));
        Task task2 = new Task("task 2", "description 2", 60, LocalDateTime.of(2024, 10, 8, 1, 1, 0));
        Task task3 = new Task("task 3", "description 3", 60, LocalDateTime.of(2024, 10, 8, 2, 2, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        int epic1id = taskManager.addEpic(epic1);
        int epic2id = taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask 1 description", 60, LocalDateTime.of(2024, 10, 8, 3, 3, 0), epic1id);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask 2 description", 60, LocalDateTime.of(2024, 10, 8, 4, 4, 0), epic2id);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(taskManager.getTasks(), fileManager2.getTasks());
        assertEquals(taskManager.getEpics(), fileManager2.getEpics());
        assertEquals(taskManager.getSubtasks(), fileManager2.getSubtasks());
    }

    @Test
    public void testFileManagerThrowException() {
        assertThrows(ManagerLoadException.class, () -> {
            try (Writer writer = new BufferedWriter(new FileWriter(tempFile, StandardCharsets.UTF_8))) {
                writer.write("asdasd\n");
                writer.write("asdasd");
            }
            FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(tempFile);
        });
    }

    @Test
    public void testFileManagerLoadCorrectEpicDetails() {
        Epic epic = new Epic("epic", "epic desc");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask 1 description", 60, LocalDateTime.of(2024, 10, 8, 1, 0, 0), epicId);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask 2 description", 60, LocalDateTime.of(2024, 10, 8, 3, 0, 0), epicId);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(taskManager2.getEpicById(epicId).get(), epic);
        assertEquals(taskManager2.getSubtasks(), taskManager.getSubtasks());
    }

    @Test
    public void testFileManagerPrioritizedTasks() {
        Epic epic = new Epic("epic", "epic desc");
        final int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask 1 description", epicId);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask 2 description", 60, LocalDateTime.of(2024, 10, 8, 3, 0, 0), epicId);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(taskManager2.getEpicById(epicId).get(), epic);
        assertEquals(2, taskManager2.getPrioritizedTasks().size());
    }
}
