package fit.nlu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;

@Data
public class Guess implements Serializable {
    private UUID id;
    private Player player;
    private String content;
    private int score;
    private Timestamp timestamp;
    private Timestamp timeTaken;

    public UUID getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public String getContent() {
        return content;
    }

    public int getScore() {
        return score;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Timestamp getTimeTaken() {
        return timeTaken;
    }
}
