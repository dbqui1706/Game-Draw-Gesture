package fit.nlu.model;

import java.io.Serializable;
import java.util.UUID;

import fit.nlu.enums.PlayerStatus;

public class Player implements Serializable {
    private UUID id;
    private String roomId;
    private String nickname;
    private boolean isOwner;
    private String avatar;
    private int score;
    private boolean isDrawing;
    private PlayerStatus status;

    public Player(String nickname, String avatar, boolean isOwner) {
        this.id = UUID.randomUUID();
        this.nickname = nickname;
        this.avatar = avatar;
        this.isOwner = isOwner;
        this.isDrawing = false;
        this.status = PlayerStatus.IDLE;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public void setDrawing(boolean drawing) {
        isDrawing = drawing;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }
}
