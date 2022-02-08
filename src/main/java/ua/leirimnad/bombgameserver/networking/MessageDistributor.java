package ua.leirimnad.bombgameserver.networking;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class MessageDistributor {

    public void process(String action, JSONObject data, WebSocketSession session){

        // some code later.

        try {
            session.sendMessage(new TextMessage("You sent a "+action+" message."));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
