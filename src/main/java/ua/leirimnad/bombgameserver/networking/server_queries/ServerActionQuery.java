package ua.leirimnad.bombgameserver.networking.server_queries;

// Usual response
public class ServerActionQuery extends ServerQuery {
    public String action;

    public ServerActionQuery(ServerQueryData data) {
        super(data);
        this.action = data.getClass().getSimpleName();
    }
}
