package fit.nlu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fit.nlu.enums.GameState;
import lombok.Data;

@Data
public class GameSession implements Serializable {
    private String id;
    private List<Round> rounds;
    private Round currentRound;
    private List<Player> players;
    private GameState state;
    private Timestamp startTime;
    private Timestamp endTime;
    private int currentRoundNumber;
    private int totalRounds;
    private int turnTimeLimit;
    private String roomId;

    public Round getCurrentRound() {
        return currentRound;
    }

    public Turn getCurrentTurn() {
        if (state != GameState.PLAYING) return null;
        if (currentRound == null) return null;
        return getCurrentRound().getCurrentTurn();
    }
}
