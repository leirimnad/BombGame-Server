package ua.leirimnad.bombgameserver.networking;

import org.json.simple.JSONObject;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.BombGameServer;
import ua.leirimnad.bombgameserver.networking.server_queries.data.PONG;
import ua.leirimnad.bombgameserver.networking.server_queries.data.QUERY_FAILURE;

import java.util.List;
import java.util.Optional;

// TODO что такое instantQueryId
public class MessageDistributor {
    private final BombGameServer server;
    private final List<String> instantQueryActions = List.of(
            "GET_TABLE_LIST", "CREATE_TABLE", "JOIN_TABLE", "LEAVE_TABLE", "PING"
            );

    public MessageDistributor(BombGameServer server) {
        this.server = server;
    }

    public void process(JSONObject request, WebSocketSession session) throws ProcessingException {

        String action = getAction(request);
        String instantQueryId = getInstantQueryId(request);

        if (instantQueryActions.contains(action) && instantQueryId == null){
            WebSocketServer.sendActionQuery(session, new QUERY_FAILURE(action+" query requires an instant query id"));
            return;
        }

        switch (action){
            case "GET_TABLE_LIST" -> this.server.getTableManager().processGetTableList(session, instantQueryId);

            case "CREATE_TABLE" -> {
                String tableName = (String) Optional.ofNullable(request.get("table_name"))
                        .orElseThrow(() -> new ProcessingException("table_name not found"));

                String playerName = (String) Optional.ofNullable(request.get("player_name"))
                        .orElseThrow(() -> new ProcessingException("player_name not found"));

                this.server.getTableManager().processCreateTable(session, instantQueryId, tableName, playerName);
            }

            case "JOIN_TABLE" -> {
                String tableId = (String) Optional.ofNullable(request.get("table_id"))
                        .orElseThrow(() -> new ProcessingException("table_id not found"));
                String playerName = (String) Optional.ofNullable(request.get("player_name"))
                        .orElseThrow(() -> new ProcessingException("player_name not found"));

                this.server.getTableManager().processJoinTable(session, instantQueryId, tableId, playerName);
            }

            case "LEAVE_TABLE" -> this.server.getTableManager().processLeaveTable(session, instantQueryId);

            case "START_GAME" -> this.server.getTableManager().processStartGame(session);

            case "UPDATE_WORD"  -> {
                String updatedWord = (String) Optional.ofNullable(request.get("updated_word"))
                        .orElseThrow(() -> new ProcessingException("updated_word not found"));

                this.server.getTableManager().processUpdateWord(session, updatedWord);
            }

            case "CONFIRM_WORD" -> this.server.getTableManager().processConfirmWord(session);

            case "PING" -> WebSocketServer.sendActionQuery(session, new PONG());
        }

    }

    private static String getInstantQueryId(JSONObject request){
        return (String) request.get("request_id");
    }

    private static String getAction(JSONObject request) throws ProcessingException {
        return (String) Optional.ofNullable(request.get("action"))
                .orElseThrow(() -> new ProcessingException("action not found"));
    }



}
