package fit.nlu.state;

import android.view.View;

import androidx.fragment.app.Fragment;

import fit.nlu.enums.RoomState;
import fit.nlu.fagment.FragmentWaitingRoom;
import fit.nlu.main.R;
import fit.nlu.main.RoomActivity;
import fit.nlu.service.GameWebSocketService;
import fit.nlu.service.WebSocketMessage;

public class WaitingStateHandler extends RoomStateHandler {
    public WaitingStateHandler(RoomActivity roomActivity, GameWebSocketService webSocket) {
        super(roomActivity, webSocket);
    }

    @Override
    public void onEnter() {
        // Hiện room options và danh sách người chơi
        roomActivity.findViewById(R.id.roomOptions).setVisibility(View.VISIBLE);
        roomActivity.findViewById(R.id.rvPlayers).setVisibility(View.VISIBLE);

        // Nếu là chủ phòng, hiện nút Start
//        if (roomActivity.isOwner()) {
//            roomActivity.findViewById(R.id.btn_start).setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onExit() {
        roomActivity.findViewById(R.id.roomOptions).setVisibility(View.GONE);
        roomActivity.findViewById(R.id.btn_start).setVisibility(View.GONE);
    }

    @Override
    public Fragment getStateFragment() {
        return new FragmentWaitingRoom();
    }

    @Override
    public void handlePlayerAction(String action, Object... params) {
        switch (action){
            case "START_GAME":
                webSocket.sendMessage(WebSocketMessage.STATE_CHANGED, RoomState.CHOOSING);
                break;
        }
    }

}
