package fit.nlu.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class RoomSetting implements Serializable  {
    private int maxPlayer;
    private int totalRound;
    private int drawingTime;
    private Set<String> dictionary;
    private List<String> customWords;
}
