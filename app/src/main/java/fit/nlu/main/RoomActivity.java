package fit.nlu.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fit.nlu.adapter.recycleview.player.PlayerAdapter;
import fit.nlu.adapter.spiner.CustomSpinnerAdapter;
import fit.nlu.adapter.spiner.model.BaseSpinnerItem;
import fit.nlu.adapter.spiner.model.HintItem;
import fit.nlu.adapter.spiner.model.PersonItem;
import fit.nlu.adapter.spiner.model.RoundItem;
import fit.nlu.adapter.spiner.model.TimeItem;
import fit.nlu.enums.RoomState;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.GameApiService;
import fit.nlu.service.GameWebSocketService;

public class RoomActivity extends AppCompatActivity implements GameWebSocketService.WebSocketEventListener {
    private GameApiService gameApiService;
    private Player player;
    private List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        // Khởi tạo ApiClient
        Room room = getIntent().getSerializableExtra("room", Room.class);

        // Khởi dữ liệu cho Player và RecyclerView
        players = new ArrayList<>(room.getPlayers().values());

        // Trong onCreate hoặc onViewCreated
        RecyclerView rvPlayers = findViewById(R.id.rvPlayers);
        PlayerAdapter adapter = new PlayerAdapter(this);

        // Thiết lập LayoutManager (vertical list)
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvPlayers.setAdapter(adapter);
        adapter.updatePlayers(players);

        // Khởi tạo dữ liệu cho Spinner
        initializeSpinners();

        // Thêm sự kiện cho nút rời phòng
        onLeaveRoom(findViewById(R.id.btnLeaveRoom));
    }

    public void replaceFragment(Fragment fragment) {
        findViewById(R.id.roomOptions).setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void initializeSpinners() {
        List<PersonItem> persons = Arrays.asList(
                new PersonItem(2),
                new PersonItem(3),
                new PersonItem(4)
        );

        List<TimeItem> times = Arrays.asList(
                new TimeItem(60),
                new TimeItem(90),
                new TimeItem(120)
        );

        List<RoundItem> rounds = Arrays.asList(
                new RoundItem(3),
                new RoundItem(5),
                new RoundItem(7)
        );

        List<HintItem> hints = Arrays.asList(
                new HintItem(1),
                new HintItem(2),
                new HintItem(3)
        );

        setSpinnerAdapter(R.id.spinner_person, persons);
        setSpinnerAdapter(R.id.spinner_timer, times);
        setSpinnerAdapter(R.id.spinner_hint, hints);
    }

    private <T extends BaseSpinnerItem> void setSpinnerAdapter(int spinnerId, List<T> items) {
        CustomSpinnerAdapter<T> adapter = new CustomSpinnerAdapter<>(
                this,
                R.layout.item_spinner_selected,
                items
        );
        Spinner spinner = findViewById(spinnerId);
        spinner.setAdapter(adapter);
    }

    private void onLeaveRoom(ImageButton btnLeaveRoom) {
        btnLeaveRoom.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onStateChanged(RoomState state) {

    }

    @Override
    public void onPlayerJoined(Player player) {

    }

    @Override
    public void onPlayerLeft(Player player) {

    }

    @Override
    public void onGameUpdate(Room roomData) {

    }

    @Override
    public void onError(String error) {

    }
}
