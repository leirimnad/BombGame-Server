package ua.leirimnad.bombgameserver.networking;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ua.leirimnad.bombgameserver.BombGameServer;
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
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("Connection established"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject jo;
        try {
            Object obj = new JSONParser().parse(message.getPayload());
            jo = (JSONObject) obj;
        } catch (ParseException e){
            informBadRequest(session);
            return;
        }

        String action = (String) jo.get("action");

        if (action == null) {
            informBadRequest(session);
            return;
        }

        JSONObject data = (JSONObject) jo.get("data");

        messageDistributor.process(action, data, session);
    }

    private void informBadRequest(WebSocketSession session){
        try {
            session.sendMessage(new TextMessage("{\"error\": \"Bad request\"}"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
