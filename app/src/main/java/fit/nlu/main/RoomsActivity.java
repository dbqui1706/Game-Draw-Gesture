package fit.nlu.main;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fit.nlu.adapter.recycleview.room.RoomAdapter;
import fit.nlu.adapter.recycleview.room.RoomItem;

public class RoomsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        RecyclerView recyclerView = findViewById(R.id.roomsRecyclerView);
        // Thiết lập GridLayoutManager với 3 cột và khoảng cách
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        // Thiết lập adapter
        List<RoomItem> rooms = createRoomsList();
        RoomAdapter adapter = new RoomAdapter(rooms);
        recyclerView.setAdapter(adapter);

        // Thiết lập sự kiện click vào nút back quay về lại trang Home
        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private List<RoomItem> createRoomsList() {
        List<RoomItem> rooms = new ArrayList<>();
        rooms.add(new RoomItem(1, 1, 2));
        rooms.add(new RoomItem(2, 2, 2));
        rooms.add(new RoomItem(3, 1, 4));
        return rooms;
    }
}
