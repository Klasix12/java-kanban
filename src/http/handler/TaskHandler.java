package http.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.TaskIntersectionException;
import http.RequestMethod;
import model.Status;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = extractPath(exchange);
        RequestMethod method = extractRequestMethod(exchange);
        switch (path.length) {
            case 2 -> handleTasks(exchange, method);
            case 3 -> handleTaskById(exchange, method, path);
            default -> sendMethodNotAllowed(exchange);
        }
    }

    private void handleTasks(HttpExchange exchange, RequestMethod method) throws IOException {
        try {
            if (method == RequestMethod.GET) {
                sendText(exchange, gson.toJson(taskManager.getTasks()));
            } else if (method == RequestMethod.POST) {
                Task task = extractTask(exchange);
                if (task.getId() > 0) {
                    taskManager.updateTask(task);
                    sendText(exchange, "Task was successfully updated");
                } else {
                    taskManager.addTask(task);
                    sendTaskCreated(exchange, "Task was successfully added");
                }
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            sendIncorrectUserData(exchange, "Incorrect data");
        } catch (TaskIntersectionException e) {
            e.printStackTrace();
            sendHasInteractions(exchange);
        }
    }

    private void handleTaskById(HttpExchange exchange, RequestMethod method, String[] path) throws IOException {
        try {
            int id = Integer.parseInt(path[2]);
            if (method == RequestMethod.GET) {
                sendText(exchange, gson.toJson(taskManager.getTaskById(id)));
            } else if (method == RequestMethod.DELETE) {
                taskManager.removeTaskById(id);
                sendText(exchange, "Task was successfully deleted");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendIncorrectUserData(exchange, "Incorrect id");
        } catch (NotFoundException e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }
    private Task extractTask(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Task.class);
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        return task;
    }
}