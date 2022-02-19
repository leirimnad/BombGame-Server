package ua.leirimnad.bombgameserver.networking;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ua.leirimnad.bombgameserver.BombGameServer;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerActionQuery;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerInstantQueryResponse;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQuery;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.networking.server_queries.data.JOIN_TABLE_SUCCESS;
import ua.leirimnad.bombgameserver.networking.server_queries.data.QUERY_FAILURE;
import ua.leirimnad.bombgameserver.words.WordManager;

import java.io.IOException;

public class WebSocketServer extends TextWebSocketHandler {
    private final BombGameServer server;
    private final MessageDistributor messageDistributor;

    public WebSocketServer() {
        server = new BombGameServer();
        messageDistributor = new MessageDistributor(server);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        JSONObject jo;
        try {
            Object obj = new JSONParser().parse(message.getPayload());
            jo = (JSONObject) obj;
        } catch (ParseException e){
            sendActionQuery(session, new QUERY_FAILURE("JSON message is corrupted"));
            return;
        }

        String action = (String) jo.get("action");

        if (action == null) {
            sendActionQuery(session, new QUERY_FAILURE("No action specified"));
            return;
        }

        try {
            messageDistributor.process(jo, session);

        } catch (ProcessingException e) {
            sendActionQuery(session, new QUERY_FAILURE(e.getMessage()));
        }
    }

    public static String getBadRequestString(){
         return "{\"error\": \"Bad request\"}";
    }

    public static void sendInstantQueryResponse(WebSocketSession session, String instantQueryId, ServerQueryData data){
        ServerQuery response = new ServerInstantQueryResponse(instantQueryId, data);
        try {
            session.sendMessage(new TextMessage(response.asJSON()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendActionQuery(WebSocketSession session, ServerQueryData data){
        ServerQuery response = new ServerActionQuery(data);
        try {
            session.sendMessage(new TextMessage(response.asJSON()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
