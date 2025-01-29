package fit.nlu.model;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import fit.nlu.enums.RoomState;

public class Room {
    private UUID id;
    private Player owner;
    private Map<UUID, Player> players;
    private ChatSystem chatSystem;
    private GameSession gameSession;
    private RoomSetting setting;
    private RoomState state;
    private Timestamp createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Map<UUID, Player> getPlayers() {
        return players;
    }

    public void setPlayers(Map<UUID, Player> players) {
        this.players = players;
    }

    public ChatSystem getChatSystem() {
        return chatSystem;
    }

    public void setChatSystem(ChatSystem chatSystem) {
        this.chatSystem = chatSystem;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public RoomSetting getSetting() {
        return setting;
    }

    public void setSetting(RoomSetting setting) {
        this.setting = setting;
    }

    public RoomState getState() {
        return state;
    }

    public void setState(RoomState state) {
        this.state = state;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
