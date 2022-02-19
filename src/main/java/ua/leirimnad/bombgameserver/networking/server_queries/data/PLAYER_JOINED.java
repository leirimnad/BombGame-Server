package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.players.Player;

public class PLAYER_JOINED implements ServerQueryData {
    public final Player player;

    public PLAYER_JOINED(Player player){
        this.player = player;
    }
}
