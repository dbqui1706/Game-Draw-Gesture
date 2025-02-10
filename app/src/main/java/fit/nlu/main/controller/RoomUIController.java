package fit.nlu.main.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private Room room;

    // UI Components
    private final RecyclerView rvPlayers;
    private final RecyclerView rvChat;
    private final TextView tvTimer;
    private final TextView tvWord;
    private final TextView tvWaiting;
    private EditText etChatInput;
    private TextView btnSend;
    private View chatInputLayout;
    private boolean isKeyboardShowing = false;
    private final Activity activity;

    // Adapters
    private final PlayerAdapter playerAdapter;
    private final MessageAdapter messageAdapter;

    // Utils
    private CountdownManager countdownManager;

    public RoomUIController(Room room, RecyclerView rvPlayers, RecyclerView rvChat, TextView tvTimer,
                            TextView tvWord, TextView tvWaiting, PlayerAdapter playerAdapter,
                            MessageAdapter messageAdapter, Activity activity, EditText etChatInput,
                            TextView btnSend, View chatInputLayout) {
        this.room = room;
        this.rvPlayers = rvPlayers;
        this.rvChat = rvChat;
        this.tvTimer = tvTimer;
        this.tvWord = tvWord;
        this.tvWaiting = tvWaiting;
        this.etChatInput = etChatInput;
        this.btnSend = btnSend;
        this.chatInputLayout = chatInputLayout;

        this.playerAdapter = playerAdapter;
        this.messageAdapter = messageAdapter;
        this.activity = activity;

        initRecyclerViews();
    }

    // Dispatch touch event để ẩn bàn phím khi chạm vào bất kỳ vị trí nào khác ngoài EditText.
    public void dispatchTouchEvent(MotionEvent ev, Activity activity) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = activity.getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                // Nếu điểm chạm không nằm trong vùng hiển thị của EditText
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
    }

    private void initRecyclerViews() {
        rvPlayers.setLayoutManager(new LinearLayoutManager(rvPlayers.getContext()));
        rvPlayers.setAdapter(playerAdapter);

        LinearLayoutManager rvChatLayout = new LinearLayoutManager(rvChat.getContext());
        rvChatLayout.setStackFromEnd(true);
        rvChat.setLayoutManager(rvChatLayout);

        // Biến để theo dõi người dùng có đang đọc tin nhắn cũ không
        final boolean[] isUserScrolling = {false};

        // Theo dõi hành vi scroll của người dùng
        rvChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isUserScrolling[0] = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Kiểm tra xem người dùng có ở cuối RecyclerView không
                int lastVisibleItem = rvChatLayout.findLastVisibleItemPosition();
                if (lastVisibleItem == messageAdapter.getItemCount() - 1) {
                    isUserScrolling[0] = false; // Reset trạng thái khi user scroll tới cuối
                }
            }
        });

        // Đăng ký AdapterDataObserver để tự động cuộn xuống khi có tin nhắn mới
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                // Chỉ tự động scroll khi người dùng không đang đọc tin nhắn cũ
                if (!isUserScrolling[0]) {
                    rvChat.postDelayed(() -> {
                        rvChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    }, 100);
                }
            }
        });

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
    public void updateHeader(Turn turn, Player currentPlayer) {
        if (room.getState() == RoomState.PLAYING) {
            if (turn == null) return;

            tvWaiting.setVisibility(TextView.GONE);
            tvWord.setVisibility(TextView.VISIBLE);

            // Khởi tạo countdown manager nếu chưa có
            if (countdownManager == null) {
                countdownManager = new CountdownManager(tvTimer);
            }

            // Cập nhật từ khóa dựa trên vai trò người chơi
            if (currentPlayer.getId().equals(turn.getDrawer().getId())) {
                tvWord.setText("Vẽ từ: " + turn.getKeyword());
            } else {
                tvWord.setText("Đoán từ: " + Util.maskWord(turn.getKeyword()));
            }
        } else if (room.getState() == RoomState.WAITING || room.getState() == RoomState.GAME_END) {
            if (countdownManager != null) {
                countdownManager.cleanup();
            }
            // Reset timer và ẩn từ khóa
            tvTimer.setText("60");
            tvWord.setVisibility(TextView.GONE);
            tvWaiting.setVisibility(TextView.VISIBLE);
        }
    }

    public void updateTimeFromServer(int serverTime) {
        if (countdownManager != null) {
            countdownManager.updateTimeFromServer(serverTime);
        }
    }

    /**
     * Cập nhật trạng thái vẽ cho người chơi.
     */
    public void updateDrawingStatus(Collection<Player> players, String drawerId) {
        if (drawerId != null) {
            players.forEach(player -> player.setDrawing(player.getId().toString().equals(drawerId)));
        }
        updatePlayerList(new ArrayList<>(players));
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
