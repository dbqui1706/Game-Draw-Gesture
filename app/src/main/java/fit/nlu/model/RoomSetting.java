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
    private int hintCount;
    private Set<String> dictionary;
    private List<String> customWords;

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public int getDrawingTime() {
        return drawingTime;
    }

    public void setDrawingTime(int drawingTime) {
        this.drawingTime = drawingTime;
    }

    public int getTotalRound() {
        return totalRound;
    }

    public void setTotalRound(int totalRound) {
        this.totalRound = totalRound;
    }

    public int getHintCount() {
        return hintCount;
    }

    public void setHintCount(int hintCount) {
        this.hintCount = hintCount;
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }

    public List<String> getCustomWords() {
        return customWords;
    }

    public void setCustomWords(List<String> customWords) {
        this.customWords = customWords;
    }
}
