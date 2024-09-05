package service;

import model.Task;

import java.util.List;
import java.util.Set;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

    void remove(Set<Integer> ids);
}
