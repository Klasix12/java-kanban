import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("Погулять", "Дойти до набережной");
        Task task2 = new Task("Вынести мусор", "");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        System.out.println(inMemoryTaskManager.getTasks());
        task1.setStatus(Status.DONE);
        task2.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        System.out.println(inMemoryTaskManager.getTasks());
        inMemoryTaskManager.removeTaskById(0);
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println();


        Epic shopping = new Epic("Сходить в магазин", "Купить продукты по списку");
        Subtask shoppingSubtask1 = new Subtask("Купить молоко", "", shopping);
        Subtask shoppingSubtask2 = new Subtask("Купить шоколадку", "", shopping);
        Epic apartmentRenovation = new Epic("Сделать ремонт", "");
        Subtask apartmentRenovationSubtask = new Subtask(
                "Поклеить обои", "Поклеить обои в коридоре", apartmentRenovation);
        inMemoryTaskManager.addEpic(apartmentRenovation);
        inMemoryTaskManager.addSubtask(apartmentRenovationSubtask);
        inMemoryTaskManager.addEpic(shopping);
        inMemoryTaskManager.addSubtask(shoppingSubtask1);
        inMemoryTaskManager.addSubtask(shoppingSubtask2);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
        shoppingSubtask1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(shoppingSubtask1);
        System.out.println(inMemoryTaskManager.getEpics());
        inMemoryTaskManager.removeSubtaskById(6);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
        inMemoryTaskManager.removeEpicById(2);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
    }
}