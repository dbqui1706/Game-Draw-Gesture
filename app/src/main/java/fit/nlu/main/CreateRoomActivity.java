package fit.nlu.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.*;

import fit.nlu.adapter.recycleview.player.PlayerAdapter;
import fit.nlu.adapter.recycleview.player.PlayerItem;
import fit.nlu.adapter.spiner.CustomSpinnerAdapter;
import fit.nlu.adapter.spiner.model.BaseSpinnerItem;
import fit.nlu.adapter.spiner.model.HintItem;
import fit.nlu.adapter.spiner.model.PersonItem;
import fit.nlu.adapter.spiner.model.TimeItem;

public class CreateRoomActivity extends AppCompatActivity {
    private List<PlayerItem> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        // Khởi dữ liệu cho Player và RecyclerView
        dummyPlayerData();
        // Trong onCreate hoặc onViewCreated
        RecyclerView rvPlayers = findViewById(R.id.rvPlayers);
        PlayerAdapter adapter = new PlayerAdapter(this);

        // Thiết lập LayoutManager (vertical list)
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvPlayers.setAdapter(adapter);

        List<PlayerItem> players = new ArrayList<>();
        players.add(new PlayerItem("Quí", 0, null));
        players.add(new PlayerItem("Quí", 0, null));
        adapter.updatePlayers(players);

        // Khởi tạo dữ liệu cho Spinner
        initializeSpinners();

        // Thêm sự kiện cho nút rời phòng
        onLeaveRoom(findViewById(R.id.btnLeaveRoom));

    }

    private void dummyPlayerData() {
        players = new ArrayList<>();
        players.add(new PlayerItem("Qui", 0, null));
        players.add(new PlayerItem("Nam", 0, null));
        players.add(new PlayerItem("Quan", 0, null));
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

        // Thêm sự kiện chọn item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                T item = items.get(i);
                String spinner;
                if (spinnerId == R.id.spinner_person) {
                    spinner = "Người chơi";
                } else if (spinnerId == R.id.spinner_timer) {
                    spinner = "Thời gian";
                } else if (spinnerId == R.id.spinner_hint) {
                    spinner = "Số gợi ý";
                } else {
                    Toast.makeText(CreateRoomActivity.this, "Unknown spinner", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CreateRoomActivity.this, spinner + ": " + item.getDisplayText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void onLeaveRoom(ImageButton btnLeaveRoom) {
        btnLeaveRoom.setOnClickListener(v -> onBackPressed());
    }
}
