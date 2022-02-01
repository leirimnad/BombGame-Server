package ua.leirimnad.bombgameserver;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Locale;

public class PingPongWebSocket extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("Connection established"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if(message.getPayload().toLowerCase(Locale.ROOT).equals("ping"))
            session.sendMessage(new TextMessage("pong"));
        else
            session.sendMessage(new TextMessage("pong?"));

    }
}
