package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.canvas.DrawingData;
import fit.nlu.fragment.FragmentDraw;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;

public class PlayingStateHandler extends RoomStateHandler {
    private FragmentDraw fragmentDraw;

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
        fragmentDraw = new FragmentDraw(webSocket,
                roomActivity.getCurrentRoom(),
                roomActivity.getCurrentTurn(),
                roomActivity.getCurrentPlayer());
        return fragmentDraw;
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {
        switch (action) {
            case "DRAWING":
                fragmentDraw.onDrawingDataReceived((DrawingData) params[0]);
                break;
            default:
                break;
        }
    }
}
