package http;

import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private final int port = 8080;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }
}
