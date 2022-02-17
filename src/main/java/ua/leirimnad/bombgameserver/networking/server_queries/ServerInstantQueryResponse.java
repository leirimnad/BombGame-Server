package ua.leirimnad.bombgameserver.networking.server_queries;

// Like usual response but with special id
public class ServerInstantQueryResponse extends ServerActionQuery {
    public String request_id;

    public ServerInstantQueryResponse(String instantQueryId, ServerQueryData data) {
        super(data);
        this.request_id = instantQueryId;
    }

}
