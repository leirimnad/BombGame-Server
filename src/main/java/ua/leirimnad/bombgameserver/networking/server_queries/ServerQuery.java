package ua.leirimnad.bombgameserver.networking.server_queries;

public class ServerQuery {
    public String action;
    public ServerQueryData data;


    public ServerQuery(ServerQueryData data) {
        this.data = data;
        this.action = data.getClass().getSimpleName();
    }
}
