package ua.leirimnad.bombgameserver.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQuery;
import ua.leirimnad.bombgameserver.networking.server_queries.TABLE_LIST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableManager {
    private final List<Table> tables;

    public TableManager() {
        this.tables = new ArrayList<>();
    }

    public void processGetTableList(WebSocketSession session) throws IOException {
        ObjectWriter objectWriter = new ObjectMapper().writer();
        ServerQuery response = new ServerQuery(new TABLE_LIST(tables));
        session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
    }
}
