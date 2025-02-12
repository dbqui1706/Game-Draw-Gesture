package fit.nlu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import fit.nlu.canvas.DrawingData;
import fit.nlu.enums.TurnState;
import lombok.Data;

@Data
public class Turn implements Serializable {
    private String id;
    private Player drawer;
    private String keyword;
    private Set<Guess> guesses;
    private TurnState state;
    private Timestamp startTime;
    private Timestamp endTime;
    private int timeLimit; // giây
    private int serverRemainingTime;
    private String roomId;
    private List<DrawingData> drawingDataList;

    public String getId() {
        return id;
    }

    public Player getDrawer() {
        return drawer;
    }

    public String getKeyword() {
        return keyword;
    }

    public Set<Guess> getGuesses() {
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

    public int getServerRemainingTime() {
        return serverRemainingTime;
    }

    public List<DrawingData> getDrawingDataList() {
        return drawingDataList;
    }

    public int getRemainingTime() {
        if (startTime == null) return timeLimit;
        long serverStartTime = startTime.getTime(); // timestamp từ server
        int timeLimit = this.timeLimit;
        long clientNow = System.currentTimeMillis();
        int remaining = timeLimit - (int) ((clientNow - serverStartTime) / 1000);
        return Math.max(0, remaining);
    }
}
