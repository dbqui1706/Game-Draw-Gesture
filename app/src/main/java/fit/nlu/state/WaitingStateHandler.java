package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.enums.RoomState;
import fit.nlu.fragment.FragmentWaitingRoom;
import fit.nlu.main.RoomActivity;
import fit.nlu.model.RoomSetting;
import fit.nlu.service.GameWebSocketService;

public class WaitingStateHandler extends RoomStateHandler {
    private FragmentWaitingRoom fragment;

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
        fragment = new FragmentWaitingRoom(webSocket, roomActivity.getCurrentRoom(), roomActivity.getCurrentPlayer());
        return fragment;
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {
        switch (action) {
            case "UPDATE_OPTIONS":
                fragment.updateSetting((RoomSetting) params[0]);
                break;
            default:
                break;
        }
    }


}
