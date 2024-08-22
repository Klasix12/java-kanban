package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
        Assertions.assertTrue(taskManager.getTasks().isEmpty());
        Assertions.assertTrue(taskManager.getSubtasks().isEmpty());
        Assertions.assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void testFileManagerSaveFewTasks() throws IOException {
        Task task1 = new Task("task 1", "description 1");
        Task task2 = new Task("task 2", "description 2");
        Task task3 = new Task("task 3", "description 3");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        BufferedReader reader = new BufferedReader(new FileReader(tempFile, StandardCharsets.UTF_8));
        ArrayList<Task> tasks = taskManager.getTasks();
        int index = 0;
        reader.readLine();
        while (reader.ready()) {
            Assertions.assertEquals(tasks.get(index).toString(), reader.readLine());
            index++;
        }
    }

    @Test
    public void testFileManagerLoadFewTasks() throws IOException {
        Task task1 = new Task("task 1", "description 1");
        Task task2 = new Task("task 2", "description 2");
        Task task3 = new Task("task 3", "description 3");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        int epic1id = taskManager.addEpic(epic1);
        int epic2id = taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask 1 description", epic1id);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask 2 description", epic2id);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(tempFile);

        Assertions.assertEquals(taskManager.getTasks(), fileManager2.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), fileManager2.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), fileManager2.getSubtasks());

    }


}
