package fit.nlu.patterns.strategy;

import fit.nlu.main.RoomActivity;
import fit.nlu.model.Message;

public interface MessageHandler {
    void handle(Message message, RoomActivity roomActivity);
}
