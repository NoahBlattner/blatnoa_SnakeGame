package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.divtec.blatnoa.snakegame.Leaderboard.RankingAdapter;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Controllers.RankingManager;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.Ranking;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    private final int MAX_RANKINGS = 20;

    private RecyclerView recyclerView;
    private TextView txtNoRankings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Get views
        recyclerView = findViewById(R.id.list_leaderboard);
        txtNoRankings = findViewById(R.id.txt_empty_leaderboard);

        // Load rankings
        initList();
    }

    /**
     * Initialize the list (or show a message if there are no rankings)
     */
    private void initList() {
        // Initialize the ranking manager
        RankingManager rankingManager = new RankingManager(this);
        ArrayList<Ranking> rankingList = rankingManager.getRankings(MAX_RANKINGS);

        if (rankingList.isEmpty()) {
            // Show the "leaderboard empty" text
            // And hide the recycler view
            txtNoRankings.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Hide the "leaderboard empty" text
            // And show the recycler view
            txtNoRankings.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Load the ranking list into the recycler view
            RankingAdapter rankingAdapter = new RankingAdapter(rankingList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(rankingAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the menu
        getMenuInflater().inflate(R.menu.leaderboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the menu item clicks
        switch (item.getItemId()) {
            case R.id.menu_action_clear_leaderboard: // Request to clear the leaderboard
                new MaterialAlertDialogBuilder(this) // Confirm the action
                        .setTitle(R.string.dialog_title_clear_leaderboard)
                        .setMessage(R.string.dialog_text_confirm)
                        .setPositiveButton(R.string.dialog_button_confirm, (dialog, which) -> {
                            initList();
                        })
                        .setNegativeButton(R.string.dialog_button_cancel, null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}