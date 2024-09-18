package http.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.TaskIntersectionException;
import http.RequestMethod;
import model.Epic;
import model.Status;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = extractPath(exchange);
        RequestMethod method = extractRequestMethod(exchange);
        switch (path.length) {
            case 2 -> handleEpics(exchange, method);
            case 3 -> handleEpicById(exchange, method, path);
            case 4 -> handleEpicSubtasksById(exchange, path);
            default -> sendMethodNotAllowed(exchange);
        }
    }

    private void handleEpics(HttpExchange exchange, RequestMethod method) throws IOException {
        if (method == RequestMethod.GET) {
            sendText(exchange, gson.toJson(taskManager.getEpics()));
        } else if (method == RequestMethod.POST) {
            try {
                Epic epic = extractEpic(exchange);
                if (epic.getId() > 0) {
                    taskManager.updateEpic(epic);
                    sendText(exchange, "Epic was successfully updated");
                } else {
                    taskManager.addEpic(epic);
                    sendTaskCreated(exchange, "Epic was successfully added");
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                sendIncorrectUserData(exchange, "Incorrect data");
            } catch (TaskIntersectionException e) {
                e.printStackTrace();
                sendHasInteractions(exchange);
            }

        }
    }

    private void handleEpicById(HttpExchange exchange, RequestMethod method, String[] path) throws IOException {
        try {
            int epicId = Integer.parseInt(path[2]);
            if (method == RequestMethod.GET) {
                sendText(exchange, gson.toJson(taskManager.getEpicById(epicId)));
            } else if (method == RequestMethod.DELETE) {
                taskManager.removeEpicById(epicId);
                sendText(exchange, "Epic was successfully deleted");
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendIncorrectUserData(exchange, "Incorrect id");
        }
    }

    private void handleEpicSubtasksById(HttpExchange exchange, String[] path) throws IOException {
        try {
            int epicId = Integer.parseInt(path[2]);
            sendText(exchange, gson.toJson(taskManager.getEpicSubtasksByEpicId(epicId)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendIncorrectUserData(exchange, "Incorrect id");
        }
    }

    private Epic extractEpic(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Epic.class);
        if (epic.getStatus() == null) {
            epic.setStatus(Status.NEW);
        }
        return new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(), epic.getDuration(), epic.getStartTime());
    }
}
