package ua.leirimnad.bombgameserver.networking.server_queries;

public class ServerInstantQueryResponse extends ServerQuery {
    public String request_id;

    public ServerInstantQueryResponse(String instantQueryId, ServerQueryData data) {
        super(data);
        this.request_id = instantQueryId;
    }

}
