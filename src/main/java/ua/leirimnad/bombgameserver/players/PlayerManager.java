package ua.leirimnad.bombgameserver.players;

import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.WebSocketServer;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.networking.server_queries.data.GAME_STARTED;
import ua.leirimnad.bombgameserver.networking.server_queries.data.PLAYER_JOINED;
import ua.leirimnad.bombgameserver.networking.server_queries.data.PLAYER_LEFT;
import ua.leirimnad.bombgameserver.tables.Table;
import ua.leirimnad.bombgameserver.words.WordManager;

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

    public void processJoinPlayer(Table table, Player player) {
        tableMap.put(player.getSession(), table);
        sendJoinNotification(table, player);
    }

    public void processLeavePlayer(Table table, Player player) {
        tableMap.remove(player.getSession());
        sendLeaveNotification(table, player);
    }

    public void processStartGame(Table table) {
        sendStartNotification(table);
    }

    private void sendJoinNotification(Table table, Player player) {
        ServerQueryData response = new PLAYER_JOINED(player);

        for(Player p : table.getPlayers()){
            if(!p.equals(player))
                WebSocketServer.sendActionQuery(p.getSession(), response);
        }
    }

    private void sendLeaveNotification(Table table, Player player) {
        ServerQueryData response = new PLAYER_LEFT(player);

        for(Player p : table.getPlayers()){
            if(!p.equals(player))
                WebSocketServer.sendActionQuery(p.getSession(), response);
        }
    }

    private void sendStartNotification(Table table) {
        String syllable = table.getCurrentSyllable();
        ServerQueryData response = new GAME_STARTED(syllable, WordManager.RUSSIAN_REQUIRED_LETTERS);

        for(Player p : table.getPlayers())
            WebSocketServer.sendActionQuery(p.getSession(), response);

    }

}

