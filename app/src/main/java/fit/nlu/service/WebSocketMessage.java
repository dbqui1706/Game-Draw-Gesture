package fit.nlu.service;

public class WebSocketMessage <T> {
    private String type;
    private T data;

    public static final String STATE_CHANGED = "STATE_CHANGED";
    public static final String PLAYER_JOINED = "PLAYER_JOINED";
    public static final String PLAYER_LEFT = "PLAYER_LEFT";
    public static final String GAME_UPDATE = "GAME_UPDATE";

    public WebSocketMessage(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }
}
