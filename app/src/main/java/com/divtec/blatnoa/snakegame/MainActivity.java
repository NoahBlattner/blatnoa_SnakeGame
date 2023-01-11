package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button startPhysicsActivityButton;
    Button startSnakeActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        startPhysicsActivityButton = findViewById(R.id.physicsActivityButton);
        startSnakeActivityButton = findViewById(R.id.gameActivityButton);

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
    }
}