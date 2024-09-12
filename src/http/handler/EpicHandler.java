package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class EpicHandler  extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    /*
    /epics | GET | getEpics()
    /epics/{id} | GET | getEpicById(id)
    /epics/{id}/subtasks | GET | getEpicSubtasksByEpicId(id)
    /epics | POST | createEpic(epic)
    /epics/{id} | DELETE | deleteEpic(id)
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
