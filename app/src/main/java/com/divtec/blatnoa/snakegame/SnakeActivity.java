package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.divtec.blatnoa.snakegame.Snake.Snake;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Controllers.ScoreManager;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.Score;
import com.divtec.blatnoa.snakegame.Tick.TickManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SnakeActivity extends AppCompatActivity {

    private final int CELL_COLUMN_COUNT = 15;

    private ConstraintLayout lyt;
    private MaterialButton saveScoreButton;
    private Button restartButton;
    private Button quitButton;
    private GridLayout grid;
    private TextView currentScoreText;
    private TextInputEditText playerNameText;

    private Snake snake;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener acceleroListener;

    private boolean hasStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        // Lock screen orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get components
        lyt = findViewById(R.id.layout);
        grid = findViewById(R.id.cellGrid);
        saveScoreButton = findViewById(R.id.bt_save_score);
        restartButton = findViewById(R.id.bt_restart);
        currentScoreText = findViewById(R.id.txt_current_score);
        playerNameText = findViewById(R.id.txt_player_name);
        quitButton = findViewById(R.id.bt_quit);

        // Set text to 0
        currentScoreText.setText("0");

        // Set up button listeners
        saveScoreButton.setOnClickListener(v -> {
            // If the player name is not too short
            if (playerNameText.getText().length() < 3) {
                playerNameText.setError(getString(R.string.name_too_short));
                playerNameText.requestFocus();
            } else {
                // Save to sqlite
                saveScore(playerNameText.getText().toString(), Integer.parseInt(currentScoreText.getText().toString()));
                // Disable the button and set text to "Saved"
                saveScoreButton.setEnabled(false);
                saveScoreButton.setAlpha(.5f);
                saveScoreButton.setText(R.string.score_saved);

                // Lock the text field
                playerNameText.setEnabled(false);
            }
        });

        restartButton.setOnClickListener(view -> {
            // Restart activity
            recreate();
        });

        quitButton.setOnClickListener(view -> {
            // Quit activity
            finish();
        });

        // Get sensor manager and from it the rotation sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Create a listener for the rotation sensor
        acceleroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(android.hardware.SensorEvent event) {
                // If the game has not started
                if (!hasStarted) {
                    // Ignore the event
                    return;
                }

                // Get highest reading above 0.5
                float x = event.values[0];
                float y = event.values[1];

                float absX = Math.abs(x);
                float absY = Math.abs(y);
                float maxAbs = Math.max(absX, absY);

                // If the highest reading is below 2.5, ignore
                if (maxAbs < 2.5) {
                    return;
                }

                if (maxAbs == absX) { // If the highest reading is on the X axis
                    if (x > 0) { // If the reading is positive
                        snake.turn(Snake.Direction.DOWN);
                    } else { // If the reading is negative
                        snake.turn(Snake.Direction.UP);
                    }
                } else { // If the highest reading is on the Y axis
                    if (y > 0) { // If the reading is positive
                        snake.turn(Snake.Direction.RIGHT);
                    } else { // If the reading is negative
                        snake.turn(Snake.Direction.LEFT);
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Generate the grid when layout is ready
        lyt.getViewTreeObserver().addOnGlobalLayoutListener(this::prepareLayout);
    }

    /**
     * Save the score to the database
     * @param playerName The name of the player
     * @param score The score of the player
     */
    private void saveScore(String playerName, int score) {
        ScoreManager scoreManager = new ScoreManager(this);

        // TODO Add toasts to indicate success or failure
        scoreManager.addScore(playerName, score);
        System.out.println(scoreManager.getScores());
    }

    /**
     * Generates the grid according to the size of the layout and the requested cell size
     */
    private void prepareLayout() {
        if (grid.getChildCount() == 0) {

            // Get layout size
            int width = grid.getWidth();
            int height = grid.getHeight();

            // Get cell size
            int cellSize = width / CELL_COLUMN_COUNT;

            // Set grid size
            grid.setColumnCount(CELL_COLUMN_COUNT);
            grid.setRowCount(height / cellSize);

            // Center the grid with padding in layout
            int xPadding = width % cellSize / 2;
            int yPadding = height % cellSize / 2;
            lyt.setPadding(xPadding, yPadding, xPadding, yPadding);

            // Generate cells
            boolean cellColor1 = true;
            boolean switchOnNewRow = grid.getColumnCount() % 2 == 0;
            for (int i = 0; i < grid.getRowCount(); i++) {
                for (int j = 0; j < grid.getColumnCount(); j++) {
                    ImageView cell = new ImageView(this);
                    cell.setAdjustViewBounds(true);
                    cell.setMinimumWidth(cellSize);
                    cell.setMinimumHeight(cellSize);
                    cell.setMaxWidth(cellSize);
                    cell.setMaxHeight(cellSize);

                    // Alternate background between cells
                    cell.setBackgroundColor(getColor(cellColor1 ? R.color.cell1 : R.color.cell2));

                    // Add the cell to the grid
                    grid.addView(cell);

                    cellColor1 = !cellColor1;
                }
                // If the grid has an uneven number of columns
                if (switchOnNewRow) {
                    // Alternate the first cell type on each row
                    cellColor1 = !cellColor1;
                }
            }

            // Create the snake
            snake = new Snake(grid);
            startGame();
        }
    }

    /**
     * Starts the game
     */
    private void startGame() {
        // Register the listener
        sensorManager.registerListener(acceleroListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        hasStarted = true;
        TickManager.getTickManager().start();
    }

    /**
     * End snake game
     */
    public void gameOver() {
        // Update final score
        TextView finalScoreText = findViewById(R.id.txt_final_score);

        runOnUiThread(() -> {
            finalScoreText.setText(currentScoreText.getText());
        });

        // Move grid to the background
        grid.setTranslationZ(-5);
        // Remove cell backgrounds
        for (int i = 0; i < grid.getChildCount(); i++) {
            ImageView cell = (ImageView) grid.getChildAt(i);
            cell.setBackgroundColor(0);
        }

        // Stop the tick manager
        TickManager.getTickManager().stop();
    }

    /**
     * Updates the score
     * @param score The new score
     */
    public void updateScore(int score) {
        runOnUiThread(() -> {
            currentScoreText.setText(Integer.toString(score));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener
        sensorManager.unregisterListener(acceleroListener);
        TickManager.getTickManager().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener
        sensorManager.registerListener(acceleroListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        TickManager.getTickManager().resume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the listener
        sensorManager.unregisterListener(acceleroListener);
        TickManager.getTickManager().stop();
    }
}