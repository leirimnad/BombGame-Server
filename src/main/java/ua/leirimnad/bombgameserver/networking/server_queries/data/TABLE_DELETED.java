package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.tables.Table;

public class TABLE_DELETED implements ServerQueryData {
    public final Table table;

    public TABLE_DELETED(Table table){
        this.table = table;
    }
}
