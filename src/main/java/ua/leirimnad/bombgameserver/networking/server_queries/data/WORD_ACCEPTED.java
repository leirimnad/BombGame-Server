package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.players.Player;

public class WORD_ACCEPTED implements ServerQueryData {
    public final String new_syllable;
    public final Player next_player;
    public final float new_syllable_complexity;

    public WORD_ACCEPTED(String new_syllable, Player next_player, float new_syllable_complexity) {
        this.new_syllable = new_syllable;
        this.next_player = next_player;
        this.new_syllable_complexity = new_syllable_complexity;
    }

}
