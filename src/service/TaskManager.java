package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasks();
    ArrayList<Epic> getEpics();
    ArrayList<Subtask> getSubtasks();
    void clearTasks();
    void clearEpics();
    void clearSubtasks();
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    void updateTask(Task task);
    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    ArrayList<Subtask> getEpicSubtasksByEpicId(int epicId);

    ArrayList<Task> getHistory();
}
