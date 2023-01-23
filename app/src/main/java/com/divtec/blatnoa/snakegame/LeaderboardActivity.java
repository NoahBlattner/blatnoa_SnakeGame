package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.divtec.blatnoa.snakegame.Leaderboard.RankingAdapter;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Controllers.RankingManager;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.Ranking;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    private final int MAX_RANKINGS = 10;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.list_leaderboard);

        RankingManager rankingManager = new RankingManager(this);
        ArrayList<Ranking> rankingList = rankingManager.getRankings(MAX_RANKINGS);
        recyclerView.setAdapter(new RankingAdapter(rankingList));
    }
}