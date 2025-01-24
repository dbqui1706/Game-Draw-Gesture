package fit.nlu.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fit.nlu.adapter.recycleview.player.PlayerAdapter;
import fit.nlu.adapter.recycleview.player.PlayerItem;
import fit.nlu.adapter.spiner.CustomSpinnerAdapter;
import fit.nlu.adapter.spiner.model.BaseSpinnerItem;
import fit.nlu.adapter.spiner.model.HintItem;
import fit.nlu.adapter.spiner.model.PersonItem;
import fit.nlu.adapter.spiner.model.TimeItem;
import fit.nlu.fagment.FragmentChooseWord;
import fit.nlu.fagment.FragmentDraw;
import fit.nlu.fagment.FragmentInfoWaiting;

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
        adapter.updatePlayers(players);

        // Khởi tạo dữ liệu cho Spinner
        initializeSpinners();

        // Thêm sự kiện cho nút rời phòng
        onLeaveRoom(findViewById(R.id.btnLeaveRoom));

        // Xử lý khi nhấn nút "BẮT ĐẦU"
        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> handleStartGame());

    }

    private void handleStartGame() {
        // Kiểm tra vai trò người chơi
        if (true) {
            // Người chơi hiện tại là người chọn từ
            replaceFragment(new FragmentChooseWord());
//            replaceFragment(new FragmentInfoWaiting("Player A")); // Thay "Player A" bằng tên người chọn

        } else {
            // Người chơi khác phải đợi
            replaceFragment(new FragmentInfoWaiting("Player A")); // Thay "Player A" bằng tên người chọn
        }
    }

    public void replaceFragment(Fragment fragment) {
        findViewById(R.id.roomOptions).setVisibility(View.GONE);
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void dummyPlayerData() {
        players = new ArrayList<>();
        players.add(new PlayerItem("Qui", 0, null));
        players.add(new PlayerItem("Quan", 0, null));
        players.add(new PlayerItem("Nam", 0, null));
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
    }

    private void onLeaveRoom(ImageButton btnLeaveRoom) {
        btnLeaveRoom.setOnClickListener(v -> onBackPressed());
    }
}
