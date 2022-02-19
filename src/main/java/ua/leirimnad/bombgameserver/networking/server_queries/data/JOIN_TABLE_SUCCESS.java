package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.tables.Table;

public class JOIN_TABLE_SUCCESS implements ServerQueryData {
    public final Table table;

    public JOIN_TABLE_SUCCESS(Table table){
        this.table = table;
    }

}
