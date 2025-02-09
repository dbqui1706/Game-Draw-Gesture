package fit.nlu.main;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import fit.nlu.adapter.recycleview.message.MessageAdapter;
import fit.nlu.adapter.recycleview.player.PlayerAdapter;
import fit.nlu.canvas.DrawingData;
import fit.nlu.enums.MessageType;
import fit.nlu.enums.RoomState;
import fit.nlu.model.Message;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.model.RoomSetting;
import fit.nlu.model.Turn;
import fit.nlu.service.ApiClient;
import fit.nlu.service.GameApiService;
import fit.nlu.service.GameWebSocketService;
import fit.nlu.state.RoomStateManager;
import fit.nlu.main.controller.RoomUIController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity quản lý logic của phòng chơi.
 * Chỉ đảm nhiệm xử lý sự kiện, kết nối và chuyển giao dữ liệu cho các controller.
 */
public class RoomActivity extends AppCompatActivity implements GameWebSocketService.WebSocketEventListener {
    private static final String TAG = "RoomActivity";
    public static final String KEY_ROOM = "room";
    public static final String KEY_PLAYER = "player";
    public static final String WEB_SOCKET_SERVICE = "webSocketService";

    // Services
    private GameApiService gameApiService;
    private GameWebSocketService webSocketService;

    // Data models
    private Turn currentTurn;
    private Room currentRoom;
    private Player currentPlayer;
    private Player owner;
    private List<Player> players;

    // Utils
    private final Gson gson = new Gson();

    // Controllers
    private RoomUIController uiController;
    private RoomStateManager stateManager;

    // UI Components
    private RecyclerView rvPlayers;
    private RecyclerView rvChat;
    private ImageButton btnLeaveRoom;
    private TextView tvTimer, tvWord, tvWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Khởi tạo UI components
        tvTimer = findViewById(R.id.tvTime);
        tvWord = findViewById(R.id.tv_word);
        tvWaiting = findViewById(R.id.tv_waiting);
        rvPlayers = findViewById(R.id.rvPlayers);
        rvChat = findViewById(R.id.rvChat);
        btnLeaveRoom = findViewById(R.id.btnLeaveRoom);

        // Lấy dữ liệu từ Intent
        initializeData();

        // Khởi tạo service
        setupServices();

        // Khởi tạo controller UI
        uiController = new RoomUIController(
                currentRoom,
                rvPlayers,
                rvChat,
                tvTimer,
                tvWord,
                tvWaiting,
                new PlayerAdapter(this),
                new MessageAdapter(this)
        );
        // Cập nhật UI header ban đầu
        uiController.updateHeader(currentTurn, currentPlayer);
        // Cập nhật danh sách người chơi
        uiController.updatePlayerList(players);

        // Hiển thị thông báo tạo phòng (chẳng hạn hiển thị chủ phòng)
        Message ownerMessage = new Message();
        ownerMessage.setType(MessageType.CREATE_ROOM);
        ownerMessage.setSender(owner);
        uiController.addChatMessage(ownerMessage);

        // Cài đặt nút rời phòng
        setupLeaveButton();

        // Khởi tạo state manager
        stateManager = new RoomStateManager(this, webSocketService);
        stateManager.changeState(currentRoom.getState());
    }

    /**
     * Lấy dữ liệu từ Intent và khởi tạo các model.
     */
    private void initializeData() {
        currentRoom = getIntent().getSerializableExtra(KEY_ROOM, Room.class);
        currentPlayer = getIntent().getSerializableExtra(KEY_PLAYER, Player.class);
        if (currentRoom.getGameSession() != null) {
            currentTurn = currentRoom.getGameSession().getCurrentTurn();
        }
        owner = currentRoom.getOwner();
        players = new ArrayList<>(currentRoom.getPlayers().values());
    }

    /**
     * Khởi tạo các service cần thiết.
     */
    private void setupServices() {
        gameApiService = ApiClient.getClient().create(GameApiService.class);
        webSocketService = new GameWebSocketService(this);
    }

    /**
     * Cài đặt nút rời phòng.
     */
    private void setupLeaveButton() {
        btnLeaveRoom.setOnClickListener(v -> onLeaveRoom());
    }

    /**
     * Xử lý rời phòng.
     */
    private void onLeaveRoom() {
        Call<Void> call = gameApiService.leaveRoom(currentRoom.getId().toString(), currentPlayer);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    finish();
                } else {
                    Log.e(TAG, "onLeaveRoom: Response not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "onLeaveRoom: Failure", t);
            }
        });
    }

    /**
     * Thay thế fragment hiện tại.
     */
    public void replaceFragment(Fragment fragment) {
        if (!isFinishing() && !isDestroyed()) {
            // Truyền data vào fragment
            Bundle args = new Bundle();
            args.putSerializable(WEB_SOCKET_SERVICE, webSocketService);
            args.putSerializable(KEY_ROOM, currentRoom);
            args.putSerializable(KEY_PLAYER, currentPlayer);
            fragment.setArguments(args);

            // Log để debug
            Log.d(TAG, "Replacing fragment: " + fragment.getClass().getSimpleName());
            Log.d(TAG, "Current room: " + (currentRoom != null ? currentRoom.getId() : "null"));
            Log.d(TAG, "Current player: " + (currentPlayer != null ? currentPlayer.getId() : "null"));


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commitNow();
        }
    }

    /**
     * Kiểm tra currentPlayer có phải là chủ phòng không.
     */
    public boolean isOwner() {
        return currentPlayer.getId().equals(owner.getId());
    }

    // --- WebSocket event handlers ---

    @Override
    public void onConnected() {
        String roomId = currentRoom.getId().toString();
        String[] topics = {
                "/topic/room/" + roomId + "/update",
                "/topic/room/" + roomId + "/join",
                "/topic/room/" + roomId + "/leave",
                "/topic/room/" + roomId + "/turn",
                "/topic/room/" + roomId + "/message",
                "/topic/room/" + roomId + "/options",
                "/topic/room/" + roomId + "/start",
                "/topic/room/" + roomId + "/time",
                "/topic/room/" + roomId + "/draw"
        };
        for (String topic : topics) {
            webSocketService.subscribe(topic);
        }
    }

    @Override
    public void onDisconnected() {
        new AlertDialog.Builder(this)
                .setTitle("Disconnected")
                .setMessage("Connection to server lost. Reconnecting...")
                .setPositiveButton("OK", (dialog, which) -> webSocketService.connect())
                .show();
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "WebSocket error: " + error);
    }

    /**
     * Xử lý tin nhắn nhận được từ WebSocket.
     */
    @Override
    public void onMessageReceived(String topic, String message) {
        String roomId = currentRoom.getId().toString();
        if (topic.equals("/topic/room/" + roomId + "/update")) {
            onRoomUpdate(message);
        } else if (topic.equals("/topic/room/" + roomId + "/message")) {
            onReceiveMessage(message);
        } else if (topic.equals("/topic/room/" + roomId + "/options")) {
            onUpdateSetting(message);
        } else if (topic.equals("/topic/room/" + roomId + "/time")) {
            onUpdateTime(message);
        } else if (topic.equals("/topic/room/" + roomId + "/start")) {
            currentRoom.setState(RoomState.PLAYING);
            stateManager.changeState(currentRoom.getState());
        } else if (topic.equals("/topic/room/" + roomId + "/draw")) {
            onDrawingData(message);
        } else if (topic.equals("/topic/room/" + roomId + "/leave")) {
            onPlayerLeave(message);
        } else if (topic.equals("/topic/room/" + roomId + "/turn")) {
            onTurnChange(message);
        }
    }

    private void onTurnChange(String message) {
        try {
            currentRoom.setState(RoomState.PLAYING);
            Turn turn = gson.fromJson(message, Turn.class);
            if (turn == null) return;
            currentTurn = turn;

            // Update UI player list that Player is drawer, header, timer
            runOnUiThread(() -> {
                uiController.updateHeader(currentTurn, currentPlayer);
                uiController.updateDrawingStatus(players, currentTurn.getDrawer().getId().toString());
            });

            stateManager.changeState(RoomState.PLAYING);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing turn change", e);
        }

    }

    private void onPlayerLeave(String message) {
        try {
            Player leavingPlayer = gson.fromJson(message, Player.class);
            if (leavingPlayer != null) {
                // Xóa player dựa trên ID thay vì object
                players.removeIf(p -> p.getId().equals(leavingPlayer.getId()));

                runOnUiThread(() -> {
                    uiController.setRoom(currentRoom);
                    uiController.updatePlayerList(new ArrayList<>(players));
                });
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing player leave", e);
        }
    }

    public void onDrawingData(String message) {
        try {
            Log.d(TAG, "onDrawingData: " + message);
            DrawingData drawingData = gson.fromJson(message, DrawingData.class);
            if (drawingData != null) {
                stateManager.handlePlayerAction("DRAWING", drawingData);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing drawing data", e);
        }
    }


    /**
     * Xử lý cập nhật thời gian từ server.
     */
    private void onUpdateTime(String message) {
        try {
            Message timeMessage = gson.fromJson(message, Message.class);
            if (timeMessage != null) {
                int time = Integer.parseInt(timeMessage.getContent());
                runOnUiThread(() -> uiController.updateTimeFromServer(time));
            }

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing time update", e);
        }
    }

    /**
     * Xử lý tin nhắn chat nhận từ server.
     */
    private void onReceiveMessage(String message) {
        try {
            Message chatMessage = gson.fromJson(message, Message.class);
            if (chatMessage != null) {
                runOnUiThread(() -> uiController.addChatMessage(chatMessage));
                Log.d(TAG, "Chat message: " + chatMessage.getContent());
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing chat message", e);
        }
    }

    /**
     * Xử lý cập nhật setting phòng.
     */
    private void onUpdateSetting(String message) {
        try {
            RoomSetting setting = gson.fromJson(message, RoomSetting.class);
            stateManager.handlePlayerAction("UPDATE_OPTIONS", setting);
            runOnUiThread(() -> tvTimer.setText(String.valueOf(setting.getDrawingTime())));
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error onUpdateSetting: ", e);
        }
    }

    /**
     * Cập nhật thông tin phòng khi có update từ server.
     */
    private void onRoomUpdate(String message) {
        try {
            Room updatedRoom = gson.fromJson(message, Room.class);
            if (updatedRoom != null) {
                for (Player player : updatedRoom.getPlayers().values()) {
                    Log.d(TAG, "onRoomUpdate Player: " + player.getNickname() + " | isDrawing: " + player.isDrawing());
                }
                currentRoom = updatedRoom;
                // Nếu currentPlayer trở thành owner, cập nhật trạng thái
                if (currentRoom.getOwner().getId().equals(currentPlayer.getId())) {
                    currentPlayer.setOwner(true);
                }
                // Cập nhật danh sách người chơi
                players = new ArrayList<>(currentRoom.getPlayers().values());

                // Cập nhật UI
                runOnUiThread(() -> {
                    uiController.setRoom(currentRoom);
                    uiController.updatePlayerList(players);
                    uiController.updateHeader(currentTurn, currentPlayer);
                });

                // Cập nhật trạng thái phòng nếu chỉ còn 1 người chơi
                if (players.size() < 2) {
                    currentRoom.setState(RoomState.WAITING);
                    stateManager.changeState(currentRoom.getState());
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing room update", e);
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RoomActivity onDestroy");
        if (webSocketService != null) {
            String roomId = currentRoom.getId().toString();
            webSocketService.unsubscribe("/topic/room/" + roomId + "/update");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/message");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/options");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/start");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/time");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/draw");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/join");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/leave");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/turn");
            webSocketService.disconnect();
        }
    }

    public Turn getCurrentTurn() {
        return currentTurn != null ? currentTurn : currentRoom.getGameSession().getCurrentTurn();
    }
}
