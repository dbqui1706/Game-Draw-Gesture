package fit.nlu.model;

import java.io.Serializable;
import java.util.UUID;

import fit.nlu.enums.MessageType;
import lombok.Data;

@Data
public class Message implements Serializable {
    private UUID id;
    private Player sender;
    private String content;
    private MessageType type;
    private MessageMetadata metadata;
    private long createdAt;
}
