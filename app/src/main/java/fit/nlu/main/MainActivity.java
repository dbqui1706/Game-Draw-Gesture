package fit.nlu.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.ApiClient;
import fit.nlu.service.GameApiService;
import fit.nlu.service.GameWebSocketService;
import fit.nlu.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GameWebSocketService.WebSocketEventListener {
    // Constants để dễ maintain và tránh hardcode
    private static final String TAG = "MainActivity";
    private static final String KEY_PLAYER = "player";
    private static final String KEY_ROOM = "room";

    // Services và model objects
    private GameApiService gameApiService;
    private GameWebSocketService webSocketService;
    private Player player;

    // UI components
    private EditText edtNickname;
    private ImageView avatarImageView;
    private MaterialButton btnRooms;
    private MaterialButton btnCreateRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo services
        initializeServices();

        // Setup UI components
        initializeViews();

        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Khởi tạo các services cần thiết
     */
    private void initializeServices() {
        webSocketService = new GameWebSocketService(this);
        gameApiService = ApiClient.getClient().create(GameApiService.class);
    }

    /**
     * Tìm và gán các view từ layout
     */
    private void initializeViews() {
        edtNickname = findViewById(R.id.edtNickname);
        edtNickname.setText(Util.generateUniqueUsername());
        avatarImageView = findViewById(R.id.avatar_player);
        btnRooms = findViewById(R.id.btnRoom);
        btnCreateRoom = findViewById(R.id.btnNewRoom);
    }

    /**
     * Setup click listeners cho các buttons
     */
    private void setupClickListeners() {
        btnRooms.setOnClickListener(v -> navigateToRooms());
        btnCreateRoom.setOnClickListener(v -> handleCreateRoom());
    }

    /**
     * Lấy thông tin player từ input fields
     */
    private Player createPlayerFromInput(boolean isOwner) {
        String nickname = edtNickname.getText().toString();
        String avatar = avatarImageView.toString();
        return new Player(nickname, avatar, isOwner);
    }

    /**
     * Chuyển sang màn hình danh sách phòng
     */
    private void navigateToRooms() {
        player = createPlayerFromInput(false);
        Intent intent = new Intent(this, RoomsActivity.class);
        intent.putExtra(KEY_PLAYER, player);
        startActivity(intent);
    }

    /**
     * Xử lý tạo phòng mới
     */
    private void handleCreateRoom() {
        player = createPlayerFromInput(true);
        createRoomOnServer(player);
    }

    /**
     * Gọi API tạo phòng mới
     */
    private void createRoomOnServer(Player player) {
        Call<Room> call = gameApiService.createRoom(player);
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(@NonNull Call<Room> call, @NonNull Response<Room> response) {
                handleCreateRoomResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<Room> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to create room", t);
                // TODO: Hiển thị thông báo lỗi cho người dùng
            }
        });
    }

    /**
     * Xử lý response khi tạo phòng thành công
     */
    private void handleCreateRoomResponse(Response<Room> response) {
        if (response.isSuccessful() && response.body() != null) {
            Room room = response.body();
            navigateToRoom(room);
        }
    }

    /**
     * Chuyển sang màn hình phòng
     */
    private void navigateToRoom(Room room) {
        Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra(KEY_ROOM, room);
        intent.putExtra(KEY_PLAYER, player);
        startActivity(intent);
    }

    // WebSocket event handlers
    @Override
    public void onConnected() {
//        webSocketService.subscribe("/topic/rooms");
    }

    @Override
    public void onDisconnected() {
        // TODO: Implement reconnection logic

    }

    @Override
    public void onError(String error) {
        // TODO: Show error to user
    }

    @Override
    public void onMessageReceived(String topic, String message) {
        // TODO: Handle received messages based on topic
    }
}