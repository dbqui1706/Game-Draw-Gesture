package fit.nlu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import fit.nlu.enums.TurnState;
import lombok.Data;

@Data
public class Turn implements Serializable {
    private String id;
    private Player drawer;
    private String keyword;
    private List<Guess> guesses;
    private TurnState state;
    private Timestamp startTime;
    private Timestamp endTime;
    private int timeLimit; // giây
    private String roomId;

    public String getId() {
        return id;
    }

    public Player getDrawer() {
        return drawer;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<Guess> getGuesses() {
        return guesses;
    }

    public TurnState getState() {
        return state;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getRemainingTime() {
        if (startTime == null) return timeLimit;
        long elapsedMillis = System.currentTimeMillis() - startTime.getTime();
        int elapsedSeconds = (int) (elapsedMillis / 1000);
        int remaining = timeLimit - elapsedSeconds;
        return Math.max(0, remaining);
    }
}
