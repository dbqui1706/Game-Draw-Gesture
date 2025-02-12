package fit.nlu.patterns.strategy;

import fit.nlu.main.RoomActivity;
import fit.nlu.model.Message;
import fit.nlu.model.Player;

public class LeaveMessage implements MessageHandler {
    @Override
    public void handle(Message message, RoomActivity roomActivity) {
        // Update recycler view
        Player playerLeave = message.getSender();
//        roomActivity.removePlayerToRV(playerLeave);
    }
}
