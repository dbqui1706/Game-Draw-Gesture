package fit.nlu.main.controller;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fit.nlu.adapter.recycleview.message.MessageAdapter;
import fit.nlu.adapter.recycleview.player.PlayerAdapter;
import fit.nlu.enums.RoomState;
import fit.nlu.model.Message;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import fit.nlu.model.Turn;
import fit.nlu.utils.CountdownManager;
import fit.nlu.utils.Util;

/**
 * Controller chuyên trách cập nhật giao diện (UI) của RoomActivity.
 * Các phương thức trong lớp này chỉ tập trung cập nhật các thành phần UI.
 */
public class RoomUIController {
    private static final String TAG = "RoomUIController";

    // UI Components
    private final RecyclerView rvPlayers;
    private final RecyclerView rvChat;
    private final TextView tvTimer;
    private final TextView tvWord;
    private final TextView tvWaiting;

    // Adapters
    private final PlayerAdapter playerAdapter;
    private final MessageAdapter messageAdapter;

    // Utils
    private CountdownManager countdownManager;

    public RoomUIController(RecyclerView rvPlayers, RecyclerView rvChat, TextView tvTimer,
                            TextView tvWord, TextView tvWaiting, PlayerAdapter playerAdapter,
                            MessageAdapter messageAdapter) {
        this.rvPlayers = rvPlayers;
        this.rvChat = rvChat;
        this.tvTimer = tvTimer;
        this.tvWord = tvWord;
        this.tvWaiting = tvWaiting;
        this.playerAdapter = playerAdapter;
        this.messageAdapter = messageAdapter;

        initRecyclerViews();
    }

    private void initRecyclerViews() {
        rvPlayers.setLayoutManager(new LinearLayoutManager(rvPlayers.getContext()));
        rvPlayers.setAdapter(playerAdapter);

        rvChat.setLayoutManager(new LinearLayoutManager(rvChat.getContext()));
        rvChat.setAdapter(messageAdapter);
    }

    /**
     * Cập nhật danh sách người chơi.
     */
    public void updatePlayerList(List<Player> players) {
        playerAdapter.updatePlayers(players);
    }

    /**
     * Thêm tin nhắn chat vào danh sách.
     */
    public void addChatMessage(Message message) {
        messageAdapter.addMessage(message);
    }

    /**
     * Cập nhật header của phòng dựa theo trạng thái và turn hiện tại.
     * Nếu state là PLAYING, hiển thị từ cần vẽ hoặc enDash tương ứng.
     */
    @SuppressLint("SetTextI18n")
    public void updateHeader(Room room, Player currentPlayer) {
        if (room.getState() == RoomState.PLAYING) {
            Turn currentTurn = room.getGameSession().getCurrentRound().getCurrentTurn();
            if (currentTurn == null) return;

            tvWaiting.setVisibility(TextView.GONE);
            tvWord.setVisibility(TextView.VISIBLE);

            // Nếu currentPlayer là người vẽ, hiển thị từ gốc, ngược lại hiển thị dạng enDash
            if (currentPlayer.getId().equals(currentTurn.getDrawer().getId())) {
                tvWord.setText("Vẽ từ: " + currentTurn.getKeyword());
            } else {
                tvWord.setText("Đoán từ: " + Util.maskWord(currentTurn.getKeyword()));
            }

            // Cập nhật countdown dựa theo thời gian còn lại
            startCountdown(currentTurn.getRemainingTime());
        } else if (room.getState() == RoomState.WAITING) {
            stopCountdown();
            tvTimer.setText("60");
            tvWord.setVisibility(TextView.GONE);
            tvWaiting.setVisibility(TextView.VISIBLE);
        }
    }

    /**
     * Cập nhật header của phòng dựa theo trạng thái và turn hiện tại.
     * Nếu state là PLAYING, hiển thị từ cần vẽ hoặc enDash tương ứng.
     */
    public void updateTurnHeader(String eventType, int remainingTime, String keyword, Player drawer, Player currentPlayer) {
        switch (eventType) {
            case "START_TURN":
                tvWaiting.setVisibility(TextView.GONE);
                tvWord.setVisibility(TextView.VISIBLE);

                // Nếu currentPlayer là người vẽ, hiển thị từ gốc, ngược lại hiển thị dạng enDash
                if (currentPlayer.getId().equals(drawer.getId())) {
                    tvWord.setText("Vẽ từ: " + keyword);
                } else {
                    tvWord.setText("Đoán từ: " + Util.maskWord(keyword));
                }

                // Cập nhật countdown dựa theo thời gian còn lại
                startCountdown(remainingTime);
                break;
            case "END_TURN":
                stopCountdown();
//                tvTimer.setText("60");
//                tvWord.setVisibility(TextView.GONE);
//                tvWaiting.setVisibility(TextView.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * Cập nhật trạng thái “vẽ” cho danh sách người chơi, dựa theo ID của người vẽ.
     */
    public void updatePlayerDrawing(List<Player> players, String drawerId) {
        List<Player> updatedPlayers = new ArrayList<>(players);
        updatedPlayers = updatedPlayers.stream()
                .peek(player -> player.setDrawing(player.getId().equals(drawerId)))
                .collect(Collectors.toList());
        updatePlayerList(updatedPlayers);
    }

    /**
     * Khởi tạo và bắt đầu đếm ngược.
     */
    public void startCountdown(int seconds) {
        stopCountdown();
        countdownManager = new CountdownManager(tvTimer);
        countdownManager.startCountdown(seconds);
    }

    /**
     * Dừng đếm ngược.
     */
    public void stopCountdown() {
        if (countdownManager != null) {
            countdownManager.stopCountdown();
        }
    }

    /**
     * Dọn dẹp bộ đếm ngược.
     */
    public void cleanupCountdown() {
        if (countdownManager != null) {
            countdownManager.cleanup();
        }
    }


}
