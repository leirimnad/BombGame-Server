package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.players.Player;

public class WORD_ACCEPTED implements ServerQueryData {
    private final String new_syllable;
    private final Player next_player;
    private final float new_syllable_complexity;

    public WORD_ACCEPTED(String new_syllable, Player next_player, float new_syllable_complexity) {
        this.new_syllable = new_syllable;
        this.next_player = next_player;
        this.new_syllable_complexity = new_syllable_complexity;
    }

    public String getNewSyllable() {
        return new_syllable;
    }

    public Player getNextPlayer() {
        return next_player;
    }

    public float getNew_syllable_complexity() {
        return new_syllable_complexity;
    }

}
