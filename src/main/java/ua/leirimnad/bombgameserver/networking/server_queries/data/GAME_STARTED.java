package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.players.Player;

import java.util.List;

public class GAME_STARTED implements ServerQueryData {

    public final String syllable;
    public final List<Character> required_letters;
    public final Player current_player;

    public GAME_STARTED(String syllable, List<Character> required_letters, Player current_player) {
        this.syllable = syllable;
        this.required_letters = required_letters;
        this.current_player = current_player;
    }
}
