import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("task1", "task1 desc");
        Task task2 = new Task("task2", "");
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


        Epic epic1 = new Epic("Epic1", "epic1 desc");
        inMemoryTaskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "", epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "", epic1.getId());
        Epic epic2 = new Epic("Epic2", "epic2 desc");
        inMemoryTaskManager.addEpic(epic2);
        Subtask apartmentRenovationSubtask = new Subtask( "subtask3", "", epic2.getId());
        inMemoryTaskManager.addSubtask(apartmentRenovationSubtask);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
        subtask1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        System.out.println(inMemoryTaskManager.getEpics());
        inMemoryTaskManager.removeSubtaskById(6);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
        inMemoryTaskManager.removeEpicById(2);
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
    }
}