package util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

public class ManagersTest {
    @Test
    public void testManagersReturnCorrectClasses() {
        InMemoryTaskManager memoryTaskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        assertEquals(Managers.getDefault().getClass(), memoryTaskManager.getClass());
        assertEquals(Managers.getDefaultHistory().getClass(), historyManager.getClass());
    }
}
