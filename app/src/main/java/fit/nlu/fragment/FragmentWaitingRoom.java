package fit.nlu.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import fit.nlu.adapter.spiner.CustomSpinnerAdapter;
import fit.nlu.adapter.spiner.model.BaseSpinnerItem;
import fit.nlu.adapter.spiner.model.HintItem;
import fit.nlu.adapter.spiner.model.PersonItem;
import fit.nlu.adapter.spiner.model.RoundItem;
import fit.nlu.adapter.spiner.model.TimeItem;
import fit.nlu.main.R;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.model.RoomSetting;
import fit.nlu.service.GameWebSocketService;

public class FragmentWaitingRoom extends Fragment {
    private GameWebSocketService webSocketService;
    private Room currentRoom;
    private Player currentPlayer;
    private Button btnStart;
    private Spinner spinnerPerson, spinnerTimer, spinnerRound, spinnerHint;
    private final Map<Class<? extends BaseSpinnerItem>, Consumer<BaseSpinnerItem>> optionUpdaters = new HashMap<>();

    public static FragmentWaitingRoom newInstance(GameWebSocketService webSocketService, Room room, Player player) {
        FragmentWaitingRoom fragment = new FragmentWaitingRoom();
        Bundle args = new Bundle();
        args.putSerializable("room", room);
        args.putSerializable("player", player);
        args.putSerializable("webSocketService", webSocketService);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            webSocketService = getArguments().getSerializable("webSocketService", GameWebSocketService.class);
            currentRoom = getArguments().getSerializable("room", Room.class);
            currentPlayer = getArguments().getSerializable("player", Player.class);
        }
        initializeOptionUpdaters();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FragmentWaitingRoom", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_options_room, container, false);

        initializeSpinners(view);
        setupStartButton(view);

        return view;
    }

    private void initializeOptionUpdaters() {
        optionUpdaters.put(PersonItem.class, item -> currentRoom.getSetting().setMaxPlayer(((PersonItem) item).getNumber()));
        optionUpdaters.put(TimeItem.class, item -> currentRoom.getSetting().setDrawingTime(((TimeItem) item).getSeconds()));
        optionUpdaters.put(RoundItem.class, item -> currentRoom.getSetting().setTotalRound(((RoundItem) item).getRound()));
        optionUpdaters.put(HintItem.class, item -> currentRoom.getSetting().setHintCount(((HintItem) item).getCount()));
    }

    private void initializeSpinners(View view) {
        setSpinnerAdapter(view, R.id.spinner_person, Arrays.asList(new PersonItem(2), new PersonItem(3), new PersonItem(4)));
        setSpinnerAdapter(view, R.id.spinner_timer, Arrays.asList(new TimeItem(60), new TimeItem(90), new TimeItem(120)));
        setSpinnerAdapter(view, R.id.spinner_round, Arrays.asList(new RoundItem(3), new RoundItem(5), new RoundItem(7)));
        setSpinnerAdapter(view, R.id.spinner_hint, Arrays.asList(new HintItem(1), new HintItem(2), new HintItem(3)));

        updateSetting(currentRoom.getSetting());
    }

    private <T extends BaseSpinnerItem> void setSpinnerAdapter(View view, int spinnerId, List<T> items) {
        CustomSpinnerAdapter<T> adapter = new CustomSpinnerAdapter<>(requireContext(), R.layout.item_spinner_selected, items);
        Spinner spinner = view.findViewById(spinnerId);
        spinner.setAdapter(adapter);
        spinnerEventListeners(spinner);

        if (spinnerId == R.id.spinner_person) spinnerPerson = spinner;
        if (spinnerId == R.id.spinner_timer) spinnerTimer = spinner;
        if (spinnerId == R.id.spinner_round) spinnerRound = spinner;
        if (spinnerId == R.id.spinner_hint) spinnerHint = spinner;
    }

    private void spinnerEventListeners(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BaseSpinnerItem item = (BaseSpinnerItem) parent.getItemAtPosition(position);
                sendOptionUpdate(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void sendOptionUpdate(BaseSpinnerItem item) {
        Consumer<BaseSpinnerItem> updater = optionUpdaters.get(item.getClass());
        if (updater != null) {
            updater.accept(item);
            String destination = "/app/room/" + currentRoom.getId() + "/options";
            String message = new Gson().toJson(currentRoom.getSetting());
            webSocketService.sendMessage(destination, message);
        }
    }

    public void updateSetting(RoomSetting setting) {
        requireActivity().runOnUiThread(() -> {
            setSpinnerValue(spinnerPerson, setting.getMaxPlayer());
            setSpinnerValue(spinnerTimer, setting.getDrawingTime());
            setSpinnerValue(spinnerRound, setting.getTotalRound());
            setSpinnerValue(spinnerHint, setting.getHintCount());
        });
    }

    private void setSpinnerValue(Spinner spinner, int value) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            BaseSpinnerItem item = (BaseSpinnerItem) spinner.getAdapter().getItem(i);
            if (item.isMatchingValue(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupStartButton(View view) {
        btnStart = view.findViewById(R.id.btn_start); // Khởi tạo button trước

        if (!currentPlayer.isOwner()) {
            Log.d("FragmentWaitingRoom", "Disabling controls for non-owner");
            List<Integer> spinnerIds = Arrays.asList(R.id.spinner_person, R.id.spinner_timer,
                    R.id.spinner_round, R.id.spinner_hint);
            for (int id : spinnerIds) {
                view.findViewById(id).setEnabled(false);
            }
            btnStart.setEnabled(false);
            int color = getResources().getColor(R.color.gray_background, null);
            btnStart.setBackgroundColor(color);
            return;
        }

        Log.d("FragmentWaitingRoom", "Setting up controls for owner");
        Log.d("FragmentWaitingRoom", currentPlayer.getNickname() + " | is owner: " + currentPlayer.isOwner());
        // Set click listener cho button
        btnStart.setOnClickListener(v -> {
            Log.d("FragmentWaitingRoom", "Start button clicked");
            String destination = "/app/room/" + currentRoom.getId() + "/start";
            webSocketService.sendMessage(destination, "");
        });
    }


}
