package fit.nlu.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ChatSystem implements Serializable {
    private Room room;
    private List<Message> messages;
    private int messageLimit;
    private List<Message> systemMessages;
}
