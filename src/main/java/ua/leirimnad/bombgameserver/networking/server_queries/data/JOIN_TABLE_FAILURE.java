package ua.leirimnad.bombgameserver.networking.server_queries.data;

public class JOIN_TABLE_FAILURE implements ServerQueryData {

    private final String description;

    public JOIN_TABLE_FAILURE(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
