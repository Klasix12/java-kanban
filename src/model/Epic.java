package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, subtasks);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        Epic otherEpic = (Epic) obj;
        return Objects.equals(name, otherEpic.name) &&
                Objects.equals(description, otherEpic.description) &&
                Objects.equals(id, otherEpic.id) &&
                Objects.equals(status, otherEpic.status) &&
                Objects.equals(subtasks, otherEpic.subtasks);
    }
}
