package manager;

import task.Task;
import utils.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) {
                removeNode(history.get(task.getId()));
            }
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    private List<Task> getTasks() {
        List<Task> taskList = new LinkedList<>();
        Node<Task> node = head;
        while (node != null) {
            taskList.add(node.getData());
            node = node.getNext();
        }
        return taskList;
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(task, tail, null);
        tail = newNode;
        history.put(task.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    private void removeNode(Node<Task> taskNode) {
        final Node<Task> next = taskNode.getNext();
        final Node<Task> prev = taskNode.getPrev();
        taskNode.setData(null);

        if (head == taskNode && tail == taskNode) {
            head = null;
            tail = null;
        } else if (head == taskNode && tail != taskNode) {
            head = next;
            head.setPrev(null);
        } else if (head != taskNode && tail == taskNode) {
            tail = prev;
            tail.setNext(null);
        } else {
            prev.setNext(next);
            next.setPrev(prev);
        }
    }
}
