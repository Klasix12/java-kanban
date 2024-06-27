package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description);
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
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
}
