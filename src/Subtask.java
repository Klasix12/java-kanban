public class Subtask extends Task {
    private final Epic epicTask;

    public Subtask(String name, String description, Epic epicTask) {
        super(name, description);
        this.epicTask = epicTask;
    }

    public Epic getEpicTask() {
        return epicTask;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
