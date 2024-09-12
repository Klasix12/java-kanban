package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler  extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    /*
    /prioritized | GET | getPrioritizedTasks()
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
