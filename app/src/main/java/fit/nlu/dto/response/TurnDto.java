package fit.nlu.dto.response;

import fit.nlu.model.Player;

public class TurnDto {
    private String turnId;
    private Player drawer;
    private int timeLimit;
    private int remainingTime;
    private String keyword; // Chỉ có giá trị với người vẽ; null đối với người đoán
    private String eventType; // Ví dụ: "TURN_START"

    public String getEventType() {
        return eventType;
    }
    public int getTimeLimit() {
        return timeLimit;
    }

    public String getKeyword() {
        return keyword;
    }
    public int getRemainingTime() {
        return remainingTime;
    }

    public Player getDrawer() {
        return drawer;
    }

    @Override
    public String toString() {
        return "TurnDto{" +
                "turnId='" + turnId + '\'' +
                ", drawer=" + drawer +
                ", timeLimit=" + timeLimit +
                ", remainingTime=" + remainingTime +
                ", keyword='" + keyword + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
