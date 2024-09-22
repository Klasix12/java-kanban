package http.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.TaskIntersectionException;
import http.RequestMethod;
import model.Status;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = extractPath(exchange);
        RequestMethod method = extractRequestMethod(exchange);
        switch (path.length) {
            case 2 -> handleSubtasks(exchange, method);
            case 3 -> handleSubtasksById(exchange, method, path);
            default -> sendMethodNotAllowed(exchange);
        }
    }

    private void handleSubtasks(HttpExchange exchange, RequestMethod method) throws IOException {
        try {
            if (method == RequestMethod.GET) {
                sendText(exchange, gson.toJson(taskManager.getSubtasks()));
            } else if (method == RequestMethod.POST) {
                Subtask subtask = extractSubtask(exchange);
                if (subtask.getId() > 0) {
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, "Subtask was successfully updated");
                } else {
                    int taskId = taskManager.addSubtask(subtask);
                    if (taskId == -1) {
                        sendIncorrectUserData(exchange, "Incorrect task");
                    } else {
                        sendTaskCreated(exchange, "Subtask was successfully added");
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            sendIncorrectUserData(exchange, "Incorrect data");
        } catch (TaskIntersectionException e) {
            e.printStackTrace();
            sendHasInteractions(exchange);
        }
    }

    private void handleSubtasksById(HttpExchange exchange, RequestMethod method, String[] path) throws IOException {
        try {
            int subtaskId = Integer.parseInt(path[2]);
            if (method == RequestMethod.GET) {
                sendText(exchange, gson.toJson(taskManager.getSubtaskById(subtaskId)));
            } else if (method == RequestMethod.DELETE) {
                taskManager.removeSubtaskById(subtaskId);
                sendText(exchange, "Subtask was successfully deleted");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendIncorrectUserData(exchange, "Incorrect id");
        } catch (NotFoundException e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }

    private Subtask extractSubtask(HttpExchange exchange) throws IOException {
        Subtask subtask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Subtask.class);
        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        return subtask;
    }
}
