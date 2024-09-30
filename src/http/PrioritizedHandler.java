package http;

import com.google.gson.GsonBuilder;

import task.Task;
import manager.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetPrioritizedTasks(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(exchange, response, 200);
    }
}