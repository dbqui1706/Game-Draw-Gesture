package fit.nlu.dto;

import java.io.Serializable;

import fit.nlu.enums.RoomState;

public class RoomResponse implements Serializable {
    private String roomId;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private RoomState state;

    public RoomResponse(String roomId, String name, int maxPlayers, int currentPlayers, RoomState state) {
        this.roomId = roomId;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.state = state;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }
    public RoomState getState() {
        return state;
    }
}
