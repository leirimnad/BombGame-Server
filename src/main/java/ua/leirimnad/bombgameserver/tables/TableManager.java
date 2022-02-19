package ua.leirimnad.bombgameserver.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.WebSocketServer;
import ua.leirimnad.bombgameserver.networking.server_queries.data.*;
import ua.leirimnad.bombgameserver.players.Player;
import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.words.WordManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableManager {

    private final List<Table> tables;
    private final PlayerManager playerManager;
    private final int ID_LENGTH = 4;

    public TableManager(PlayerManager playerManager) {
        this.tables = new ArrayList<>();
        this.playerManager = playerManager;
    }

    public void processGetTableList(WebSocketSession session, String instantQueryId) {
        WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new TABLE_LIST(tables));
    }

    public void processCreateTable(WebSocketSession session, String instantQueryId,
                                   String tableName, String playerName) {
        if(playerManager.hasSession(session)){
            WebSocketServer.sendInstantQueryResponse(
                    session,
                    instantQueryId,
                    new QUERY_FAILURE("The player " + session.getId() +
                    " cannot create a table because he is already playing at another table")
            );
        }
        else {


            Player host = new Player(session, playerName);

            String tableId = generateRandomId(ID_LENGTH);
            Table table = new Table(tableId, tableName, host);

            tables.add(table);
            playerManager.processJoinPlayer(table, host);

            WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new CREATE_TABLE_SUCCESS(table));
        }
    }


    public void processJoinTable(WebSocketSession session, String instantQueryId,
                                 String tableId, String playerName) {
        if(!idExists(tableId) || playerManager.hasSession(session)){
            WebSocketServer.sendInstantQueryResponse(
                    session,
                    instantQueryId,
                    new QUERY_FAILURE("The player " + session.getId() + " cannot join the table " + tableId)
            );
        }
        else{
            Player player = new Player(session, playerName);

            Table table = tables.stream()
                                .filter(t -> t.getId().equals(tableId))
                                .findFirst()
                                .orElse(null);

            assert table != null;
            table.addPlayer(player);

            playerManager.processJoinPlayer(table, player);


            WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new JOIN_TABLE_SUCCESS(table));
        }
    }

    // должен ли меняться слог, после того как вышел игрок?
    public void processLeaveTable(WebSocketSession session, String instantQueryId) {
        Table table = playerManager.getTableBySession(session);
        if(table == null) {
            WebSocketServer.sendActionQuery(session, new QUERY_FAILURE("No table found for this session"));
            return;
        }

        Player player = table.getPlayers().stream()
                                        .filter(p -> p.getSession().equals(session))
                                        .findFirst()
                                        .orElse(null);
        assert player != null;

        table.removePlayer(player);
        playerManager.processLeavePlayer(table, player);

        if(table.getPlayers().isEmpty())
            tables.remove(table);

        WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new LEAVE_TABLE_SUCCESS());

    }

    public void processStartGame(WebSocketSession session, WordManager wordManager) {
        Table table = playerManager.getTableBySession(session);
        if (table == null) {
            WebSocketServer.sendActionQuery(session, new QUERY_FAILURE("No table found for this session"));
            return;
        }

        if(table.getHost().getSession().equals(session)){
            String syllable = wordManager.getSyllable(0.25f);
            table.start(syllable);

            playerManager.processStartGame(table);
        }
    }

    private String generateRandomId(int length){
        String id;
        do{
            id = RandomStringUtils.random(length, true, false).toUpperCase(Locale.ROOT);
        }
        while(idExists(id));

        return id;
    }

    private boolean idExists(String id){
        return tables
                .stream()
                .anyMatch(t -> t.getId().equals(id));
    }

}
