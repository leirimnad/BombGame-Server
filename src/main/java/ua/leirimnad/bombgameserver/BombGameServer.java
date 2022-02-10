package ua.leirimnad.bombgameserver;

import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.tables.TableManager;
import ua.leirimnad.bombgameserver.words.WordManager;

public class BombGameServer {
    public PlayerManager playerManager;
    public TableManager tableManager;
    public WordManager wordManager;

    public BombGameServer() {
        this.playerManager = new PlayerManager();
        this.tableManager = new TableManager();
        this.wordManager = new WordManager();
    }
}
