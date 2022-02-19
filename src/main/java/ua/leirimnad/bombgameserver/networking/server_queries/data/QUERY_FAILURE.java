package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;

public class QUERY_FAILURE implements ServerQueryData {
    public final String error;

    public QUERY_FAILURE(String error) {
        this.error = error;
    }
}
