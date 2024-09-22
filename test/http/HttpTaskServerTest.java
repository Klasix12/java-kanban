package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import serialization.LocalDateTimeAdapter;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    // tasks
    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "");
        Task task1 = new Task("Task1", "");
        Task task2 = new Task("Task2", "");

        manager.addTask(task);
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/tasks"));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(tasksFromResponse);
        assertEquals(manager.getTasks().size(), tasksFromResponse.size());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test", "");
        int taskId = manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = createGetRequest(URI.create("http://localhost:8080/tasks/" + taskId));
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(task, gson.fromJson(response.body(), Task.class));
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createPostRequest(URI.create("http://localhost:8080/tasks"), taskJson);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test", tasksFromManager.get(0).getName());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createPostRequest(URI.create("http://localhost:8080/tasks"), taskJson);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        task.setId(1);
        task.setDescription("description");
        taskJson = gson.toJson(task);
        request = createPostRequest(URI.create("http://localhost:8080/tasks"), taskJson);
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(task.getDescription(), manager.getTaskById(1).getDescription());
    }

    @Test
    public void testRemoveTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "");
        int taskId = manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = createDeleteRequest(URI.create("http://localhost:8080/tasks/" + taskId));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }

    // subtasks
    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("subtask", "", epicId);
        Subtask subtask1 = new Subtask("subtask1", "", epicId);
        Subtask subtask2 = new Subtask("subtask2", "", epicId);

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/subtasks"));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> subtasksFromResponse = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertNotNull(subtasksFromResponse);
        assertEquals(manager.getSubtasks().size(), subtasksFromResponse.size());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "", epicId);
        int subtaskId = manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = createGetRequest(URI.create("http://localhost:8080/subtasks/" + subtaskId));
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(subtask, gson.fromJson(response.body(), Subtask.class));
    }

    @Test
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "", epicId);
        String jsonSubtasks = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createPostRequest(URI.create("http://localhost:8080/subtasks"), jsonSubtasks);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager);
        assertEquals(1, subtasksFromManager.size());
        assertEquals("Subtask", subtasksFromManager.get(0).getName());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "", epicId);
        String jsonSubtask = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createPostRequest(URI.create("http://localhost:8080/subtasks"), jsonSubtask);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        subtask.setId(epicId + 1);
        subtask.setDescription("description");
        jsonSubtask = gson.toJson(subtask);
        request = createPostRequest(URI.create("http://localhost:8080/subtasks"), jsonSubtask);
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(subtask.getDescription(), manager.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    public void testRemoveSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "", epicId);
        int subtaskId = manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest createRequest = createDeleteRequest(URI.create("http://localhost:8080/subtasks/" + subtaskId));
        HttpResponse<String> response = client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubtasks().size());
    }

    // epics
    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        Epic epic1 = new Epic("Epic", "");
        Epic epic2 = new Epic("Epic", "");

        manager.addEpic(epic);
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/epics"));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> epicsFromResponse = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertNotNull(epicsFromResponse);
        assertEquals(manager.getEpics().size(), epicsFromResponse.size());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/epics/" + epicId));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(epic, gson.fromJson(response.body(), Epic.class));
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);

        Subtask subtask = new Subtask("subtask", "", epicId);
        Subtask subtask1 = new Subtask("subtask1", "", epicId);
        Subtask subtask2 = new Subtask("subtask2", "", epicId);

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> responseSubtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(manager.getEpicSubtasksByEpicId(epicId), responseSubtasks);
    }

    @Test
    public void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        String jsonEpic = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createPostRequest(URI.create("http://localhost:8080/epics"), jsonEpic);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getEpics().size());
        assertEquals("Epic", manager.getEpics().getFirst().getName());
    }

    @Test
    public void testRemoveEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "");
        int epicId = manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest createRequest = createDeleteRequest(URI.create("http://localhost:8080/epics/" + epicId));
        HttpResponse<String> response = client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getEpics().size());
    }

    // history
    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Task", "");
        int taskId = manager.addTask(task);
        Task task1 = new Task("Task1", "");
        int task1Id = manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/tasks/" + taskId));
        HttpRequest request1 = createGetRequest(URI.create("http://localhost:8080/tasks/" + task1Id));
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest historyRequest = createGetRequest(URI.create("http://localhost:8080/history"));
        HttpResponse<String> historyResponse = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());
        List<Task> historyFromResponse = gson.fromJson(historyResponse.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(manager.getHistory(), historyFromResponse);
    }

    // prioritized
    @Test
    public void testPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "", 60, LocalDateTime.now());
        Task task1 = new Task("Task1", "", 60, LocalDateTime.now().minusHours(2));
        manager.addTask(task);
        manager.addTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/prioritized"));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> responsePrioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(manager.getPrioritizedTasks(), responsePrioritizedTasks);
    }

    // not found & intersection
    @Test
    public void testNotFoundTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createGetRequest(URI.create("http://localhost:8080/tasks/123"));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testTaskHasIntersection() throws IOException, InterruptedException {
        Task task = new Task("Task", "", 60, LocalDateTime.now());
        Task task1 = new Task("Task1", "", 60, LocalDateTime.now());
        String task1Json = gson.toJson(task1);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createPostRequest(URI.create("http://localhost:8080/tasks"), task1Json);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    private HttpRequest createGetRequest(URI url) {
        return HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
    }

    private HttpRequest createPostRequest(URI url, String body) {
        return HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpRequest createDeleteRequest(URI url) {
        return HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
    }

}
