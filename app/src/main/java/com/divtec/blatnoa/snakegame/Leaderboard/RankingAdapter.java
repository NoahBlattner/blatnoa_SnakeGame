package com.divtec.blatnoa.snakegame.Leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.divtec.blatnoa.snakegame.R;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.Ranking;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingView> {

    private ArrayList<Ranking> rankingList;

    public RankingAdapter(ArrayList<Ranking> rankingList) {
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new ranking view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_ranking, parent, false);
        return new RankingView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingView holder, int position) {
        // If it's the first item, set it as a title
        if (position == 0) {
            holder.setIsTitle(true);
            return;
        }

        // Get the ranking
        // Subtract 1 because the first item is the title
        Ranking ranking = rankingList.get(position-1);

        // Set the ranking data to the view
        holder.setData(String.valueOf(position) + ".", ranking.getPlayer(), String.valueOf(ranking.getScore()), ranking.getTimeDate());
    }

    @Override
    public int getItemCount() {
        // + 1 for the header
        return rankingList.size() + 1;
    }
}
