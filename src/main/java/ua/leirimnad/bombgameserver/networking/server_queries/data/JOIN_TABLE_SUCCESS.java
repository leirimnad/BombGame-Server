package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.tables.Table;

public class JOIN_TABLE_SUCCESS implements ServerQueryData {

    private final Table table;

    public JOIN_TABLE_SUCCESS(Table table){
        this.table = table;
    }

    public Table getTable() {
        return table;
    }
}
