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

    public UUID getId() {
        return id;
    }

    public Player getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    public MessageMetadata getMetadata() {
        return metadata;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setMetadata(MessageMetadata metadata) {
        this.metadata = metadata;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
