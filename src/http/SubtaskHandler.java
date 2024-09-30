package http;

import com.google.gson.GsonBuilder;
import task.Subtask;
import manager.ManagerException;
import manager.TaskManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                handleGetSubtask(exchange);
                break;
            case "POST":
                handlePostSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtask(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                String response = gson.toJson(subtask);
                sendText(exchange, response, 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            String response = gson.toJson(subtasks);
            sendText(exchange, response, 200);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(),
                StandardCharsets.UTF_8))) {
            String json = reader.lines().collect(java.util.stream.Collectors.joining());
            Subtask subtask = gson.fromJson(json, Subtask.class);

            if (subtask.getId() > 0) {
                taskManager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 200);
            } else {
                try {
                    int id = taskManager.addSubtask(subtask);
                    sendText(exchange, gson.toJson(subtask), 201);
                } catch (ManagerException e) {
                    sendHasInteractions(exchange);
                }
            }
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            taskManager.removeSubtaskById(id);
            sendText(exchange, "Subtask with id " + id + " deleted", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}