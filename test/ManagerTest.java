import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagerTest {

    @Test
    void getDefaultShouldInitializeInMemoryTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault(), "Утилитарный класс всегда " +
                "возвращает проинициализированные и готовые к работе экземпляры менеджеров.");
    }

    @Test
    void getDefaultHistoryShouldInitializeInMemoryHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory(), "Утилитарный класс " +
                "всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров.");
    }
}