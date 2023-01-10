package com.divtec.blatnoa.snakegame;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.concurrent.TimeUnit;

// TODO Move tick to a parent class TickObject
public class PhysicsMarble {

    private final float bounciness = 0.45f;

    private Activity activity;
    private ImageView binding;
    private ConstraintLayout.LayoutParams params;

    private float xPosition = .5f;
    private float yPosition = .5f;

    private static double lastXReading = 0;
    private static double lastYReading = 0;

    private double xSpeed = 0;
    private double ySpeed = 0;

    /**
     * Constructor for the physics marble
     * @param activity The activity in witch the marble is placed
     * @param viewToBind The image view to use as marble
     */
    public PhysicsMarble(Activity activity, ImageView viewToBind) {
        this.activity = activity;
        binding = viewToBind;

        params = (ConstraintLayout.LayoutParams) viewToBind.getLayoutParams();

        setPositions((float) Math.random(), (float) Math.random());

        pushToTickObjectStack();
    }

    /**
     * Constructor for the PhysicsMarble
     * @param activity The activity in witch the marble is placed
     * @param viewToBind The image view to use as marble
     * @param useRandomStartPosition Whether to use a random start position or start at the center
     */
    public PhysicsMarble(Activity activity, ImageView viewToBind, boolean useRandomStartPosition) {
        this.activity = activity;
        binding = viewToBind;

        params = (ConstraintLayout.LayoutParams) viewToBind.getLayoutParams();

        if (useRandomStartPosition) {
            setPositions((float) Math.random(), (float) Math.random());
        } else {
            setPositions(0.5f, 0.5f);
        }

        pushToTickObjectStack();
    }

    /**
     * Pushes the tick object to the tick object stack
     */
    private void pushToTickObjectStack() {
        boolean additionSuccessful = TickManager.getTickManager().addTickObject(this);
        if (!additionSuccessful) {
            Toast.makeText(activity, "Cannot add anymore physics objects", Toast.LENGTH_SHORT).show();
            binding.setVisibility(ImageView.INVISIBLE);
        } else {
            binding.setVisibility(ImageView.VISIBLE);
        }
    }

    /**
     * Updates the accelerometer values
     * @param xAcceleration The current x acceleration
     * @param yAcceleration
     */
    public static void updateAccelerationValues(Double xAcceleration, Double yAcceleration) {
        lastXReading = xAcceleration;
        lastYReading = yAcceleration;
    }

    /**
     * Updates the acceleration values with the last sensor readings
     */
    private void addAccelerationValues(long deltaTime) {
        // IMPORTANT -> X and Y are inverted due to landscape mode
        ySpeed += lastXReading * deltaTime / 1000;
        xSpeed -= lastYReading * deltaTime / 1000;
    }

    /**
     * Move the marble to a new position according to it's speed
     * @param deltaTime The time since the last update
     */
    private void moveMarble(long deltaTime) {

        // Using delta time and acceleration, calculate new position
        float newXPosition = (float) (xPosition + xSpeed / 100 * (deltaTime / 1000f));
        float newYPosition = (float) (yPosition + ySpeed / 100 * (deltaTime / 1000f));

        if (newXPosition < 0) { // If marble has reached left bound
            // Correct position and bounce marble
            newXPosition = 0;
            xSpeed *= -bounciness;
        } else if (newXPosition > 1) { // If marble has reached right bound
            // Correct position and bounce marble
            newXPosition = 1;
            xSpeed *= -bounciness;
        }

        if (newYPosition < 0) { // If marble has reached lower bound
            // Correct position and bounce marble
            newYPosition = 0;
            ySpeed *= -bounciness;
        } else if (newYPosition > 1) { // If marble has reached upper bound
            // Correct position and bounce marble
            newYPosition = 1;
            ySpeed *= -bounciness;
        }

        setPositions(newXPosition, newYPosition);
    }

    /**
     * Update the position of the marble
     * @param newXPosition The new x position
     * @param newYPosition The new y position
     */
    private void setPositions(float newXPosition, float newYPosition) {
        xPosition = newXPosition;
        yPosition = newYPosition;

        params.horizontalBias = xPosition;
        params.verticalBias = yPosition;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.setLayoutParams(params);
            }
        });

    }

    /**
     * Run all the physics calculations
     * @param deltaTime The time since the last tick
     */
    public void tick(long deltaTime) {
        addAccelerationValues(deltaTime);
        moveMarble(deltaTime);
    }
}


