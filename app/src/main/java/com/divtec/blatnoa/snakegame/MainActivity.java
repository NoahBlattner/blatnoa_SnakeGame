package com.divtec.blatnoa.snakegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.divtec.blatnoa.snakegame.Physics.Collider;
import com.divtec.blatnoa.snakegame.Physics.PhysicsMarble;
import com.divtec.blatnoa.snakegame.Tick.TickManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int SENSOR_AVERAGE_VALUES = 2;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener acceleroListener;

    private ArrayList<Float> xReadings = new ArrayList<>();
    private ArrayList<Float> yReadings = new ArrayList<>();

    private ConstraintLayout lyt;
    private ImageView mainMarble;
    private ImageView square;
    private Button addButton;

    private TickManager tickManager;
    private PhysicsMarble mainPhysics;

    private ArrayList<PhysicsMarble> additionalPhysicMarbles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lock screen orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get components
        lyt = findViewById(R.id.layout);
        mainMarble = findViewById(R.id.marble);
        square = findViewById(R.id.centerSquare);
        addButton = findViewById(R.id.addButton);

        // Create tick manager
        tickManager = TickManager.getTickManager();

        // Create the physics of the main marble
        mainPhysics = new PhysicsMarble(this, mainMarble, false);

        // Create the collision of the center square
        new Collider(square);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMarble();
            }
        });

        // Get sensor manager and from it the rotation sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Create a listener for the rotation sensor
        acceleroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(android.hardware.SensorEvent event) {
                // Add reading to readings list
                addSensorReading(event);

                // Get average values from 5 last readings to reduces jittering
                double xReadingAvg = getArrayAverage(xReadings);
                double yReadingAvg = getArrayAverage(yReadings);

                // Set the values in the static var of the physics marble
                PhysicsMarble.updateAccelerationValues(xReadingAvg, yReadingAvg);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Register the listener
        sensorManager.registerListener(acceleroListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        tickManager.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tickManager.stop();
    }

    /**
     * Add a new sensor reading value to both the x and y readings
     * @param event The sensor event containing the readings
     */
    private void addSensorReading(SensorEvent event) {
        xReadings.add(event.values[0]);
        yReadings.add(-event.values[1]);
        if (xReadings.size() > SENSOR_AVERAGE_VALUES) {
            xReadings.remove(0);
        }
        if (yReadings.size() > SENSOR_AVERAGE_VALUES) {
            yReadings.remove(0);
        }
    }

    /**
     * Add a new marble to the activity
     */
    private void addNewMarble() {
        if (TickManager.getTickManager().canAddTickObject()) {
            ImageView newView = createNewMarbleView();

            lyt.addView(newView);

            PhysicsMarble newMarble = new PhysicsMarble(this, newView);
            additionalPhysicMarbles.add(newMarble);
        } else {
            Toast.makeText(this, "Cannot add more marbles", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO Create a custom component
    /**
     * Creates and sets up a new marble view
     * @return The new marble view
     */
    @NonNull
    private ImageView createNewMarbleView() {
        ImageView newView = new ImageView(this);
        ConstraintLayout.LayoutParams newParams = new ConstraintLayout.LayoutParams(20, 20);

        // Set up constraints
        int id = lyt.getId();
        newParams.topToTop = id;
        newParams.bottomToBottom = id;
        newParams.startToStart = id;
        newParams.endToEnd = id;

        newView.setLayoutParams(newParams);
        newView.setImageResource(R.drawable.marble);
        return newView;
    }

    /**
     * Get the average of an array
     * @param array The array to get the average of
     * @return The average of the array
     */
    float getArrayAverage(ArrayList<Float> array) {
        if (array.size() == 0) {
            return 0;
        }

        float sum = 0;
        for (float current : array) {
            sum += current;
        }

        return sum / array.size();
    }
}