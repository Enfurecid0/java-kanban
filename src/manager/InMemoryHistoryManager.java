package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> list = new ArrayList<>();
    private static final int historyListSize = 10;

    @Override
    public List<Task> getHistory() {
        return List.copyOf(list);
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (list.size() >= historyListSize) {
                list.removeFirst();
            }
            list.add(task);
        }
    }
}
