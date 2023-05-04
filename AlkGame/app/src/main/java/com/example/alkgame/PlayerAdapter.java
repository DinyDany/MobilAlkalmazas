package com.example.alkgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private List<Player> players;
    Context context;

    public PlayerAdapter(List<Player> players, Context context) {
        this.players = players;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.player_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTextView.setText(players.get(position).getUserName());
        holder.scoreTextView.setText(String.valueOf(players.get(position).getScore()));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView scoreTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }
    }
}
