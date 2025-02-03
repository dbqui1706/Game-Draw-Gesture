package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.enums.RoomState;
import fit.nlu.fagment.FragmentWaitingRoom;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;
import fit.nlu.service.WebSocketMessage;

public class WaitingStateHandler extends RoomStateHandler {
    public WaitingStateHandler(RoomActivity roomActivity, GameWebSocketService webSocket) {
        super(roomActivity, webSocket);
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }

    @Override
    public Fragment getStateFragment() {
        return FragmentWaitingRoom.newInstance(roomActivity.getCurrentRoom(), roomActivity.getCurrentPlayer());
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {
        switch (action) {
            case "START_GAME":
                webSocket.sendMessage(WebSocketMessage.STATE_CHANGED, String.valueOf(RoomState.CHOOSING));
                break;
        }
    }



}
