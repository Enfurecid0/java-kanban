package manager;

import status.TaskStatus;
import task.Task;
import task.Epic;
import task.Subtask;

import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>();

    protected int generatorId = 0;

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public int addTask(Task task) {
        if (overlapTask(task)) {
            return -1;
        }
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        updatePrioritizedTasks();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        overlapTask(epic);
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        overlapTask(subtask);
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        epic.setDuration(getDuration(epic));
        epic.setStartTime(getStartTime(epic));
        epic.setEndTime(getEndTime(epic));
        updatePrioritizedTasks();
        updateEpicStatus(epic);
        return id;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.addTask(task);
        return task;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = epics.get(id);
        return epic.getSubtaskList();
    }

    @Override
    public Task getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.removeSubtasks();
            epic.setStatus(TaskStatus.NEW);
        });
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public Task updateTask(Task task) {
        overlapTask(task);
        tasks.put(task.getId(), task);
        updatePrioritizedTasks();
        return task;
    }

    @Override
    public Task updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
        updatePrioritizedTasks();
        return oldEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtask.getId());
        oldSubtask.setName(subtask.getName());
        oldSubtask.setStatus(subtask.getStatus());
        Epic epic = epics.get(epicId);
        epic.setDuration(getDuration(epic));
        epic.setStartTime(getStartTime(epic));
        epic.setEndTime(getEndTime(epic));
        updatePrioritizedTasks();
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        List<Subtask> epicSubtasks = epics.remove(id).getSubtaskList();
        epicSubtasks.stream()
                .map(Subtask::getId)
                .forEach(subtasks::remove);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            List<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(subtask);
            updateEpicStatus(epic);
        } else {
            System.out.println("Такой подзадачи нету");
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskList().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            List<Subtask> subtaskList = epic.getSubtaskList();
            long doneCount = subtaskList.stream()
                    .filter(subtask -> subtask.getStatus() == TaskStatus.DONE)
                    .count();

            long newCount = subtaskList.stream()
                    .filter(subtask -> subtask.getStatus() == TaskStatus.NEW)
                    .count();

            boolean hasOtherStatus = subtaskList.stream()
                    .anyMatch(subtask -> subtask.getStatus() != TaskStatus.DONE && subtask.getStatus() != TaskStatus.NEW);

            if (hasOtherStatus) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else if (doneCount == subtaskList.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (newCount == subtaskList.size()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private LocalDateTime getStartTime(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime getEndTime(Epic epic) {
        return Optional.ofNullable(epic.getStartTime())
                .map(startTime -> startTime.plus(epic.getDuration()))
                .orElse(null);
    }

    private Duration getDuration(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        return subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private void updatePrioritizedTasks() {
        prioritizedTasks.clear();
        for (Task task : getTasks()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
        for (Subtask subtask : getSubtasks()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    private boolean overlapTask(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime taskEndTime = task.getEndTime();
            LocalDateTime prioritizedTaskEndTime = prioritizedTask.getEndTime();
            if (taskEndTime.isAfter(prioritizedTask.getStartTime()) && task.getStartTime().isBefore(prioritizedTaskEndTime)) {
                return true;
            }
        }
        return false;
    }
}