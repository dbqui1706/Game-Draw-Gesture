package fit.nlu.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import fit.nlu.canvas.DrawingData;
import fit.nlu.canvas.DrawingView;
import fit.nlu.main.R;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.model.Turn;
import fit.nlu.service.GameWebSocketService;

public class FragmentDraw extends Fragment {
    private static final String TAG = "FragmentDraw";
    private GameWebSocketService webSocketService;
    private Turn currentTurn;
    private Room currentRoom;
    private Player currentPlayer;
    private DrawingView drawingView;
    private ImageButton btnClear, btnUndo, btnPen;
    private Gson gson = new Gson();

    public FragmentDraw(GameWebSocketService webSocketService,
                        Room room, Turn turn, Player player) {
        Log.d("FragmentDraw", "FragmentDraw constructor called");
        Log.d("FragmentDraw", "webSocketService: " + webSocketService);
        Log.d("FragmentDraw", "currentRoom: " + room);
        Log.d("FragmentDraw", "currentPlayer: " + player);

        this.webSocketService = webSocketService;
        this.currentRoom = room;
        this.currentTurn = turn;
        this.currentPlayer = player;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw, container, false);
        // Khởi tạo các view
        drawingView = view.findViewById(R.id.drawingView);
        drawingView.setWebSocketService(webSocketService);
        drawingView.setRoomId(currentRoom.getId().toString());
        btnClear = view.findViewById(R.id.btnClear);
        btnUndo = view.findViewById(R.id.btnUndo);
        btnPen = view.findViewById(R.id.btnPen);

        // Set visibility cho các nút dựa vào quyền của người chơi và ngăn chặn việc vẽ khi không phải lượt của mình
        if (currentTurn != null) {
            Player drawer = currentTurn.getDrawer();
            if (drawer.getId().equals(currentPlayer.getId())) {
                btnClear.setVisibility(View.VISIBLE);
                btnUndo.setVisibility(View.VISIBLE);
                btnPen.setVisibility(View.VISIBLE);
                drawingView.setDrawingEnabled(true);
            } else {
                btnClear.setVisibility(View.GONE);
                btnUndo.setVisibility(View.GONE);
                btnPen.setVisibility(View.GONE);
                drawingView.setDrawingEnabled(false);
            }
        } else {
            Log.w("FragmentDraw", "Current turn is null");
            // Có thể hiển thị trạng thái chờ hoặc disable thao tác vẽ
        }

        // Xử lý sự kiện cho các nút
        setupButtons();

        // Áp dụng animation cho root view của fragment
        view.setTranslationY(-container.getHeight()); // Đặt vị trí ban đầu ở trên màn hình
        view.animate()
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // replay drawing data
        drawingView.post(() -> {
            drawingView.replayDrawingEvents(currentTurn.getDrawingDataList());
        });

        return view;
    }

    public void onDrawingDataReceived(DrawingData data) {
        Log.d("FragmentDraw", "Drawing data received: " + data);
        if (drawingView == null) {
            Log.w("FragmentDraw", "onDrawingDataReceived DrawingView is null");
            return;
        }
        if (currentTurn == null) {
            Log.w("FragmentDraw", "Current turn is null");
            return;
        }
        if (!currentPlayer.getId().equals(currentTurn.getDrawer().getId())) {
            // Chỉ xử lý drawing data từ người khác
            getActivity().runOnUiThread(() -> {
                drawingView.handleRemoteDrawing(data);
            }); // Chỉ xử lý drawing data từ người khác
        }
    }

    private void setupButtons() {
        // Xử lý nút Clear
        btnClear.setOnClickListener(v -> {
            drawingView.clear(); // Xóa toàn bộ vẽ
        });

        // Xử lý nút Undo
        btnUndo.setOnClickListener(v -> drawingView.undo());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

