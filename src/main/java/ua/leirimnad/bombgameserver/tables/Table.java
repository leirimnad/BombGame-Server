package ua.leirimnad.bombgameserver.tables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ua.leirimnad.bombgameserver.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final String id;
    private Player host;
    private final List<Player> players;
    private boolean gameInProgress;
    private Player currentPlayer;
    private String currentWord;
    private String currentSyllable;
    private final String name;

    public Table(String id, String name, Player host) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.players = new ArrayList<>();

        addPlayer(host);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player player){
        if(players.contains(player)) host = player;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public String getCurrentSyllable() {
        return currentSyllable;
    }

    public void removePlayer(Player player){
        players.remove(player);
        if(player.equals(host) && !players.isEmpty()){
            Player newHost = players.stream()
                                    .filter(p -> gameInProgress && !p.isSpectating())
                                    .findFirst()
                                    .orElse(players.get(0));
            setHost(newHost);
        }
    }
}
