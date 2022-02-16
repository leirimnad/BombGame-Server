package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.players.Player;

public class PLAYER_LEFT implements ServerQueryData {

    private final Player player;

    public PLAYER_LEFT(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
