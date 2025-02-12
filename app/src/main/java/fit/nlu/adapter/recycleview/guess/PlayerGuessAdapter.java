package fit.nlu.adapter.recycleview.guess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fit.nlu.main.R;
import fit.nlu.model.Guess;

public class PlayerGuessAdapter extends RecyclerView.Adapter<PlayerGuessAdapter.PlayerGuessViewHolder> {
    private Set<Guess> guesses;

    public PlayerGuessAdapter() {
        guesses = new HashSet<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setGuesses(Set<Guess> players) {
        this.guesses = players;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerGuessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_guess, parent, false);
        return new PlayerGuessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerGuessViewHolder holder, int position) {
        Guess guess = new ArrayList<>(guesses).get(position);
        holder.tvPlayerName.setText(guess.getPlayer().getNickname());
        holder.tvPlayerScore.setText("+" + guess.getScore());
    }

    @Override
    public int getItemCount() {
        return guesses.size();
    }

    public static class PlayerGuessViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayerName, tvPlayerScore;

        public PlayerGuessViewHolder(View itemView) {
            super(itemView);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerScore = itemView.findViewById(R.id.tvPlayerScore);
        }
    }
}
