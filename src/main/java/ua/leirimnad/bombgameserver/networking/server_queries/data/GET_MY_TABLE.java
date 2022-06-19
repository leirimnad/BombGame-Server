package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;
import ua.leirimnad.bombgameserver.tables.Table;

public class GET_MY_TABLE implements ServerQueryData {
    public final String tableId;

    public GET_MY_TABLE(String tableId){
        this.tableId = tableId;
    }
}
