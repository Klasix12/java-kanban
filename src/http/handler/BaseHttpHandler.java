package http.handler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import http.RequestMethod;
import serialization.LocalDateTimeAdapter;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected Gson gson;
    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendTaskCreated(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
        exchange.sendResponseHeaders(201, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendIncorrectUserData(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
        exchange.sendResponseHeaders(400, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        byte[] resp = "Method Not Allowed".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
        exchange.sendResponseHeaders(405, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        byte[] resp = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected RequestMethod extractRequestMethod(HttpExchange exchange) {
        return RequestMethod.valueOf(exchange.getRequestMethod());
    }

    protected String[] extractPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }
}
