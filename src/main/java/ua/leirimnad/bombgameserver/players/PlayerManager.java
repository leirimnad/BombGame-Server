package ua.leirimnad.bombgameserver.players;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerActionQuery;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerInstantQueryResponse;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQuery;
import ua.leirimnad.bombgameserver.networking.server_queries.data.CREATE_TABLE_SUCCESS;
import ua.leirimnad.bombgameserver.networking.server_queries.data.PLAYER_JOINED;
import ua.leirimnad.bombgameserver.networking.server_queries.data.PLAYER_LEFT;
import ua.leirimnad.bombgameserver.tables.Table;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private final Map<WebSocketSession, Table> tableMap;

    public PlayerManager(){
        tableMap = new HashMap<>();
    }

    public boolean hasSession(WebSocketSession session){
        return tableMap.containsKey(session);
    }

    public Table getTableBySession(WebSocketSession session){
        return tableMap.get(session);
    }

    public void processJoinPlayer(Table table, Player player) throws IOException {
        tableMap.put(player.getSession(), table);
        sendJoinNotification(table, player);
    }

    public void processLeavePlayer(Table table, Player player) throws IOException {
        tableMap.remove(player.getSession());
        sendLeaveNotification(table, player);
    }

    private void sendJoinNotification(Table table, Player player) throws IOException {
        ObjectWriter objectWriter = new ObjectMapper().writer();
        for(Player p : table.getPlayers()){
            if(!p.equals(player)){
                WebSocketSession session = p.getSession();
                ServerQuery response = new ServerActionQuery(new PLAYER_JOINED(player));
                session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
            }
        }
    }

    private void sendLeaveNotification(Table table, Player player) throws IOException{
        ObjectWriter objectWriter = new ObjectMapper().writer();
        for(Player p : table.getPlayers()){
            if(!p.equals(player)){
                WebSocketSession session = p.getSession();
                ServerQuery response = new ServerActionQuery(new PLAYER_LEFT(player));
                session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
            }
        }
    }

}

