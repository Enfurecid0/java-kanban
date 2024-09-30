import http.HttpTaskServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static final int PORT = 8080;
    private HttpTaskServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void testGetAllTasks() throws IOException {
        int responseCode = sendRequest("GET", "/tasks");
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }

    @Test
    void testGetTaskById() throws IOException {
        int taskId = createSampleTask();
        int responseCode = sendRequest("GET", "/tasks?id=" + taskId);
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }

    @Test
    void testGetTaskByIdNotFound() throws IOException {
        int responseCode = sendRequest("GET", "/tasks?id=9999");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);
    }

    @Test
    void testAddTask() throws IOException {
        String jsonTask = "{ \"name\": \"Test task\", \"description\": \"Test\", \"status\": \"NEW\" }";
        int responseCode = sendRequest("POST", "/tasks", jsonTask);
        assertEquals(HttpURLConnection.HTTP_CREATED, responseCode);
    }

    @Test
    void testDeleteTask() throws IOException {
        int taskId = createSampleTask();
        int responseCode = sendRequest("DELETE", "/tasks?id=" + taskId);
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
    }

    @Test
    void testDeleteTaskNotFound() throws IOException {
        int responseCode = sendRequest("DELETE", "/tasks?id=9999");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);
    }

    private int sendRequest(String method, String endpoint) throws IOException {
        return sendRequest(method, endpoint, null);
    }

    private int sendRequest(String method, String endpoint, String jsonInputString) throws IOException {
        URL url = new URL("http://localhost:" + PORT + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (jsonInputString != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonInputString.getBytes());
        }
        return connection.getResponseCode();
    }

    private int createSampleTask() throws IOException {
        String jsonTask = "{ \"name\": \"Sample Task\", \"description\": \"Sample Description\", \"status\": \"NEW\" }";
        sendRequest("POST", "/tasks", jsonTask);
        return 1;
    }
}