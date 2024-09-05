import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;
import util.Managers;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("task 1", "");
        Task task2 = new Task("task 2", "");
        final int task1Id = taskManager.addTask(task1);
        final int task2Id = taskManager.addTask(task2);

        Epic epic1 = new Epic("epic 1", "");
        Epic epic2 = new Epic("epic 2", "");
        final int epic1Id = taskManager.addEpic(epic1);
        final int epic2Id = taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("subtask 1", "", epic1Id);
        Subtask subtask2 = new Subtask("subtask 2", "", epic1Id);
        Subtask subtask3 = new Subtask("subtask 3", "", epic1Id);
        final int subtask1Id = taskManager.addSubtask(subtask1);
        final int subtask2Id = taskManager.addSubtask(subtask2);
        final int subtask3Id = taskManager.addSubtask(subtask3);

        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic2Id);
        taskManager.getSubtaskById(subtask1Id);
        taskManager.getSubtaskById(subtask2Id);
        taskManager.getSubtaskById(subtask3Id);

        printHistory(taskManager.getHistory());

        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task1Id);

        printHistory(taskManager.getHistory());

        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic2Id);

        printHistory(taskManager.getHistory());

        taskManager.removeTaskById(task1Id);
        taskManager.removeEpicById(epic1Id);

        printHistory(taskManager.getHistory());
    }

    private static void printHistory(List<Task> history) {
        for (Task task : history) {
            System.out.println(task);
        }
        System.out.println("-".repeat(20));
    }
}