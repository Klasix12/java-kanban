package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description, Status status, int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    public Epic(int id, String name, String description, Status status, int duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, subtasks);
    }
}
