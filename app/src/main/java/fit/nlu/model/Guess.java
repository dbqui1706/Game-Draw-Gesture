package fit.nlu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;

@Data
public class Guess implements Serializable {
    private UUID id;
    private Player player;
    private Turn turn;
    private String content;
    private int point;
    private Timestamp timestamp;
    private Timestamp timeTaken;
}
