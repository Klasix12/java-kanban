package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class SubtaskHandler  extends BaseHttpHandler implements HttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    /*
    /subtasks | GET | getSubtasks()
    /subtasks/{id} | GET | getSubtaskById(id)
    /subtasks | POST | createSubtask(subtask) - без id, updateSubtask(subtask) - c id
    /subtasks/{id} | DELETE | deleteSubtasks(id)
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
