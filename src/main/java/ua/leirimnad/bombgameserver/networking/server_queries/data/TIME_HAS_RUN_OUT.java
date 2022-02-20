package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.players.Player;

public class TIME_HAS_RUN_OUT implements ServerQueryData {
    public final String new_syllable;
    public final Player next_player;
    public final float new_syllable_complexity;
    public final String possible_word;

    public TIME_HAS_RUN_OUT(String new_syllable, Player next_player, float new_syllable_complexity, String possible_word) {
        this.new_syllable = new_syllable;
        this.next_player = next_player;
        this.new_syllable_complexity = new_syllable_complexity;
        this.possible_word = possible_word;
    }

    public TIME_HAS_RUN_OUT(String new_syllable, Player next_player, float new_syllable_complexity) {
        this.new_syllable = new_syllable;
        this.next_player = next_player;
        this.new_syllable_complexity = new_syllable_complexity;
        this.possible_word = null;
    }
}
