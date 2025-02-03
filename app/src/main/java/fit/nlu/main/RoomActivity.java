package fit.nlu.main;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import fit.nlu.adapter.recycleview.player.PlayerAdapter;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.ApiClient;
import fit.nlu.service.GameApiService;
import fit.nlu.service.GameWebSocketService;
import fit.nlu.state.RoomStateManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity quản lý giao diện và logic của một phòng chơi.
 * Xử lý hiển thị danh sách người chơi, trạng thái phòng và các tương tác WebSocket.
 */
public class RoomActivity extends AppCompatActivity implements GameWebSocketService.WebSocketEventListener {
    // Constants để quản lý logging và intent keys
    private static final String TAG = "RoomActivity";
    private static final String KEY_ROOM = "room";
    private static final String KEY_PLAYER = "player";

    // Services để tương tác với server
    private GameApiService gameApiService;
    private GameWebSocketService webSocketService;

    // UI Components
    private RecyclerView rvPlayers;
    private PlayerAdapter adapter;
    private ImageButton btnLeaveRoom;

    // Data models
    private Player currentPlayer;  // Người chơi hiện tại
    private Player owner;         // Chủ phòng
    private Room currentRoom;     // Thông tin phòng hiện tại
    private List<Player> players; // Danh sách người chơi trong phòng
    private RoomStateManager stateManager; // Quản lý trạng thái phòng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        initializeData();
        setupServices();
        setupUI();
        setupStateManager();
    }

    /**
     * Khởi tạo dữ liệu từ Intent
     */
    private void initializeData() {
        currentRoom = getIntent().getSerializableExtra(KEY_ROOM, Room.class);
        currentPlayer = getIntent().getSerializableExtra(KEY_PLAYER, Player.class);
        owner = currentRoom.getOwner();
        players = new ArrayList<>(currentRoom.getPlayers().values());
    }

    /**
     * Khởi tạo các services cần thiết
     */
    private void setupServices() {
        gameApiService = ApiClient.getClient().create(GameApiService.class);
        webSocketService = new GameWebSocketService(this);
    }

    /**
     * Thiết lập giao diện người dùng
     */
    private void setupUI() {
        setupPlayerList();
        setupLeaveButton();
    }

    /**
     * Thiết lập RecyclerView hiển thị danh sách người chơi
     */
    private void setupPlayerList() {
        rvPlayers = findViewById(R.id.rvPlayers);
        adapter = new PlayerAdapter(this);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvPlayers.setAdapter(adapter);
        adapter.updatePlayers(players);
    }

    /**
     * Thiết lập nút rời phòng
     */
    private void setupLeaveButton() {
        btnLeaveRoom = findViewById(R.id.btnLeaveRoom);
        btnLeaveRoom.setOnClickListener(v -> onLeaveRoom());
    }

    private void onLeaveRoom() {
        // Gửi request rời phòng
        Call<Void> call = gameApiService.leaveRoom(
                currentRoom.getId().toString(),
                currentPlayer
        );
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Rời phòng thành công
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    /**
     * Khởi tạo và cấu hình RoomStateManager
     */
    private void setupStateManager() {
        stateManager = new RoomStateManager(this, null);
        stateManager.changeState(currentRoom.getState());
    }

    /**
     * Thay thế fragment hiện tại trong container
     */
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /**
     * Kiểm tra xem người chơi hiện tại có phải là chủ phòng không
     */
    public boolean isOwner() {
        return currentPlayer.getId().equals(owner.getId());
    }

    // WebSocket event handlers
    @Override
    public void onConnected() {

        // Đăng ký nhận cập nhật từ topic của phòng hiện tại
        String roomTopic = String.format("/topic/room/%s/update", currentRoom.getId());
        webSocketService.subscribe(roomTopic);

        // Lắng nghe sự kiện rời phòng
        webSocketService.subscribe("/topic/room/" + currentRoom.getId() + "/leave");
    }

    @Override
    public void onDisconnected() {
        // TODO: Implement reconnection logic
        // Hiển thị dialog thông báo và thử kết nối lại
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disconnected");
        builder.setMessage("Connection to server lost. Reconnecting...");
        builder.setPositiveButton("OK", (dialog, which) -> {
            webSocketService.connect();
        });
    }

    @Override
    public void onError(String error) {
        // TODO: Implement error handling
        // Hiển thị thông báo lỗi cho người dùng
        Log.e(TAG, "WebSocket error: " + error);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("An error occurred: " + error);
        builder.setPositiveButton("OK", null);
    }

    /**
     * Xử lý các tin nhắn WebSocket nhận được từ server
     */
    @Override
    public void onMessageReceived(String topic, String message) {
        if (topic.equals("/topic/room/" + currentRoom.getId() + "/update")) {
            onRoomUpdate(message);
        }else if (topic.equals("/topic/room/" + currentRoom.getId() + "/leave")) {
            onPlayerLeave(message);
        }
    }

    /**
     * Xử lý sự kiện người chơi rời phòng
     */
    private void onPlayerLeave(String message) {
        try {
            Player player = new Gson().fromJson(message, Player.class);
            if (player != null) {
                players.remove(player);
                runOnUiThread(() -> adapter.updatePlayers(players));
                Log.d(TAG, "Player left: " + player.getNickname());
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing player leave message", e);
        }
    }

    /**
     * Cập nhật thông tin phòng khi nhận được update từ server
     */
    private void onRoomUpdate(String message) {
        try {
            Room updatedRoom = new Gson().fromJson(message, Room.class);
            if (updatedRoom != null) {
                updateRoomState(updatedRoom);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing room update", e);
        }
    }

    /**
     * Cập nhật UI với thông tin phòng mới
     */
    private void updateRoomState(Room updatedRoom) {
        currentRoom = updatedRoom;
        List<Player> updatedPlayers = new ArrayList<>(updatedRoom.getPlayers().values());

        runOnUiThread(() -> {
            adapter.updatePlayers(updatedPlayers);
            Log.d(TAG, "Updated players list: " + updatedPlayers.size());
            // TODO: Cập nhật các thành phần UI khác nếu cần
        });
    }

    // Getters for current state
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketService != null) {
            webSocketService.unsubscribe("/topic/room/" + currentRoom.getId() + "/update");
            webSocketService.unsubscribe("/topic/room/" + currentRoom.getId() + "/leave");
            webSocketService.disconnect();
        }
    }
}
