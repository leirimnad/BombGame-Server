package ua.leirimnad.bombgameserver;

import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.tables.TableManager;

public class BombGameServer {
    public PlayerManager playerManager;
    public TableManager tableManager;

    public BombGameServer() {
        this.playerManager = new PlayerManager();
        this.tableManager = new TableManager();
    }
}
