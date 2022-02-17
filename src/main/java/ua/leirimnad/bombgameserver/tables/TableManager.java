package ua.leirimnad.bombgameserver.tables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.WebSocketServer;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerInstantQueryResponse;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQuery;
import ua.leirimnad.bombgameserver.networking.server_queries.data.*;
import ua.leirimnad.bombgameserver.players.Player;
import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.words.WordManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TableManager {

    private final List<Table> tables;
    private final PlayerManager playerManager;
    private final int ID_LENGTH = 4;

    public TableManager(PlayerManager playerManager) {
        this.tables = new ArrayList<>();
        this.playerManager = playerManager;
    }

    public void processGetTableList(WebSocketSession session, String instantQueryId) throws IOException {
        ObjectWriter objectWriter = new ObjectMapper().writer();
        ServerQuery response = new ServerInstantQueryResponse(instantQueryId, new TABLE_LIST(tables));
        session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
    }

    public void processCreateTable(WebSocketSession session, String instantQueryId,
                                   String tableName, String playerName) throws IOException {
        if(playerManager.hasSession(session)){
            ObjectWriter objectWriter = new ObjectMapper().writer();
            ServerQuery response = new ServerInstantQueryResponse(
                    instantQueryId,
                    new CREATE_TABLE_FAILURE("The player " + session.getId() +
                            " cannot create a table because he is already playing at another table")
            );
            session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
        }
        else {
            Player host = new Player(session, playerName);

            String tableId = generateRandomId(ID_LENGTH);
            Table table = new Table(tableId, tableName, host);

            tables.add(table);
            playerManager.processJoinPlayer(table, host);

            ObjectWriter objectWriter = new ObjectMapper().writer();
            ServerQuery response = new ServerInstantQueryResponse(instantQueryId, new CREATE_TABLE_SUCCESS(table));
            session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
        }
    }


    public void processJoinTable(WebSocketSession session, String instantQueryId,
                                 String tableId, String playerName) throws IOException {
        if(!idExists(tableId) || playerManager.hasSession(session)){
            ObjectWriter objectWriter = new ObjectMapper().writer();
            ServerQuery response = new ServerInstantQueryResponse(
                    instantQueryId,
                    new JOIN_TABLE_FAILURE("The player " + session.getId() +
                            " cannot join the table " + tableId)
            );
            session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
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

            ObjectWriter objectWriter = new ObjectMapper().writer();
            ServerQuery response = new ServerInstantQueryResponse(instantQueryId, new JOIN_TABLE_SUCCESS(table));
            session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
        }
    }

    // должен ли меняться слог, после того как вышел игрок?
    public void processLeaveTable(WebSocketSession session, String instantQueryId) throws IOException{
        Table table = playerManager.getTableBySession(session);
        if(table == null){
            WebSocketServer.informBadRequest(session);
        }
        else {
            Player player = table.getPlayers().stream()
                                            .filter(p -> p.getSession().equals(session))
                                            .findFirst()
                                            .orElse(null);
            assert player != null;

            table.removePlayer(player);
            playerManager.processLeavePlayer(table, player);

            if(table.getPlayers().isEmpty())
                tables.remove(table);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            ObjectWriter objectWriter = objectMapper.writer();
            ServerQuery response = new ServerInstantQueryResponse(instantQueryId, new LEAVE_TABLE_SUCCESS());
            session.sendMessage(new TextMessage(objectWriter.writeValueAsString(response)));
        }
    }

    public void processStartGame(WebSocketSession session, WordManager wordManager) throws IOException{
        Table table = playerManager.getTableBySession(session);

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
