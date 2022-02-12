package ua.leirimnad.bombgameserver.tables;

import ua.leirimnad.bombgameserver.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private final String id;
    private final String name;
    private List<Player> players;

    public Table(String id, String name) {
        this.id = id;
        this.name = name;
        this.players = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // может ли игрок зайти дваэды в одну и ту же игру
    public void addPlayer(Player player){
        players.add(player);
    }
}
