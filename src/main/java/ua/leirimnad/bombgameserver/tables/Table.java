package ua.leirimnad.bombgameserver.tables;

import ua.leirimnad.bombgameserver.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final String id;
    private final String name;
    private Player host;
    private final List<Player> players;

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

    // может ли игрок зайти дваэды в одну и ту же игру
    public void addPlayer(Player player){
        players.add(player);
    }

    public Player getHost() {
        return host;
    }
}
