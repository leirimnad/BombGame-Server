package ua.leirimnad.bombgameserver.networking.server_queries;

/**
 * A response for an instant query. Has a special id for answering the query.
 */

public class ServerInstantQueryResponse extends ServerActionQuery {

    public String request_id;

    public ServerInstantQueryResponse(String instantQueryId, ServerQueryData data) {
        super(data);
        this.request_id = instantQueryId;
    }

}
