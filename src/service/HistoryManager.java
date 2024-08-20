package service;

import model.Task;

import java.util.ArrayList;
import java.util.Set;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();

    void remove(int id);

    void remove(Set<Integer> ids);
}
