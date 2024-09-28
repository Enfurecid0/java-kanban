import org.junit.jupiter.api.Test;
import status.TaskStatus;
import task.Epic;
import task.Task;
import task.Subtask;
import task.TaskType;
import manager.Managers;
import manager.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    @Test
    void taskIdEquals() {
        Task task1 = new Task(1, "Task1", "Task number 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task1", "Task number 1", TaskStatus.DONE);
        assertNotEquals(task1, task2, "Экземпляры класса Task равны друг другу, если равен их id;");
    }

    @Test
    void epicIdEquals() {
        Epic epic1 = new Epic(2, "Epic2", "Epic number 2", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Epic2", "Epic number 2", TaskStatus.DONE);
        assertNotEquals(epic1, epic2, "Наследники класса Task равны друг другу, если равен их id;");
    }

    @Test
    void subtaskIdEquals() {
        Subtask subtask1 = new Subtask(3, "Subtask1", "Subtask number 1", TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask(3, "Subtask1", "Subtask number 1", TaskStatus.DONE, 2);
        assertNotEquals(subtask1, subtask2, "Наследники класса Task должны быть равны друг другу, если равен их id;");
    }

    @Test
    void epicCannotAddedToItselfAsASubtask() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "Epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", "Epic number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask);
        assertFalse(manager.getEpics().contains(subtask), "Объект Epic нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    public void subtaskCannotBeMadeIntoItsOwnEpic() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "Epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", "Epic number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask);
        assertFalse(manager.getSubtasks().contains(epic1), "Объект Subtask нельзя сделать своим же эпиком");
    }

    @Test
    void checkIfCanAddDiffTypes() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", "Task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        Epic epic1 = new Epic(2, "Epic1", "Epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask(3, "Subtask1", "Subtask number 1", TaskStatus.NEW, 2);
        manager.addSubtask(subtask1);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);
        assertEquals(manager.getHistory().size(), 3);
    }

    @Test
    void immutabilityAddingTaskToManager() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", "Task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = manager.getTask(1);
        Task[] arrayTwo = new Task[]{task2};
        assertArrayEquals(arrayOne, arrayTwo);
    }

    @Test
    void givenIdGeneratedIdDontConflict() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", "Task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = new Task("task2", "Task number 2", TaskStatus.NEW);
        final int taskId2 = manager.addTask(task2);
        task2 = new Task(taskId2, task2.getName(), task2.getDescription(), task2.getStatus());
        manager.addTask(task2);
        Task[] arrayTwo = new Task[]{task2};
        assertNotEquals(arrayOne, arrayTwo, "Задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера");
    }

    @Test
    void deletedSubtasksShouldNotStoreOldIds() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "Epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask(2, "Subtask1", "Subtask number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask1);
        manager.removeSubtaskById(2);
        assertNotEquals(manager.getSubtasks(), "Удаляемая подзадача не хранит в себе старый Id");
    }

    @Test
    void shouldBeNoIrrelevantIdSubtasksInsideEpics() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "Epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask(2, "Subtask1", "Subtask number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask1);
        manager.removeSubtaskById(2);
        List<Subtask> epicSubtasks = manager.getEpicSubtasks(1);
        assertTrue(epicSubtasks.isEmpty(), "Список подзадач эпика должен быть пустым после удаления подзадачи.");
    }

    @Test
    void usingSettersAllowToChangeTheirFields() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(1, "Task1", "Task number 1", TaskStatus.NEW);
        final int taskId1 = manager.addTask(task1);

        Task task2 = new Task("Task2", "Task number 2", TaskStatus.NEW);
        task2 = new Task(taskId1, task2.getName(), task2.getDescription(), task2.getStatus());

        assertNotEquals(task1, task2, "Задачи с одинаковыми id должны считаться одинаковыми.");
    }

    @Test
    public void checkEpicsStatusIsNew() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(1, TaskType.EPIC, "Epic", "Epic number", TaskStatus.DONE,
                new ArrayList());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask(2, TaskType.SUBTASK, "Subtask1", "Subtask number 1", TaskStatus.NEW,
                epic.getId());
        Subtask subtask2 = new Subtask(3, TaskType.SUBTASK, "Subtask2", "Subtask number 2", TaskStatus.NEW,
                epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void checkEpicsStatusIsDone() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(1, TaskType.EPIC, "Epic", "Epic number", TaskStatus.DONE,
                new ArrayList());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask(2, TaskType.SUBTASK, "Subtask1", "Subtask number 1", TaskStatus.DONE,
                epic.getId());
        Subtask subtask2 = new Subtask(3, TaskType.SUBTASK, "Subtask2", "Subtask number 2", TaskStatus.DONE,
                epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void checkEpicsStatusIsInProgress() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(1, TaskType.EPIC, "Epic", "Epic number", TaskStatus.NEW,
                new ArrayList());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask(2, TaskType.SUBTASK, "Subtask1", "Subtask number 1", TaskStatus.DONE,
                epic.getId());
        Subtask subtask2 = new Subtask(3, TaskType.SUBTASK, "Subtask2", "Subtask number 2", TaskStatus.NEW,
                epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkSubtaskNotEmpty() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic("Epic", "Epic number");
        manager.addEpic(epic1);
        Subtask subtaskWithTime = new Subtask(2, TaskType.SUBTASK, "Subtask1", "Subtask number 1", TaskStatus.NEW,
                Duration.ofMinutes(120), LocalDateTime.now(), epic1.getId());
        manager.addSubtask(subtaskWithTime);
        Epic epic = manager.getEpicById(subtaskWithTime.getEpicId());
        assertNotNull(subtaskWithTime);
        assertEquals(subtaskWithTime.getDuration(), epic.getDuration());
        assertEquals(subtaskWithTime.getStartTime(), epic.getStartTime());
        assertEquals(epic.getEndTime(), epic.getStartTime().plus(epic.getDuration()));
    }

    @Test
    public void shouldNotAddOverlappingTasks() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Task1", "Task number 1", Duration.ofMinutes(10), LocalDateTime.now());
        manager.addTask(task1);
        Task task2 = new Task("Task2", "Task number 2", Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(5));

        int initialSize = manager.getPrioritizedTasks().size();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addTask(task2);
        });

        assertEquals(initialSize, manager.getPrioritizedTasks().size(), "Пересекающаяся задача не должна добавляться");
        assertEquals("Задача пересекается с другой задачей и не может быть добавлена.", exception.getMessage());
    }
}
