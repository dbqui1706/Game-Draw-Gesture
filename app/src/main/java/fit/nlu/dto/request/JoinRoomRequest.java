package fit.nlu.dto.request;

import fit.nlu.model.Player;

public class JoinRoomRequest {
    private String roomId;
    private Player player;

    public JoinRoomRequest(String roomId, Player player) {
        this.roomId = roomId;
        this.player = player;
    }

    public String getRoomId() {
        return roomId;
    }

    public Player getPlayer() {
        return player;
    }
}
