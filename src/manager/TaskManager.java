package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    List<Task> getTasks();

    Task getTask(int id);

    List<Epic> getEpics();

    Task getEpic(int id);

    List<Subtask> getSubtasks();

    List<Subtask> getEpicSubtasks(int id);

    Task getSubtask(int id);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    Task updateTask(Task task);

    Task updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
