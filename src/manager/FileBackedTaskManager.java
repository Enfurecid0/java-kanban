package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;
import status.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    public static final String Header = "id,type,name,description,status,duration,startTime,epic \n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(Header);
            writer.newLine();
            getAllTasks().stream()
                    .map(Task::toString)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerException("Ошибка сохранения задачи.");
                        }
                    });

            getAllEpics().stream()
                    .map(Epic::toString)
                    .forEach(epicString -> {
                        try {
                            writer.write(epicString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerException("Ошибка сохранения эпика.");
                        }
                    });

            getAllSubtasks().stream()
                    .map(Subtask::toString)
                    .forEach(subtaskString -> {
                        try {
                            writer.write(subtaskString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerException("Ошибка сохранения подзадачи.");
                        }
                    });
        } catch (IOException e) {
            throw new ManagerException("Can't write to file: " + file.getName());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    continue;
                }
                Task task = manager.fromString(line);

                if (task.getType() == TaskType.TASK) {
                    manager.tasks.put(task.getId(), task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Can`t read from file: " + file.getName());
        }
        return manager;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Task updateEpic(Epic epic) {
        Task updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    private Task fromString(String value) {
        String[] line = value.split(",");
        int id = Integer.parseInt(line[0]);
        String taskType = line[1];
        String name = line[2];
        String description = line[3];
        TaskStatus status = TaskStatus.valueOf(line[4]);
        Duration duration = Duration.parse(line[5]);
        LocalDateTime startTime = LocalDateTime.parse(line[6]);

        switch (taskType) {
            case "TASK":
                Task task = new Task(name, description, duration, startTime);
                task.setStatus(status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description, duration, startTime);
                epic.setId(id);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(line[7]);
                Subtask subtask = new Subtask(name, description, epicId, duration, startTime);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип: " + taskType);
        }
    }
}