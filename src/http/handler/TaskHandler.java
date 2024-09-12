package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    /*
    /tasks | GET | getTasks()
    /tasks/{id} | GET | getTaskById(id)
    /tasks | POST | createTask(task) - без id, updateTask(task) - с id
    /tasks/{id} | DELETE | deleteTask(id)
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
    }
}
