package http;

import com.google.gson.GsonBuilder;
import task.Task;
import manager.ManagerException;
import manager.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetTask(exchange);
                break;
            case "POST":
                handlePostTask(exchange);
                break;
            case "DELETE":
                handleDeleteTask(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Task task = null;
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            task = taskManager.getTaskById(id);
            if (task != null) {
                String response = gson.toJson(task);
                sendText(exchange, response, 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            List<Task> tasks = taskManager.getAllTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, response, 200);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(),
                StandardCharsets.UTF_8))) {
            String json = reader.lines().collect(java.util.stream.Collectors.joining());
            Task task = gson.fromJson(json, Task.class);

            if (task.getId() > 0) {
                taskManager.updateTask(task);
                sendText(exchange, gson.toJson(task), 200);
            } else {
                try {
                    int id = taskManager.addTask(task);
                    sendText(exchange, gson.toJson(task), 201);
                } catch (ManagerException e) {
                    sendHasInteractions(exchange);
                }
            }
        } catch (ManagerException e) {
            sendText(exchange, "Manager Error: " + e.getMessage(), 400);
        } catch (IOException e) {
            sendText(exchange, "Input/Output Error: " + e.getMessage(), 500);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                taskManager.removeTaskById(id);
                sendText(exchange, "Task with id " + id + " deleted", 200);
            } else {
                sendText(exchange, "Task not found", 404);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
