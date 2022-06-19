package ua.leirimnad.bombgameserver.tables;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.Settings;
import ua.leirimnad.bombgameserver.networking.ProcessingException;
import ua.leirimnad.bombgameserver.networking.WebSocketServer;
import ua.leirimnad.bombgameserver.networking.server_queries.data.*;
import ua.leirimnad.bombgameserver.players.Player;
import ua.leirimnad.bombgameserver.players.PlayerManager;
import ua.leirimnad.bombgameserver.words.WordManager;

import java.security.SecureRandom;
import java.util.*;

public class TableManager {

    private final List<Table> tables;
    private final PlayerManager playerManager;
    private final WordManager wordManager;

    public TableManager(PlayerManager playerManager, WordManager wordManager) {
        this.tables = new ArrayList<>();
        this.playerManager = playerManager;
        this.wordManager = wordManager;
    }

    public void processGetTableList(WebSocketSession session, String instantQueryId) {
        WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new TABLE_LIST(tables));
    }

    public void processCreateTable(WebSocketSession session, String instantQueryId,
                                   String tableName, String playerName) throws ProcessingException {
        if(playerManager.hasSession(session))
            throw new ProcessingException("The player " + session.getId() +
                    " cannot create a table because he is already playing at another table");

        Player host = new Player(session, playerName);
        String tableId = generateRandomId();
        Table table = new Table(tableId, tableName, host);

        tables.add(table);
        playerManager.processJoinPlayer(table, host);

        WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new CREATE_TABLE_SUCCESS(table));

    }

    public void processDeleteTable(WebSocketSession session) throws ProcessingException {
        Table table = playerManager.getTableBySession(session);
        if (table == null) throw new ProcessingException("No table found for this session");

        if(isHost(session, table)){
            playerManager.processDeleteTable(table);
            deleteTable(table);
        }
    }

    public void processJoinTable(WebSocketSession session, String instantQueryId,
                                 String tableId, String playerName) throws ProcessingException {
        if (!idExists(tableId))
            throw new ProcessingException("ID " + tableId + " does not exist");
        if (playerManager.hasSession(session))
            throw new ProcessingException("Player is already playing on another table");

        Player player = new Player(session, playerName);

        Table table = tables.stream()
                            .filter(t -> t.getId().equals(tableId))
                            .findFirst()
                            .orElse(null);

        assert table != null;
        table.addPlayer(player);

        playerManager.processJoinPlayer(table, player);

        WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new JOIN_TABLE_SUCCESS(table));

    }

    // должен ли меняться слог, после того как вышел игрок?
    public void processLeaveTable(WebSocketSession session, String instantQueryId) throws ProcessingException {
        Table table = playerManager.getTableBySession(session);
        if (table == null) throw new ProcessingException("No table found for this session");

        Player player = table.getPlayers().stream()
                                        .filter(p -> p.getSession().equals(session))
                                        .findFirst()
                                        .orElse(null);
        assert player != null;


        boolean isCurrent = table.getCurrentPlayer() != null && table.getCurrentPlayer().equals(player);

        table.removePlayer(player);

        Player nextPlayer = null;
        if (isCurrent && table.isGameInProgress())
            nextPlayer = table.getCurrentPlayer();

        playerManager.processLeavePlayer(table, player, nextPlayer);

        if(table.getPlayers().isEmpty())
            tables.remove(table);

        WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new LEAVE_TABLE_SUCCESS());

    }

    public void processStartGame(WebSocketSession session) throws ProcessingException {
        Table table = playerManager.getTableBySession(session);
        if (table == null) throw new ProcessingException("No table found for this session");

        if(isHost(session, table)){
            String syllable = getNewSyllable(table);
            table.start(syllable, startTimer(table, table.getTurnNumber()));

            playerManager.processStartGame(table);
        }
    }

    public void processUpdateWord(WebSocketSession session, String updatedWord) throws ProcessingException {
        Table table = playerManager.getTableBySession(session);
        if (table == null) throw new ProcessingException("No table found for this session");

        if (!table.getCurrentPlayer().getSession().equals(session))
            throw new ProcessingException("You are not the current player");

        table.setCurrentWord(updatedWord.toUpperCase(Locale.ROOT));
        playerManager.processUpdateWord(table, table.getCurrentPlayer());
    }

    public void processConfirmWord(WebSocketSession session) throws ProcessingException {
        Table table = playerManager.getTableBySession(session);
        if (table == null) throw new ProcessingException("No table found for this session");

        Player currentPlayer = table.getCurrentPlayer();

        if (!currentPlayer.getSession().equals(session))
            throw new ProcessingException("You are not the current player");

        if (!wordManager.matches(table.getCurrentWord(), table.getCurrentSyllable()))
            playerManager.processWordRejected(table);
        else {
            String newSyllable = getNewSyllable(table);

            currentPlayer.fillCharacters(table.getCurrentWord());
            if (currentPlayer.getNeededCharacters().isEmpty()){
                currentPlayer.incrementLives();
                currentPlayer.applyNeededCharacters(
                        table.getRequiredLetters(currentPlayer.getCharacterSetGeneration())
                );
                playerManager.processLifeEarned(table, currentPlayer);
            }

            table.turn(newSyllable, startTimer(table, table.getTurnNumber()+1));
            playerManager.processWordAccepted(table, newSyllable,
                    wordManager.getComplexity(newSyllable), table.getCurrentPlayer());
        }

    }

    public void processTimeHasRunOut(Table table) {
        table.getCurrentPlayer().decrementLives();
        String previousSyllable = table.getCurrentSyllable();

        String newSyllable = table.getCurrentSyllable();
        if (table.getCurrentSyllableDuration() + 1 >= Settings.SYLLABLE_ROUNDS_DURATION)
            newSyllable = getNewSyllable(table);

        if (table.countAlivePlayers() == 0){
            table.endGame();
            playerManager.processTimeHasRunOut(table, null,
                    -1, null, wordManager.getPossibleWord(previousSyllable));
        } else {
            table.turn(newSyllable, startTimer(table, table.getTurnNumber()+1));

            String possibleWord = null;
            if(!Objects.equals(previousSyllable, newSyllable))
                possibleWord = wordManager.getPossibleWord(previousSyllable);

            playerManager.processTimeHasRunOut(table, newSyllable,
                    wordManager.getComplexity(newSyllable), table.getCurrentPlayer(), possibleWord
            );
        }
    }

    private String getNewSyllable(Table table){
        float complexity = table.calculateSyllableComplexity();
        SecureRandom random = new SecureRandom();
        float minComplexity = complexity - random.nextFloat(Settings.COMPLEXITY_RANDOMNESS/2);
        float maxComplexity = complexity + random.nextFloat(Settings.COMPLEXITY_RANDOMNESS/2);

        return wordManager.getSyllable(minComplexity, maxComplexity);
    }

    public Timer startTimer(Table table, int turnNumber){
        Timer timer = new java.util.Timer();
        timer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if(table.getTurnNumber() == turnNumber){
                            processTimeHasRunOut(table);
                        }
                    }
                },
                Settings.TIMER_MS
        );

        return timer;
    }

    private String generateRandomId(){
        String id;
        do{
            id = RandomStringUtils.random(Settings.TABLE_ID_LENGTH, true, false).toUpperCase(Locale.ROOT);
        }
        while(idExists(id));

        return id;
    }

    private boolean idExists(String id){
        return tables
                .stream()
                .anyMatch(t -> t.getId().equals(id));
    }

    private boolean isHost(WebSocketSession session, Table table){
        return table.getHost().getSession().equals(session);
    }

    private void deleteTable(Table table){
        tables.remove(table);
    }

    public void processGetMyTable(WebSocketSession session, String instantQueryId) {
        if(playerManager.hasSession(session)){
            Table table = playerManager.getTableBySession(session);
            WebSocketServer.sendInstantQueryResponse(session, instantQueryId, new GET_MY_TABLE(table));
        }
    }
}
