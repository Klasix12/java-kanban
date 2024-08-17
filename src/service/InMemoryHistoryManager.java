package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Node next;
        Task task;
        Node prev;

        private Node(Node next, Task task, Node prev) {
            this.next = next;
            this.task = task;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node> history = new HashMap<>();
    private Node start;
    private Node tail;

    private void linkLast(Task task) {
        Node node = new Node(null, task, tail);
        if (start == null) {
            start = node;
        } else {
            tail.next = node;
        }
        tail = node;
        history.put(task.getId(), node);
    }


    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            start = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }
    }

    private ArrayList<Task> getTasks() {
        Node node = start;
        ArrayList<Task> tasks = new ArrayList<>();
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (history.get(task.getId()) != null) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }
}
