package ua.leirimnad.bombgameserver;

import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.tables.TableManager;
import ua.leirimnad.bombgameserver.words.WordManager;

public class BombGameServer {
    public TableManager tableManager;
    public WordManager wordManager;

    // TODO make IOC
    public BombGameServer() {
        this.tableManager = new TableManager();
        this.wordManager = new WordManager();
    }
}
