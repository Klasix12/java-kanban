package service;

public class TaskManagerTest extends AbstractTaskManagerTest<TaskManager> {
    @Override
    protected TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}