import org.junit.jupiter.api.Test;
import status.TaskStatus;
import task.Epic;
import task.Task;
import task.Subtask;
import manager.Managers;
import manager.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    @Test
    void taskIdEquals() {
        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Task1", "task number 1", TaskStatus.DONE);
        assertNotEquals(task1, task2, "Экземпляры класса Task равны друг другу, если равен их id;");
    }

    @Test
    void epicIdEquals() {
        Epic epic1 = new Epic(2, "Epic2", "epic number 2", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Epic2", "epic number 2", TaskStatus.DONE);
        assertNotEquals(epic1, epic2, "Наследники класса Task равны друг другу, если равен их id;");
    }

    @Test
    void subtaskIdEquals() {
        Subtask subtask1 = new Subtask(3, "Subtask1", "subtask number 1", TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask(3, "Subtask1", "subtask number 1", TaskStatus.DONE, 2);
        assertNotEquals(subtask1, subtask2, "Наследники класса Task должны быть равны друг другу, " +
                "если равен их id;");
    }

    @Test
    void epicCannotAddedToItselfAsASubtask() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", "epic number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask);
        assertFalse(manager.getEpics().contains(subtask), "Объект Epic нельзя добавить в самого себя " +
                "в виде подзадачи");
    }

    @Test
    public void subtaskCannotBeMadeIntoItsOwnEpic() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", "epic number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask);
        assertFalse(manager.getSubtasks().contains(epic1), "Объект Subtask нельзя сделать своим же эпиком");
    }

    @Test
    void checkIfCanAddDiffTypes() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        Epic epic1 = new Epic(2, "Epic1", "epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask(3, "Subtask1", "subtask number 1", TaskStatus.NEW, 2);
        manager.addSubtask(subtask1);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);
        assertEquals(manager.getHistory().size(), 3);
    }

    @Test
    void immutabilityAddingTaskToManager() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = manager.getTask(1);
        Task[] arrayTwo = new Task[]{task2};
        assertArrayEquals(arrayOne, arrayTwo);
    }

    @Test
    void givenIdGeneratedIdDontConflict() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        manager.addTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = new Task("task2", "task number 2", TaskStatus.NEW);
        final int taskId2 = manager.addTask(task2);
        task2 = new Task(taskId2, task2.getName(), task2.getDescription(), task2.getStatus());
        manager.addTask(task2);
        Task[] arrayTwo = new Task[]{task2};
        assertNotEquals(arrayOne, arrayTwo, "Задачи с заданным id и сгенерированным id не конфликтуют " +
                "внутри менеджера");
    }

    @Test
    void deletedSubtasksShouldNotStoreOldIds() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask(2, "Subtask1", "subtask number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask1);
        manager.removeSubtaskById(2);
        assertTrue(manager.getSubtasks().isEmpty(), "Список подзадач должен быть пустым после удаления подзадачи");
    }

    @Test
    void shouldBeNoIrrelevantIdSubtasksInsideEpics() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", "epic number 1", TaskStatus.NEW);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask(2, "Subtask1", "subtask number 1", TaskStatus.NEW, 1);
        manager.addSubtask(subtask1);
        manager.removeSubtaskById(2);
        List<Subtask> epicSubtasks = manager.getEpicSubtasks(1);
        assertTrue(epicSubtasks.isEmpty(), "Список подзадач эпика должен быть пустым после удаления подзадачи.");
    }

    @Test
    void usingSettersAllowToChangeTheirFields() {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(1, "Task1", "task number 1", TaskStatus.NEW);
        final int taskId1 = manager.addTask(task1);

        Task task2 = new Task("Task2", "task number 2", TaskStatus.NEW);
        task2 = new Task(taskId1, task2.getName(), task2.getDescription(), task2.getStatus());

        assertNotEquals(task1, task2, "Задачи с одинаковыми id должны считаться одинаковыми.");
    }
}