package ua.leirimnad.bombgameserver.players;

import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.WebSocketServer;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.networking.server_queries.data.*;
import ua.leirimnad.bombgameserver.tables.Table;

import java.util.Arrays;
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
        sendToAll(table, new PLAYER_JOINED(player), player);
    }

    public void processLeavePlayer(Table table, Player player, Player nextPlayer) {
        tableMap.remove(player.getSession());
        sendToAll(table, new PLAYER_LEFT(player, nextPlayer), player);
    }

    public void processDeleteTable(Table table){
        table.getPlayers().stream().map(Player::getSession).forEach(tableMap::remove);
        sendToAll(table,new TABLE_DELETED(table));
    }

    public void processStartGame(Table table) {
        for (Player player : table.getPlayers()) {
            player.applyNeededCharacters(table.getRequiredLetters(0));
        }

        sendToAll(table,
                new GAME_STARTED(table.getCurrentSyllable(), table.getRequiredLetters(0), table.getCurrentPlayer())
        );

    }

    public void processUpdateWord(Table table, Player origin_player) {
        sendToAll(table, new WORD_UPDATED(table.getCurrentWord()), origin_player);
    }

    public void processWordRejected(Table table) {
        sendToAll(table, new WORD_REJECTED());
    }

    public void processWordAccepted(Table table, String newSyllable, float complexity, Player nextPlayer) {
        sendToAll(table, new WORD_ACCEPTED(newSyllable, nextPlayer, complexity));
    }

    public void processTimeHasRunOut(Table table, String newSyllable, float complexity, Player nextPlayer, String possibleWord) {
        sendToAll(table, new TIME_HAS_RUN_OUT(newSyllable, nextPlayer, complexity, possibleWord));
    }

    public void processLifeEarned(Table table, Player currentPlayer) {
        WebSocketServer.sendActionQuery(currentPlayer.getSession(),
                new LIFE_EARNED(currentPlayer.getId(), currentPlayer.getNeededCharacters())
        );
        sendToAll(table, new LIFE_EARNED(currentPlayer.getId(), null), currentPlayer);
    }

    private void sendToAll(Table table, ServerQueryData data, Player... exceptPlayers){
        for(Player p : table.getPlayers()){
            if(!Arrays.asList(exceptPlayers).contains(p))
                WebSocketServer.sendActionQuery(p.getSession(), data);
        }
    }



}

