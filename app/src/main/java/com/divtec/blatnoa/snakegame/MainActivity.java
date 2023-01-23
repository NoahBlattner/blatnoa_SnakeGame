package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button startPhysicsActivityButton;
    private Button startSnakeActivityButton;
    private Button startLeaderboardActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get views
        startPhysicsActivityButton = findViewById(R.id.bt_physics_activity);
        startSnakeActivityButton = findViewById(R.id.bt_game_activity);
        startLeaderboardActivityButton = findViewById(R.id.bt_leaderboard_activity);

        // Set the button click listeners
        startPhysicsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhysicsActivity.class);
                startActivity(intent);
            }
        });

        startSnakeActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SnakeActivity.class);
                startActivity(intent);
            }
        });

        startLeaderboardActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

    }
}