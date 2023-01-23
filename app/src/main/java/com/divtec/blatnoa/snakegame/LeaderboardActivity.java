package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.divtec.blatnoa.snakegame.Leaderboard.RankingAdapter;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Controllers.RankingManager;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.Ranking;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    private final int MAX_RANKINGS = 10;

    private RecyclerView recyclerView;
    private TextView txtNoRankings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.list_leaderboard);
        txtNoRankings = findViewById(R.id.txt_empty_leaderboard);

        RankingManager rankingManager = new RankingManager(this);
        ArrayList<Ranking> rankingList = rankingManager.getRankings(MAX_RANKINGS);

        if (rankingList.isEmpty()) {
            txtNoRankings.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtNoRankings.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            RankingAdapter rankingAdapter = new RankingAdapter(rankingList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(rankingAdapter);
        }
    }
}