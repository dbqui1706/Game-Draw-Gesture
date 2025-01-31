package fit.nlu.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class MessageMetadata implements Serializable {
    private boolean isGuess;
    private boolean isCorrect;
    private boolean isSystemMessage;
    private String color;
}
