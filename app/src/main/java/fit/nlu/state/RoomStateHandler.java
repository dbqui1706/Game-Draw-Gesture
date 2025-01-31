package fit.nlu.state;

import androidx.fragment.app.Fragment;

import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;

public abstract class RoomStateHandler {
    protected RoomActivity roomActivity;
    protected GameWebSocketService webSocket;

    public RoomStateHandler(RoomActivity roomActivity, GameWebSocketService webSocket) {
        this.roomActivity = roomActivity;
        this.webSocket = webSocket;
    }

    public abstract void onEnter();
    public abstract void onExit();
    public abstract Fragment getStateFragment();
    public abstract void handlePlayerAction(String action, Object... params);
}
