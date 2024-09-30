import http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        HttpURLConnection connection = sendRequest("GET", "/tasks");
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        String responseBody = getResponseBody(connection);
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
    }

    @Test
    void testGetTaskById() throws IOException {
        int taskId = createSampleTask();
        HttpURLConnection connection = sendRequest("GET", "/tasks?id=" + taskId);
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        String responseBody = getResponseBody(connection);
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
    }

    @Test
    void testGetTaskByIdNotFound() throws IOException {
        HttpURLConnection connection = sendRequest("GET", "/tasks?id=9999");
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);

        String responseBody = getResponseBody(connection);
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
    }

    @Test
    void testAddTask() throws IOException {
        String jsonTask = "{ \"name\": \"Test task\", \"description\": \"Test\", \"status\": \"NEW\" }";
        HttpURLConnection connection = sendRequest("POST", "/tasks", jsonTask);
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_CREATED, responseCode);

        String responseBody = getResponseBody(connection);
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
    }

    @Test
    void testDeleteTask() throws IOException {
        int taskId = createSampleTask();
        HttpURLConnection connection = sendRequest("DELETE", "/tasks?id=" + taskId);
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        String responseBody = getResponseBody(connection);
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
    }

    @Test
    void testDeleteTaskNotFound() throws IOException {
        HttpURLConnection connection = sendRequest("DELETE", "/tasks?id=9999");
        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);

        String responseBody = getResponseBody(connection);
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
    }

    private HttpURLConnection sendRequest(String method, String endpoint) throws IOException {
        return sendRequest(method, endpoint, null);
    }

    private HttpURLConnection sendRequest(String method, String endpoint, String jsonInputString) throws IOException {
        URL url = new URL("http://localhost:" + PORT + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (jsonInputString != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonInputString.getBytes());
        }
        connection.connect();
        return connection;
    }

    private int createSampleTask() throws IOException {
        String jsonTask = "{ \"name\": \"Sample Task\", \"description\": \"Sample Description\", \"status\": \"NEW\" }";
        sendRequest("POST", "/tasks", jsonTask);
        return 1;
    }

    private String getResponseBody(HttpURLConnection connection) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
                        ? connection.getInputStream()
                        : connection.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
        }
        return responseBody.toString();
    }
}