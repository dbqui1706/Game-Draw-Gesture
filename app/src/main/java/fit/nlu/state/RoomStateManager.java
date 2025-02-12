package fit.nlu.state;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import fit.nlu.enums.RoomState;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;

public class RoomStateManager {
    private RoomActivity activity;
    private RoomState currentState;
    private Map<RoomState, RoomStateHandler> stateHandlers;
    private GameWebSocketService webSocket;

    public RoomStateManager(RoomActivity activity, GameWebSocketService webSocket) {
        this.activity = activity;
        this.webSocket = webSocket;
        initStateHandlers();
    }

    private void initStateHandlers() {
        stateHandlers = new HashMap<>();
        stateHandlers.put(RoomState.WAITING, new WaitingStateHandler(activity, webSocket));
        stateHandlers.put(RoomState.CHOOSING, new ChoosingStateHandler(activity, webSocket));
        stateHandlers.put(RoomState.PLAYING, new PlayingStateHandler(activity, webSocket));
        stateHandlers.put(RoomState.TURN_TIMEOUT, new TurnTimeoutStateHandler(activity, webSocket));
        stateHandlers.put(RoomState.FINISHED, new FinishedStateHandler(activity, webSocket));
    }

    public void changeState(RoomState newState) {
        if (currentState != null) {
            stateHandlers.get(currentState).onExit();
        }
        currentState = newState;
        RoomStateHandler handler = stateHandlers.get(currentState);
        handler.onEnter();

        // Thay đổi fragment
        Fragment fragment = handler.getStateFragment();
        if (fragment != null) {
            activity.replaceFragment(fragment);
        }
    }

    public void handlePlayerAction(String action, Object... params) {
        if (currentState != null) {
            stateHandlers.get(currentState).handlePlayerAction(action, params);
        }
    }
}
