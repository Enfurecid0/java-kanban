package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;
import status.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    public static final String Header = "id,type,name,description,status, epic \n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(Header);
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString());
                writer.newLine();
                for (Subtask subtask : getEpicSubtasks(epic.getId())) {
                    writer.write(subtask.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Can't write to file: " + file.getName());
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
            throw new ManagerSaveException("Can`t read from file: " + file.getName());
        }
        return manager;
    }

    @Override
    public int addTask(Task task) {
        save();
        return super.addTask(task);
    }

    @Override
    public int addEpic(Epic epic) {
        save();
        return super.addEpic(epic);
    }

    @Override
    public int addSubtask(Subtask subtask) {
        save();
        return super.addSubtask(subtask);
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
        save();
        return super.updateTask(task);
    }

    @Override
    public Task updateEpic(Epic epic) {
        save();
        return super.updateEpic(epic);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        save();
        return super.updateSubtask(subtask);
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

        switch (taskType) {
            case "TASK":
                Task task = new Task(name, description);
                task.setStatus(status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(line[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Unknown type: " + taskType);
        }
    }
}