package ua.leirimnad.bombgameserver.networking.server_queries;

import ua.leirimnad.bombgameserver.networking.server_queries.data.ServerQueryData;

// Usual response
public class ServerActionQuery extends ServerQuery {
    public String action;

    public ServerActionQuery(ServerQueryData data) {
        super(data);
        this.action = data.getClass().getSimpleName();
    }
}
