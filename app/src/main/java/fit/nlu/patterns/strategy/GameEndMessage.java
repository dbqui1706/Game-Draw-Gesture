package fit.nlu.patterns.strategy;

import fit.nlu.main.RoomActivity;
import fit.nlu.model.Message;

public class GameEndMessage implements MessageHandler {
    @Override
    public void handle(Message message, RoomActivity roomActivity) {
        // Update recycler view
//        roomActivity.setUpStatusHeader();
    }
}
