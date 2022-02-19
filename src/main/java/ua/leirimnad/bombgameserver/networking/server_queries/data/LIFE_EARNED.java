package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;

import java.util.List;

public class LIFE_EARNED implements ServerQueryData {
    public final String player_id;
    public final List<Character> required_letters;

    public LIFE_EARNED(String player_id, List<Character> required_letters) {
        this.player_id = player_id;
        this.required_letters = required_letters;
    }

}
