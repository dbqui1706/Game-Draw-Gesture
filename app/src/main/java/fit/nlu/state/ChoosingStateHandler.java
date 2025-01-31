package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.fagment.FragmentChooseWord;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;

public class ChoosingStateHandler extends RoomStateHandler {

    public ChoosingStateHandler(RoomActivity roomActivity, GameWebSocketService webSocket) {
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
        return new FragmentChooseWord();
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {

    }
}
