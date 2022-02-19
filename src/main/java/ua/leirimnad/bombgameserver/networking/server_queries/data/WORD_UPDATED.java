package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;

public class WORD_UPDATED implements ServerQueryData {
    public String updated_word;

    public WORD_UPDATED(String updated_word) {
        this.updated_word = updated_word;
    }
}
