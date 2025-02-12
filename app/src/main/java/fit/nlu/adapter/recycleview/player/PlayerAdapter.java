package fit.nlu.adapter.recycleview.player;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.HarmonizedColorsOptions;

import java.util.*;

import fit.nlu.main.R;
import fit.nlu.model.Player;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Player> players;           // Danh sách người chơi
    private Context context;

    // Constructor
    public PlayerAdapter(Context context) {
        this.context = context;
        players = new ArrayList<>();
    }

    // ViewHolder class đại diện cho mỗi item trong RecyclerView
    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlayerAvatar;
        TextView tvPlayerName;
        TextView tvPlayerScore;
        ImageView ivPlayerDrawing;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            ivPlayerAvatar = itemView.findViewById(R.id.ivPlayerAvatar);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerScore = itemView.findViewById(R.id.tvPlayerScore);
            ivPlayerDrawing = itemView.findViewById(R.id.ivPlayerDrawing);
        }
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view mới từ layout item
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        // Lấy dữ liệu người chơi tại vị trí position
        Player player = players.get(position);
        // Cập nhật giao diện với thông tin người chơi
        holder.tvPlayerName.setText(player.getNickname());
        holder.tvPlayerScore.setText(player.getScore() + " điểm");
        if (player.isDrawing()){
            holder.ivPlayerDrawing.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlayerDrawing.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    // Phương thức để cập nhật danh sách người chơi
    public void updatePlayers(List<Player> newPlayers) {
        this.players.clear();
        this.players.addAll(newPlayers);
        notifyDataSetChanged();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        notifyDataSetChanged();
    }
}