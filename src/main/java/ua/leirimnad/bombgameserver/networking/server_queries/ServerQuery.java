package ua.leirimnad.bombgameserver.networking.server_queries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import ua.leirimnad.bombgameserver.networking.WebSocketServer;

public class ServerQuery {
    public ServerQueryData data;

    public ServerQuery(ServerQueryData data) {
        this.data = data;
    }

    public String asJSON(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        ObjectWriter objectWriter = objectMapper.writer();
        try {
            return objectWriter.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return WebSocketServer.getBadRequestString();
    }
}
