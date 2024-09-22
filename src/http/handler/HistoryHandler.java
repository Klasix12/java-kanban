package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.RequestMethod;
import service.TaskManager;

import java.io.IOException;

public class HistoryHandler  extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RequestMethod method = RequestMethod.valueOf(exchange.getRequestMethod());
        if (method == RequestMethod.GET) {
            sendText(exchange, gson.toJson(taskManager.getHistory()));
        }
    }
}
