package ua.leirimnad.bombgameserver.players;

import org.springframework.web.socket.WebSocketSession;

public class Player {
    private String id;
    private String name;
    private int lives;
    private boolean isHost;
    private boolean isSpectating;

    public Player(String id, String name){
        this.id = id;
        this.name = name;
        this.lives = 3;
        this.isHost = false;
        this.isSpectating = true;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public boolean isSpectating() {
        return isSpectating;
    }

    public void setSpectating(boolean spectating) {
        isSpectating = spectating;
    }
}
