import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.HistoryManager;
import service.TaskManager;
import util.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("task", "");
        Task task1 = new Task("task1", "");
        manager.addTask(task);
        manager.addTask(task1);

        Epic epic = new Epic("epic", "");
        Epic epic1 = new Epic("epic1", "");
        int epicId = manager.addEpic(epic);
        int epic1Id = manager.addEpic(epic1);
        Subtask subtask = new Subtask("subtask", "", epicId);
        Subtask subtask1 = new Subtask("subtask1", "", epicId);
        Subtask subtask2 = new Subtask("subtask2", "", epic1Id);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        printAllTasks(manager);

        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);

        System.out.println("-".repeat(30));
        printAllTasks(manager);

        manager.clearSubtasks();
        printAllTasks(manager);

        for (int i = 0; i < 10; i++) {
            manager.getEpicById(3);
        }

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}