package ua.leirimnad.bombgameserver.players;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.socket.WebSocketSession;

public class Player {
    private final String id;
    private final String name;
    private int lives;
    private boolean isSpectating;

    @JsonIgnore
    private final WebSocketSession session;

    public Player(WebSocketSession session, String name){
        this.id = session.getId();
        this.name = name;
        this.lives = 3;
        this.isSpectating = true;

        this.session = session;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLives() {
        return lives;
    }

    public boolean isSpectating() {
        return isSpectating;
    }

    public void setSpectating(boolean spectating) {
        isSpectating = spectating;
    }

    public WebSocketSession getSession() {
        return session;
    }


}
