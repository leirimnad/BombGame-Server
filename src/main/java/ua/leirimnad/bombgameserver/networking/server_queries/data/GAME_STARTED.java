package ua.leirimnad.bombgameserver.networking.server_queries.data;

import ua.leirimnad.bombgameserver.networking.server_queries.ServerQueryData;

public class GAME_STARTED implements ServerQueryData {

    private final String syllable;
    private final char[] requiredLetters;

    public GAME_STARTED(String syllable, char[] requiredLetters) {
        this.syllable = syllable;
        this.requiredLetters = requiredLetters;
    }

    public String getSyllable() {
        return syllable;
    }

    public char[] getRequiredLetters() {
        return requiredLetters;
    }
}
