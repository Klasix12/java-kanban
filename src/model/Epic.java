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

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
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
}
