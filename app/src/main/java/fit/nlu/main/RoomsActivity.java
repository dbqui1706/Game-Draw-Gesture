package fit.nlu.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fit.nlu.adapter.recycleview.room.RoomAdapter;
import fit.nlu.adapter.recycleview.room.RoomItem;
import fit.nlu.dto.RoomResponse;
import fit.nlu.dto.request.JoinRoomRequest;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.ApiClient;
import fit.nlu.service.GameApiService;
import fit.nlu.service.GameWebSocketService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RoomsActivity extends AppCompatActivity implements GameWebSocketService.WebSocketEventListener {
    private static final String TAG = "RoomsActivity";
    private Player player;
    private GameApiService gameApiService;
    private List<RoomResponse> rooms;
    private RoomAdapter adapter; // Thêm adapter là biến instance
    private RecyclerView roomsRV;
    private GameWebSocketService webSocketService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        rooms = new ArrayList<>();

        // thiết lập webSocketService
        webSocketService = new GameWebSocketService(this);

        player = getIntent().getSerializableExtra("player", Player.class);

        gameApiService = ApiClient.getClient().create(GameApiService.class);

        setupRecyclerView();
        setupBackButton();
        // Thêm sự kiên click cho các item recyclerview
//        adapter.updateRooms(rooms);
//        roomsRV.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        roomsRV = findViewById(R.id.roomsRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        roomsRV.setLayoutManager(layoutManager);

        // Khởi tạo adapter với danh sách rỗng
        adapter = new RoomAdapter(new ArrayList<>());

        // Set adapter ngay sau khi khởi tạo
        roomsRV.setAdapter(adapter);
        adapter.setOnItemClickListener(room -> onJoinRoom(room));
    }

    // Thêm hàm onJoinRoom
    private Room onJoinRoom(RoomResponse room) {
        // Tạo request để join phòng
        Call<Room> call = gameApiService.joinRoom(room.getRoomId(), player);
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful()) {
                    Room room = response.body();
                    if (room != null) {
                        // Chuyển sang RoomActivity
                        Intent intent = new Intent(RoomsActivity.this, RoomActivity.class);
                        intent.putExtra("room", room);
                        intent.putExtra("player", player);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.e(TAG, "Error joining room", t);
            }
        });
        return null;
    }

    private void setupBackButton() {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }


    @Override
    public void onConnected() {
        // Khi kết nối thành công, đăng ký nhận tin nhắn từ các topics
        // Subscribe trước khi gửi request
        Log.d(TAG, "Subscribing to /topic/rooms");
        webSocketService.subscribe("/topic/rooms");

        Log.d(TAG, "Sending room list request");
        webSocketService.sendMessage("/app/rooms", "");

    }

    private void onLoadingRooms(String message) {
        try {
            Log.d("RoomsActivity", "Rooms: " + message);
            Type type = new TypeToken<List<RoomResponse>>() {
            }.getType();
            rooms = new Gson().fromJson(message, type);

            // Đảm bảo cập nhật UI trên main thread và log để debug
            runOnUiThread(() -> {
                adapter.updateRooms(rooms);
                Log.d("RoomsActivity", "Updated rooms size: " + rooms.size());
            });
        } catch (JsonSyntaxException e) {
            Log.e("RoomsActivity", "Error parsing rooms", e);
        }
    }

    @Override
    public void onDisconnected() {
        runOnUiThread(() -> {

            Log.d(TAG, "Disconnected from server");
            // Thử kết nối lại sau 1 giây
//            new Handler().postDelayed(() -> {
//                if (!webSocketService.isConnected()) {
//                    webSocketService.connect();
//                }
//            }, 500);
        });
    }

    @Override
    public void onError(String error) {
        // Khi có lỗi xảy ra, hiển thị thông báo cho người dùng
        runOnUiThread(() ->{
            Log.e(TAG, "WebSocket error: " + error);
        });
    }

    @Override
    public void onMessageReceived(String topic, String message) {
        // Khi nhận được tin nhắn từ server, xử lý dựa vào topic
        switch (topic) {
            case "/topic/rooms":
                onLoadingRooms(message);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketService != null) {
            webSocketService.unsubscribe("/topic/rooms");
            webSocketService.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webSocketService != null) {
            if (!webSocketService.isConnected()) {
                webSocketService.connect();
            } else {
               webSocketService.sendMessage("/app/rooms", "");
            }
        }
    }
}
