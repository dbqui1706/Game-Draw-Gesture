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
import fit.nlu.adapter.recycleview.player.PlayerItem;
import fit.nlu.adapter.spiner.CustomSpinnerAdapter;
import fit.nlu.adapter.spiner.model.BaseSpinnerItem;
import fit.nlu.adapter.spiner.model.HintItem;
import fit.nlu.adapter.spiner.model.PersonItem;
import fit.nlu.adapter.spiner.model.TimeItem;
import fit.nlu.fagment.FragmentChooseWord;
import fit.nlu.fagment.FragmentInfoWaiting;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.ApiClient;
import fit.nlu.service.GameApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateRoomActivity extends AppCompatActivity {
    private GameApiService gameApiService;
    private Player owner = getIntent().getSerializableExtra("player", Player.class);
    private List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        // Khởi tạo ApiClient
//        gameApiService = ApiClient.getClient().create(GameApiService.class);

        // Khởi dữ liệu cho Player và RecyclerView
        players = new ArrayList<>();
        players.add(owner);
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
        // Gọi API tạo phòng
        gameApiService.createRoom(owner).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CreateRoomActivity", "Room created: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                // Handle error
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateRoomActivity.this);
                builder.setMessage("Không thể tạo phòng, vui lòng thử lại sau")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
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
