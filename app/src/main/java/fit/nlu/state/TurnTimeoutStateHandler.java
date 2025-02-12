package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.fragment.FragmentTurnTimeout;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;

public class TurnTimeoutStateHandler extends RoomStateHandler {
    private FragmentTurnTimeout fragmentTurnTimeout;

    public TurnTimeoutStateHandler(RoomActivity activity, GameWebSocketService webSocket) {
        super(activity, webSocket);
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }

    @Override
    public Fragment getStateFragment() {
        fragmentTurnTimeout = new FragmentTurnTimeout(webSocket,
                roomActivity.getCurrentRoom(), roomActivity.getCurrentTurn());
        return fragmentTurnTimeout;
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {

    }
}
