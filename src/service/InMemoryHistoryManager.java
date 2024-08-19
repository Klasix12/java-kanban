package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node<T> {
        Node<T> next;
        T value;
        Node<T> prev;

        private Node(Node<T> next, T value, Node<T> prev) {
            this.next = next;
            this.value = value;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> start;
    private Node<Task> tail;

    private void linkLast(Task task) {
        Node<Task> node = new Node<>(null, task, tail);
        if (start == null) {
            start = node;
        } else {
            tail.next = node;
        }
        tail = node;
        history.put(task.getId(), node);
    }


    private void removeNode(Node<Task> node) {
        final Node<Task> next = node.next;
        final Node<Task> prev = node.prev;

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
        Node<Task> node = start;
        ArrayList<Task> tasks = new ArrayList<>();
        while (node != null) {
            tasks.add(node.value);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.get(task.getId()) != null) {
                remove(task.getId());
            }
            linkLast(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }
}
