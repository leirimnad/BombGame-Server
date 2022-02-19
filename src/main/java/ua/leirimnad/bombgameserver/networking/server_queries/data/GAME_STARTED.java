package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.players.Player;

import java.util.List;

public class GAME_STARTED implements ServerQueryData {

    private final String syllable;
    private final List<Character> required_letters;
    private final Player current_player;

    public GAME_STARTED(String syllable, List<Character> required_letters, Player current_player) {
        this.syllable = syllable;
        this.required_letters = required_letters;
        this.current_player = current_player;
    }

    public String getSyllable() {
        return syllable;
    }

    public List<Character> getRequiredLetters() {
        return required_letters;
    }

    public Player getCurrent_player() {
        return current_player;
    }

}
