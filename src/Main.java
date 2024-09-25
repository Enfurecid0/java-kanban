
import manager.Managers;
import manager.TaskManager;
import status.TaskStatus;
import task.Task;
import task.Epic;
import task.Subtask;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("task1", "task number1", TaskStatus.NEW);
        final int taskId1 = manager.addTask(task1);
        task1 = new Task(taskId1, task1.getName(), task1.getDescription(), task1.getStatus());

        Epic epic1 = new Epic("epic1", "epic number1", TaskStatus.NEW);
        final int epicId1 = manager.addEpic(epic1);
        epic1 = new Epic(epicId1, epic1.getName(), epic1.getDescription(), epic1.getStatus());

        Subtask subtask1 = new Subtask("subtask1", "subtask number1", TaskStatus.NEW, epicId1);
        final int subtaskId1 = manager.addSubtask(subtask1);
        subtask1 = new Subtask(subtaskId1, subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(), epicId1);

        Subtask subtask2 = new Subtask("subtask2", "subtask number2", TaskStatus.DONE, epicId1);
        final int subtaskId2 = manager.addSubtask(subtask2);
        subtask2 = new Subtask(subtaskId2, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(), epicId1);

        Task task2 = new Task("task2", "task number 2", TaskStatus.NEW);
        final int taskId2 = manager.addTask(task2);
        task2 = new Task(taskId2, task2.getName(), task2.getDescription(), task2.getStatus());

        Epic epic2 = new Epic("epic2", "epic number2", TaskStatus.NEW);
        final int epicId2 = manager.addEpic(epic2);
        epic2 = new Epic(epicId2, epic2.getName(), epic2.getDescription(), epic2.getStatus());

        Subtask subtask3 = new Subtask("subtask3", "subtask number3", TaskStatus.DONE, epicId2);
        final int subtaskId3 = manager.addSubtask(subtask3);
        subtask3 = new Subtask(subtaskId3, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(), epicId2);


        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);
        manager.getSubtask(4);
        manager.getTask(5);
        manager.getEpic(6);
        manager.getTask(100);
        manager.getEpic(2);
        manager.getTask(15);
        manager.getSubtask(3);
        manager.getEpic(6);
        manager.getTask(5);
        manager.getSubtask(4);
        manager.getSubtask(7);
        manager.getSubtask(3);
        manager.getEpic(6);
        printAllTasks(manager);
    }
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
