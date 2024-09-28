import task.Task;
import task.TaskType;
import manager.FileBackedTaskManager;
import status.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private File tempFile;

    @BeforeEach
    void beforeEach() throws IOException {
        tempFile = File.createTempFile("file_backed_taskManager_test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void savingAndLoadingEmptyFile() {
        List<Task> tasks = fileBackedTaskManager.getAllTasks();
        Assertions.assertTrue(tasks.isEmpty(), "Список задач должен быть пуст после загрузки " +
                "из пустого файла.");
    }

    @Test
    void savingAndLoadingMultipleTasks() throws IOException {
        Task task1 = new Task(1, TaskType.TASK, "Task1", "task number 1", TaskStatus.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        fileBackedTaskManager.addTask(task1);

        Task task2 = new Task(2, TaskType.TASK, "Task2", "task number 2", TaskStatus.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusHours(1));
        fileBackedTaskManager.addTask(task2);

        if (!tempFile.exists()) {
            System.out.println("Файл не был создан: " + tempFile.getAbsolutePath());
        } else {
            System.out.println("Файл успешно сохранен: " + tempFile.getAbsolutePath());
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getAllTasks();

        Assertions.assertEquals(2, loadedTasks.size(), "Должно быть загружено столько же задач, " +
                "сколько сохранено");
        Assertions.assertEquals(task1, loadedTasks.get(0), "Задача должна совпадать с сохраненной.");
        Assertions.assertEquals(task2, loadedTasks.get(1), "Задача должна совпадать с сохраненной.");
        fileBackedTaskManager.removeTasks();
    }
}