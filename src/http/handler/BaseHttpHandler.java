package http.handler;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import http.RequestMethod;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getDefaultGson();
    }

    protected void sendText(HttpExchange exchange, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    protected void sendTaskCreated(HttpExchange exchange, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    protected void sendIncorrectUserData(HttpExchange exchange, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
            exchange.sendResponseHeaders(400, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    protected void sendNotFound(HttpExchange exchange) {
        try (exchange) {
            byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
            exchange.sendResponseHeaders(404, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) {
        try (exchange) {
            byte[] resp = "Method Not Allowed".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
            exchange.sendResponseHeaders(405, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) {
        try (exchange) {
            byte[] resp = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
            exchange.sendResponseHeaders(406, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке ответа: " + e.getMessage());
        }
    }

    protected RequestMethod extractRequestMethod(HttpExchange exchange) {
        return RequestMethod.valueOf(exchange.getRequestMethod());
    }

    protected String[] extractPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }
}
