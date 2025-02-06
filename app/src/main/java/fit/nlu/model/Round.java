package fit.nlu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import fit.nlu.enums.RoundState;
import lombok.Data;

@Data
public class Round implements Serializable {
    private String id;
    private List<Turn> turns;
    private Turn currentTurn;
    private Queue<Player> remainingPlayers;
    private Set<Player> completedPlayers;
    private RoundState state;
    private Timestamp startTime;
    private Timestamp endTime;
    private int turnTimeLimit;
    private String roomId;
    private int roundNumber;

    public Turn getCurrentTurn() {
        return currentTurn;
    }
}
