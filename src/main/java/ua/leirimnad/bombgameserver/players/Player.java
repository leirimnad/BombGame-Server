package ua.leirimnad.bombgameserver.players;

public class Player {
    private String id;
    private String name;
    private int lives;
    private boolean isSpectating;

    public Player(String id, String name){
        this.id = id;
        this.name = name;
        this.lives = 3;
        this.isSpectating = true;
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
}
