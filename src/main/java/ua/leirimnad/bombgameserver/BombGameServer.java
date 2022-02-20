package ua.leirimnad.bombgameserver;

import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.tables.TableManager;
import ua.leirimnad.bombgameserver.words.WordManager;

public class BombGameServer {
    private final TableManager tableManager;
    private final WordManager wordManager;

    // TODO make IOC
    public BombGameServer() {
        this.wordManager = new WordManager();
        this.tableManager = new TableManager(new PlayerManager(), wordManager);
    }

    public TableManager getTableManager() {
        return tableManager;
    }

    public WordManager getWordManager() {
        return wordManager;
    }
}
