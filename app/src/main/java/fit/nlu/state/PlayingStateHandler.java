package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.fragment.FragmentDraw;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;

public class PlayingStateHandler extends RoomStateHandler {
    public PlayingStateHandler(RoomActivity roomActivity, GameWebSocketService webSocket) {
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
        return new FragmentDraw();
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {

    }
}
