package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.tables.Table;

import java.util.List;

public class TABLE_LIST implements ServerQueryData {
    public List<Table> tables;

    public TABLE_LIST(List<Table> tables) {
        this.tables = tables;
    }
}
