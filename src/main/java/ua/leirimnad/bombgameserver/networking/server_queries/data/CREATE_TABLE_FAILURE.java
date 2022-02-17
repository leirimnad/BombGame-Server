package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;

public class CREATE_TABLE_FAILURE implements ServerQueryData {
    private final String description;

    public CREATE_TABLE_FAILURE(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
