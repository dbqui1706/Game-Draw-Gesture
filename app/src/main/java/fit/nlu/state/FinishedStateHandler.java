package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.fragment.FragmentFinished;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;

public class FinishedStateHandler extends RoomStateHandler {
    public FinishedStateHandler(RoomActivity roomActivity, GameWebSocketService webSocket) {
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
        return new FragmentFinished();
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {

    }
}
