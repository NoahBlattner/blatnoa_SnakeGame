package com.divtec.blatnoa.snakegame;

import android.app.Activity;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.concurrent.TimeUnit;

public class PhysicsMarble {

    final float bounciness = 0.45f;

    Activity activity;
    ImageView binding;
    ConstraintLayout.LayoutParams params;

    float xPosition = .5f;
    float yPosition = .5f;

    double lastXReading = 0;
    double lastYReading = 0;

    double xSpeed = 0;
    double ySpeed = 0;

    public PhysicsMarble(Activity activity, ImageView viewToBind) {
        this.activity = activity;
        binding = viewToBind;

        params = (ConstraintLayout.LayoutParams) viewToBind.getLayoutParams();

        setPositions((float) Math.random(), (float) Math.random());

        TickManager.getTickManager().addTickObject(this);
    }

    public void updateAccelerationValues(Double xSpeed, Double ySpeed) {
        lastXReading = xSpeed;
        lastYReading = ySpeed;
    }

    private void addAccelerationValues() {
        ySpeed += lastXReading;
        xSpeed -= lastYReading;
    }

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

    public void tick(long deltaTime) {
        addAccelerationValues();
        moveMarble(deltaTime);
    }
}


