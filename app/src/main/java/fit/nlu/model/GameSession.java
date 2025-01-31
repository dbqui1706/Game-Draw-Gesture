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
    private UUID id;
    private Room room;
    private List<Round> rounds;
    private Round currentRound;
    private List<Player> players;
    private Map<UUID, Integer> scores;
    private GameState state;
    private Timestamp startTime;
    private Timestamp endTime;
}
