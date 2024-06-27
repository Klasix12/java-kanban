import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Погулять", "Дойти до набережной");
        Task task2 = new Task("Вынести мусор", "");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        System.out.println(taskManager.getTasks());
        task1.setStatus(Status.DONE);
        task2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println(taskManager.getTasks());
        taskManager.removeTaskById(0);
        System.out.println(taskManager.getTasks());
        System.out.println();


        Epic shopping = new Epic("Сходить в магазин", "Купить продукты по списку", new ArrayList<>());
        Subtask shoppingSubtask1 = new Subtask("Купить молоко", "", shopping);
        Subtask shoppingSubtask2 = new Subtask("Купить шоколадку", "", shopping);
        Epic apartmentRenovation = new Epic("Сделать ремонт", "", new ArrayList<>());
        Subtask apartmentRenovationSubtask = new Subtask(
                "Поклеить обои", "Поклеить обои в коридоре", apartmentRenovation);
        taskManager.addEpic(apartmentRenovation);
        taskManager.addSubtask(apartmentRenovationSubtask);
        taskManager.addEpic(shopping);
        taskManager.addSubtask(shoppingSubtask1);
        taskManager.addSubtask(shoppingSubtask2);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        shoppingSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(shoppingSubtask1);
        System.out.println(taskManager.getEpics());
        taskManager.removeSubtaskById(6);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        taskManager.removeEpicById(2);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }
}