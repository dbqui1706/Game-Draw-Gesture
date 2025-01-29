package fit.nlu.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import fit.nlu.model.Player;

public class MainActivity extends AppCompatActivity {

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

        // Todo: Tìm nút theo phòng theo ID
        MaterialButton btnRooms = findViewById(R.id.btnRoom);

        String nickname = ((EditText) findViewById(R.id.edtNickname)).getText().toString();
        String avatar = ((ImageView) findViewById(R.id.avatar_player)).toString();

        // Thiết lập sự kiện click cho nút Phòng
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
            Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
            intent.putExtra("player", player);
            startActivity(intent);
        });
    }
}