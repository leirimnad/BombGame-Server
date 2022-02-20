package ua.leirimnad.bombgameserver.tables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.Validate;
import ua.leirimnad.bombgameserver.Settings;
import ua.leirimnad.bombgameserver.players.Player;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

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

    @JsonIgnore
    private int playersIterated = 0;

    @JsonIgnore
    private int turnNumber = 0;

    @JsonIgnore
    private int currentSyllableDuration = 0;

    @JsonIgnore
    private final List<List<Character>> requiredLettersSets = new ArrayList<>(){{add(generateRequiredLetters());}};
    private Timer timer = null;


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

    public synchronized String getCurrentSyllable() {
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

    public boolean start(String syllable, Timer timer){
        if(gameInProgress) return false;

        this.timer = timer;
        gameInProgress = true;
        currentPlayer = players.get(iter);
        currentSyllable = syllable;

        for(Player p : players) {
            p.reset();
            p.setSpectating(false);
        }

        return true;
    }

    public void endGame(){
        if (!gameInProgress) return;

        gameInProgress = false;
        currentPlayer = null;
        currentSyllable = null;
        currentWord = null;
        iter = 0;
        playersIterated = 0;
        turnNumber = 0;
        currentSyllableDuration = 0;
        requiredLettersSets.clear();
        requiredLettersSets.add(generateRequiredLetters());
        timer = null;

        for(Player p : players) p.reset();
    }

    private void passTurn(){
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
        float playersPlaying = this.players.stream().filter((Player p)->!p.isSpectating()).count();
        if (!this.isGameInProgress())
            playersPlaying = players.size();

        float rounds = (float) playersIterated / playersPlaying;
        float sigmoidMultiplier = (float) (-Math.log(0.0001)/Settings.ROUNDS_TO_HARDEST_SYLLABLE);
        return sigmoid(rounds, sigmoidMultiplier);
    }

    private static float sigmoid(double x, float xMultiplier){
        return (float) (1 / (1 + Math.exp(-x * xMultiplier + Math.log(99))));
    }

    private static List<Character> generateRequiredLetters(){
        List<Character> letters  = "абвгдежзийклмнопрстуфхцчшщыьюя".chars().mapToObj(e -> (char)e).collect(Collectors.toList());
        Collections.shuffle(letters);
        return letters.subList(0, Settings.REQUIRED_LETTERS_COUNT);
    }

    public List<Character> getRequiredLetters(int gen){
        while(requiredLettersSets.size() <= gen){
            requiredLettersSets.add(generateRequiredLetters());
        }
        return requiredLettersSets.get(gen);
    }

    public void turn(String newSyllable, Timer timer){
        this.turnNumber++;

        if (this.timer != null)
            this.timer.cancel();

        if (currentSyllable.equals(newSyllable))
            currentSyllableDuration++;
        else
            this.currentSyllable = newSyllable;

        this.setCurrentWord("");
        this.passTurn();
        this.timer = timer;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public synchronized int getCurrentSyllableDuration() {
        return currentSyllableDuration;
    }

    public int countAlivePlayers() {
        return (int) this.players.stream().filter(p->p.getLives()>0).count();
    }
}
