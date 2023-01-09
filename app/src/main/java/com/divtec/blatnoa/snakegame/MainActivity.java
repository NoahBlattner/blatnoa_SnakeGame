package com.divtec.blatnoa.snakegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener acceleroListener;

    private ArrayList<Float> xReadings = new ArrayList<>();
    private ArrayList<Float> yReadings = new ArrayList<>();

    private ConstraintLayout lyt;
    private ImageView mainMarble;
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
        addButton = findViewById(R.id.addButton);

        tickManager = TickManager.getTickManager();

        mainPhysics = new PhysicsMarble(this, mainMarble);

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
                
                xReadings.add(event.values[0]);
                yReadings.add(-event.values[1]);
                if (xReadings.size() > 10) {
                    xReadings.remove(0);
                }
                if (yReadings.size() > 10) {
                    yReadings.remove(0);
                }

                double xReadingAvg = getArrayAverage(xReadings);
                double yReadingAvg = getArrayAverage(yReadings);

                mainPhysics.updateAccelerationValues(xReadingAvg, yReadingAvg);

                for (PhysicsMarble current : additionalPhysicMarbles) {
                    current.updateAccelerationValues(xReadingAvg, yReadingAvg);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Register the listener
        sensorManager.registerListener(acceleroListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        tickManager.start();
    }

    private void addNewMarble() {
        ImageView newView = new ImageView(this);
        ConstraintLayout.LayoutParams newParams = new ConstraintLayout.LayoutParams(20, 20);

        int id = lyt.getId();
        newParams.topToTop = id;
        newParams.bottomToBottom = id;
        newParams.startToStart = id;
        newParams.endToEnd = id;

        newView.setLayoutParams(newParams);
        newView.setImageResource(R.drawable.marble);

        lyt.addView(newView);

        PhysicsMarble newMarbel = new PhysicsMarble(this, newView);
        additionalPhysicMarbles.add(newMarbel);
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