package ua.leirimnad.bombgameserver.players;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.Validate;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.Settings;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private final String id;
    private final String name;
    private int lives;
    private boolean isSpectating;

    @JsonIgnore
    private List<Character> neededCharacters;

    @JsonIgnore
    private int characterSetGeneration;

    @JsonIgnore
    private final WebSocketSession session;

    public Player(WebSocketSession session, String name){
        Validate.notNull(name, "player's session can't be null");
        this.id = session.getId();

        Validate.notNull(name, "player's name can't be null");
        this.name = name;

        this.reset();

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

    public List<Character> getNeededCharacters() {
        return neededCharacters;
    }

    public void applyNeededCharacters(List<Character> neededCharacters) {
        this.neededCharacters = neededCharacters;
        this.characterSetGeneration++;
    }

    public int getCharacterSetGeneration() {
        return characterSetGeneration;
    }

    public void fillCharacters(String str) {
        List<Character> characters = str.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
        for (Character c: characters) {
            neededCharacters.removeAll(Collections.singleton(Character.toLowerCase(c)));
            neededCharacters.removeAll(Collections.singleton(Character.toUpperCase(c)));
        }
    }

    public void incrementLives() {
        this.lives++;
    }

    public void decrementLives() {
        this.lives--;
    }

    public void reset(){
        this.lives = Settings.DEFAULT_PLAYER_LIVES;
        this.isSpectating = true;
        this.neededCharacters = null;
        this.characterSetGeneration = 0;
    }
}
