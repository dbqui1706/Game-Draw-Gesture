package fit.nlu.model;

import java.io.Serializable;
import java.util.UUID;

import fit.nlu.enums.PlayerStatus;
import lombok.Data;

@Data
public class Player implements Serializable {
    private UUID id;
    private String roomId;
    private String nickname;
    private boolean isOwner;
    private String avatar;
    private int score;
    private boolean drawing;
    private PlayerStatus status;

    public Player(String nickname, String avatar, boolean isOwner) {
        this.id = UUID.randomUUID();
        this.nickname = nickname;
        this.avatar = avatar;
        this.isOwner = isOwner;
        this.drawing = false;
        this.status = PlayerStatus.IDLE;
    }

    public UUID getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getScore() {
        return score;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public PlayerStatus getStatus() {
        return status;
    }
    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }
    public void setOwner(boolean owner) {
        this.isOwner = owner;
    }
}
