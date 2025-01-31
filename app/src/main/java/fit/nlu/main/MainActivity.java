package fit.nlu.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.ApiClient;
import fit.nlu.service.GameApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private GameApiService gameApiService;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameApiService = ApiClient.getClient().create(GameApiService.class);

        // Todo: Tìm nút theo phòng theo ID
        MaterialButton btnRooms = findViewById(R.id.btnRoom);

        String nickname = ((EditText) findViewById(R.id.edtNickname)).getText().toString();
        String avatar = ((ImageView) findViewById(R.id.avatar_player)).toString();

        // Thiết lập sự kiện click cho nút list Phòng
        btnRooms.setOnClickListener(v -> {
            Player player = new Player(nickname, avatar, false);
            // Tạo Intent để chuyển đối tượng Player sang RoomsActivity
            Intent intent = new Intent(MainActivity.this, RoomsActivity.class);
            intent.putExtra("player", player);
            startActivity(intent);
        });

        // Todo: Navigate to CreateRoomActivity
        MaterialButton btnCreateRoom = findViewById(R.id.btnNewRoom);
        btnCreateRoom.setOnClickListener(v -> {
            Player player = new Player(nickname, avatar, true);

            // Call API đến Server để tạo phòng
            Room room = createRoom(player);
        });
    }


    private Room createRoom(Player player) {
        final Room[] room = new Room[1];
        gameApiService.createRoom(player).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    room[0] = response.body();
                    Log.d("API_SUCCESS", "Room created: " + room[0]);
                    // Todo: Navigate to CreateRoomActivity
                    Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                    intent.putExtra("room", room[0]);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Failed to create room: " + t.getMessage());
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.show();
                Log.e("API_ERROR", "Failed to create room: " + t.getMessage());
            }
        });
        return room[0];
    }
}