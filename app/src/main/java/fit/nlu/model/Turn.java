package fit.nlu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Turn implements Serializable {
    private UUID id;
    private Player drawer;
    private String keyword;
    private List<Guess> guesses;
    private Timestamp startTime;
    private Timestamp endTime;
}
