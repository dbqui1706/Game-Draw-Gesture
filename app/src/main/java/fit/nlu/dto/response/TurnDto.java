package fit.nlu.dto.response;

import fit.nlu.model.Player;

public class TurnDto {
    private String turnId;
    private Player drawer;
    private int timeLimit;
    private String keyword; // Chỉ có giá trị với người vẽ; null đối với người đoán
    private String eventType; // Ví dụ: "TURN_START"

    public TurnDto() {
    }

    public TurnDto(String turnId, Player drawer, int timeLimit, String keyword, String eventType) {
        this.turnId = turnId;
        this.drawer = drawer;
        this.timeLimit = timeLimit;
        this.keyword = keyword;
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
    public int getTimeLimit() {
        return timeLimit;
    }

    public String getKeyword() {
        return keyword;
    }
}
