package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.players.Player;

public class PLAYER_LEFT implements ServerQueryData {

    private final Player player;
    private final Player next_player;

    public PLAYER_LEFT(Player player, Player next_player) {
        this.player = player;
        this.next_player = next_player;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getNextPlayer() {
        return next_player;
    }


}
