package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.Snake.Snake;

public class SnakeActivity extends AppCompatActivity {

    private final int CELL_COLUMN_COUNT = 15;

    private ConstraintLayout lyt;
    private GridLayout grid;
    private Snake snake;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener acceleroListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);

        // Lock screen orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get components
        lyt = findViewById(R.id.layout);
        grid = findViewById(R.id.cellGrid);

        // Get sensor manager and from it the rotation sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Create a listener for the rotation sensor
        acceleroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(android.hardware.SensorEvent event) {
                // Get highest reading above 0.5
                float x = event.values[0];
                float y = event.values[1];

                if (Math.abs(x) > 0.5) {
                    if (x > 0) {
                        snake.turn(Snake.Direction.DOWN);
                    } else {
                        snake.turn(Snake.Direction.UP);
                    }
                } else if (Math.abs(y) > 0.5) {
                    if (y > 0) {
                        snake.turn(Snake.Direction.RIGHT);
                    } else {
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
     * Generates the grid according to the size of the layout and the requested cell size
     */
    private void prepareLayout() {
        if (grid.getChildCount() == 0) {

            // Get layout size
            int width = lyt.getWidth();
            int height = lyt.getHeight();

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
            boolean cellType1 = true;
            boolean switchOnNewRow = grid.getColumnCount() % 2 == 0;
            for (int i = 0; i < grid.getRowCount(); i++) {
                for (int j = 0; j < grid.getColumnCount(); j++) {
                    ImageView cell = new ImageView(this);
                    cell.setMinimumWidth(cellSize);
                    cell.setMinimumHeight(cellSize);
                    cell.setMaxWidth(cellSize);
                    cell.setMaxHeight(cellSize);

                    // Alternate background between cells
                    cell.setBackground(AppCompatResources.getDrawable(this,
                            cellType1 ? R.drawable.cell_1 : R.drawable.cell_2));

                    // Add the cell to the grid
                    grid.addView(cell);

                    cellType1 = !cellType1;
                }
                // If the grid has an uneven number of columns
                if (switchOnNewRow) {
                    // Alternate the first cell type on each row
                    cellType1 = !cellType1;
                }
            }

            // Create the snake
            snake = new Snake(grid, cellSize);
            startGame();
        }
    }

    private void startGame() {
        // Register the listener
        sensorManager.registerListener(acceleroListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
}