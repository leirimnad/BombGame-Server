package ua.leirimnad.bombgameserver;

import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.tables.TableManager;
import ua.leirimnad.bombgameserver.words.WordManager;

public class BombGameServer {
    private TableManager tableManager;
    private WordManager wordManager;

    // TODO make IOC
    public BombGameServer() {
        this.tableManager = new TableManager(new PlayerManager());
        this.wordManager = new WordManager();
    }

    public TableManager getTableManager() {
        return tableManager;
    }

    public WordManager getWordManager() {
        return wordManager;
    }
}
