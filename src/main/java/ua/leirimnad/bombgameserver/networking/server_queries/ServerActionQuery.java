package ua.leirimnad.bombgameserver.networking.server_queries;

// TODO что делает ServerActionQuery
public class ServerActionQuery extends ServerQuery {
    public String action;

    public ServerActionQuery(ServerQueryData data) {
        super(data);
        this.action = data.getClass().getSimpleName();
    }
}
