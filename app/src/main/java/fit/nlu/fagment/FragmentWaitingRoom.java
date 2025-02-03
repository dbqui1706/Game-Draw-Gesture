package fit.nlu.fagment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

import fit.nlu.adapter.spiner.CustomSpinnerAdapter;
import fit.nlu.adapter.spiner.model.BaseSpinnerItem;
import fit.nlu.adapter.spiner.model.HintItem;
import fit.nlu.adapter.spiner.model.PersonItem;
import fit.nlu.adapter.spiner.model.RoundItem;
import fit.nlu.adapter.spiner.model.TimeItem;
import fit.nlu.main.R;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.service.GameWebSocketService;

public class FragmentWaitingRoom extends Fragment {
    private GameWebSocketService webSocket;
    private Room currentRoom;
    private Player currentPlayer;


    public static FragmentWaitingRoom newInstance(Room room, Player player) {
        FragmentWaitingRoom fragment = new FragmentWaitingRoom();
        Bundle args = new Bundle();
        args.putSerializable("room", room);
        args.putSerializable("player", player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentRoom = getArguments().getSerializable("room", Room.class);
            currentPlayer = getArguments().getSerializable("player", Player.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FragmentWaitingRoom", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_options_room, container, false);

        initializeSpinners(view);
        return view;
    }

    private void initializeSpinners(View view) {
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


        setSpinnerAdapter(view, R.id.spinner_person, persons);
        setSpinnerAdapter(view, R.id.spinner_timer, times);
        setSpinnerAdapter(view, R.id.spinner_round, rounds);
        setSpinnerAdapter(view, R.id.spinner_hint, hints);


        // Khởi tạo button start nếu là owner
        setupStartButton(view);
    }

    private void setupStartButton(View view) {

    }

    private <T extends BaseSpinnerItem> void setSpinnerAdapter(View view, int spinnerId, List<T> items) {
        Spinner spinner = view.findViewById(spinnerId);
        CustomSpinnerAdapter<T> adapter = new CustomSpinnerAdapter<>(
                requireContext(),
                R.layout.item_spinner_selected,
                items
        );

        spinner.setAdapter(adapter);
    }
}
