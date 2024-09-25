import task.Task;
import manager.HistoryManager;
import manager.Managers;
import status.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HistoryManagerTest {
    @Test
    void addTaskInHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        final List<Task> history = manager.getHistory();
        Assertions.assertNotEquals(history, "В истории просмотров есть " + task1);
    }

    @Test
    void removeTaskFromHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        manager.remove(1);
        final List<Task> history = manager.getHistory();
        Assertions.assertNotEquals(history, "История просмотров пуста");
    }
}
