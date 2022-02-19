package ua.leirimnad.bombgameserver.tables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.Validate;
import ua.leirimnad.bombgameserver.Settings;
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

    @JsonIgnore
    private int iter;
    private int playersIterated = 0;

    public Table(String id, String name, Player host) {
        Validate.notNull(id, "table's id can't be null");
        this.id = id;

        Validate.notNull(name, "table's name can't be null");
        this.name = name;

        Validate.notNull(host, "table's host can't be null");
        this.host = host;
        this.players = new ArrayList<>();
        this.iter = 0;

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

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public String getCurrentSyllable() {
        return currentSyllable;
    }

    public void setCurrentSyllable(String currentSyllable) {
        this.currentSyllable = currentSyllable;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player){
        players.remove(player);

        if(!players.isEmpty() && player.equals(host)){
            Player newHost = players.stream()
                                    .filter(p -> gameInProgress && !p.isSpectating())
                                    .findFirst()
                                    .orElse(players.get(0));
            setHost(newHost);
        }

        if(gameInProgress && !players.isEmpty() && currentPlayer.equals(player)){
            currentWord = null;
            passTurn();
        }
    }

    // первый игрок - хост?
    public boolean start(String syllable){
        if(gameInProgress) return false;

        gameInProgress = true;
        currentPlayer = players.get(iter);
        currentSyllable = syllable;

        for(Player p : players) p.setSpectating(false);

        return true;
    }

    public void passTurn(){
        currentPlayer = getNextAlivePlayer();
    }

    private Player getNextAlivePlayer(){
        do{
            iter++;
            iter %= players.size();

            if(players.get(iter).isSpectating()){
                continue;
            }
            else if (players.get(iter).getLives() <= 0) {
                playersIterated++;
            } else break;

        } while(true);
        playersIterated++;
        return players.get(iter);
    }

    public float calculateSyllableComplexity() {
        float rounds = (float) playersIterated / this.players.stream().filter((Player p)->!p.isSpectating()).count();
        float sigmoidMultiplier = (float) (-Math.log(0.0001)/Settings.ROUNDS_TO_HARDEST_SYLLABLE);
        return sigmoid(rounds, sigmoidMultiplier);
    }

    private static float sigmoid(double x, float xMultiplier){
        return (float) (1 / (1 + Math.exp(-x * xMultiplier + Math.log(99))));
    }
}
