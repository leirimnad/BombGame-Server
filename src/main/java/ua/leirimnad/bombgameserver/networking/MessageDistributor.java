package ua.leirimnad.bombgameserver.networking;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.BombGameServer;

import java.io.IOException;
import java.util.Objects;

public class MessageDistributor {
    private final BombGameServer server;

    public MessageDistributor(BombGameServer server) {
        this.server = server;
    }

    public void process(String action, JSONObject data, WebSocketSession session) throws IOException {

        // some code later.

        try {
            session.sendMessage(new TextMessage("You sent a "+action+" message."));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Objects.equals(action, "GET_TABLE_LIST")){
            this.server.tableManager.processGetTableList(session);
        }


    }



}
