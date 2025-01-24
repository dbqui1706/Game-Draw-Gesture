package fit.nlu.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

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

        // Thiết lập sự kiện click cho nút Phòng
        btnRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển sang RoomsActivity
                Intent intent = new Intent(MainActivity.this, RoomsActivity.class);
                startActivity(intent);
            }
        });

        // Todo: Navigate to CreateRoomActivity
        MaterialButton btnCreateRoom = findViewById(R.id.btnNewRoom);
        btnCreateRoom.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
            startActivity(intent);
        });
    }
}