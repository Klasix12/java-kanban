package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();
    private static final int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public int getHistorySize() {
        return HISTORY_SIZE;
    }
}
