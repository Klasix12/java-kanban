package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskManagerTest extends AbstractTaskManagerTest<TaskManager> {
    @Override
    protected TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}