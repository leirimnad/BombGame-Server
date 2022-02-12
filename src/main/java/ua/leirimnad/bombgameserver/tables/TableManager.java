package ua.leirimnad.bombgameserver.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerInstantQueryResponse;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQuery;
import ua.leirimnad.bombgameserver.networking.server_queries.data.CREATE_TABLE_SUCCESS;
import ua.leirimnad.bombgameserver.networking.server_queries.data.TABLE_LIST;
import ua.leirimnad.bombgameserver.players.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableManager {
    private final List<Table> tables;
    private final Map<String, Table> tableMap;
    private final int ID_LENGTH = 4;

    public TableManager() {
        this.tables = new ArrayList<>();
        this.tableMap = new HashMap<>();
    }

    public void processGetTableList(WebSocketSession session, String instantQueryId) throws IOException {
        ObjectWriter objectWriter = new ObjectMapper().writer();
        ServerQuery response = new ServerInstantQueryResponse(instantQueryId, new TABLE_LIST(tables));
        session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
    }

    public void processCreateTable(WebSocketSession session, String instantQueryId,
                                   String playerName, String tableName) throws IOException {
        String sessionId = session.getId();
        Player host = new Player(sessionId, playerName);
        host.setHost(true);
        Table table = new Table(generateRandomId(ID_LENGTH), tableName);
        table.addPlayer(host);

        tables.add(table);
        tableMap.put(sessionId, table);

        ObjectWriter objectWriter = new ObjectMapper().writer();
        ServerQuery response = new ServerInstantQueryResponse(instantQueryId, new CREATE_TABLE_SUCCESS(table));
        session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
    }

    private String generateRandomId(int length){
        String id;
        do{
            id = RandomStringUtils.random(length, true, false);
        }
        while(!idExists(id));

        return id;
    }

    private boolean idExists(String id){
        return tables.stream().anyMatch(t -> t.getId().equals(id));
    }


}
