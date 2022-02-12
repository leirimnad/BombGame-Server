package ua.leirimnad.bombgameserver.networking;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.BombGameServer;

import java.io.IOException;
import java.util.Objects;

// TODO что такое instantQueryId
public class MessageDistributor {
    private final BombGameServer server;

    public MessageDistributor(BombGameServer server) {
        this.server = server;
    }

    public void process(JSONObject request, WebSocketSession session) throws IOException {

        String action = getAction(request);
        String instantQueryId = getInstantQueryId(request);

        if (Objects.equals(action, "GET_TABLE_LIST")){
            if (instantQueryId == null) WebSocketServer.informBadRequest(session);
            else this.server.tableManager.processGetTableList(session, instantQueryId);
        }

        if (Objects.equals(action, "GET_SYLLABLE")){
            double min = (double) request.get("min");
            double max = (double) request.get("max");

            this.server.wordManager.processGetSyllable(session, (float)min, (float)max);
        }


    }

    private static String getInstantQueryId(JSONObject request){
        return (String) request.get("request_id");
    }

    private static String getAction(JSONObject request){
        return (String) request.get("action");
    }



}
