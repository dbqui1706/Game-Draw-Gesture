package fit.nlu.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fit.nlu.adapter.recycleview.room.RoomAdapter;
import fit.nlu.dto.RoomResponse;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.ApiClient;
import fit.nlu.service.GameApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RoomsActivity extends AppCompatActivity {
    private Player player;
    private GameApiService gameApiService;
    private List<RoomResponse> rooms;
    private RoomAdapter adapter; // Thêm adapter là biến instance
    private RecyclerView roomsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        player = getIntent().getSerializableExtra("player", Player.class);
        gameApiService = ApiClient.getClient().create(GameApiService.class);

        setupRecyclerView();
        setupBackButton();
        loadRooms();
        // Thêm sự kiên click cho các item recyclerview
        adapter.setOnItemClickListener(room -> {
            // Call API join room
            gameApiService.joinRoom(room.getRoomId(), player).enqueue(new Callback<Room>() {
                @Override
                public void onResponse(Call<Room> call, Response<Room> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("API_SUCCESS", "Join room: " + response.body());
                    }
                }

                @Override
                public void onFailure(Call<Room> call, Throwable t) {
                }
            });

        });
        roomsRV.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        roomsRV = findViewById(R.id.roomsRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        roomsRV.setLayoutManager(layoutManager);

        // Khởi tạo adapter với danh sách rỗng
        adapter = new RoomAdapter(new ArrayList<>());
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadRooms() {
        Call<List<RoomResponse>> call = gameApiService.getRooms();
        call.enqueue(new Callback<List<RoomResponse>>() {
            @Override
            public void onResponse(Call<List<RoomResponse>> call, Response<List<RoomResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_SUCCESS", "Rooms: " + response.body().size());
                    rooms = response.body();
                    updateRoomsList();
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RoomResponse>> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                // Hiển thị thông báo lỗi cho người dùng
                runOnUiThread(() -> Toast.makeText(RoomsActivity.this,
                        "Error loading rooms: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateRoomsList() {
        if (adapter != null) {
            adapter.updateRooms(rooms);
        }
    }
}
