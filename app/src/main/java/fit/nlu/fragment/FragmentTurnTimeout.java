package fit.nlu.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fit.nlu.adapter.recycleview.guess.PlayerGuessAdapter;
import fit.nlu.main.R;
import fit.nlu.model.Room;
import fit.nlu.model.Turn;
import fit.nlu.service.GameWebSocketService;

public class FragmentTurnTimeout extends Fragment {
    private static final String TAG = "FragmentDraw";
    private final GameWebSocketService webSocketService;
    private final Turn currentTurn;
    private final Room currentRoom;
    private final PlayerGuessAdapter playerGuessAdapter;
    private RecyclerView rvPlayerGuess;


    public FragmentTurnTimeout(GameWebSocketService webSocketService, Room room, Turn turn) {
        this.webSocketService = webSocketService;
        this.currentRoom = room;
        this.currentTurn = turn;
        this.playerGuessAdapter = new PlayerGuessAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_turn_end, container, false);
        // Khởi tạo các view
        TextView tvWord = view.findViewById(R.id.tvWord);
        tvWord.setText(currentTurn.getKeyword());

        this.rvPlayerGuess = view.findViewById(R.id.rvGuessList);
        this.rvPlayerGuess.setLayoutManager(new LinearLayoutManager(getContext()));
        this.playerGuessAdapter.setGuesses(currentTurn.getGuesses());
        rvPlayerGuess.setAdapter(playerGuessAdapter);

        // Áp dụng animation cho root view của fragment
        view.setTranslationY(-container.getHeight()); // Đặt vị trí ban đầu ở trên màn hình
        view.animate()
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        return view;
    }
}
