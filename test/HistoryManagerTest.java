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
        Assertions.assertEquals(1, history.size(), "История должна содержать одну задачу");
        Assertions.assertEquals(task1, history.get(0), "Единственная задача в истории должна быть" + task1);
    }

    @Test
    void removeTaskFromHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        manager.remove(1);
        final List<Task> history = manager.getHistory();
        Assertions.assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи");
    }
}
