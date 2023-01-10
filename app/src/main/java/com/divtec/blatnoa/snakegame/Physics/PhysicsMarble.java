package com.divtec.blatnoa.snakegame.Physics;

import android.app.Activity;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.divtec.blatnoa.snakegame.Tick.StackLimitReachedException;
import com.divtec.blatnoa.snakegame.Tick.TickManager;
import com.divtec.blatnoa.snakegame.Tick.Tickable;

public class PhysicsMarble extends Collider implements Tickable {

    private final float bounciness = 0.45f;

    private Activity activity;
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
        super(viewToBind);

        this.activity = activity;

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
        super(viewToBind);

        this.activity = activity;

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
        TickManager.getTickManager().addTickObject(this);
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

        PointF screenSizeCM = getPhysicalScreenSizeCM();
        float xBiasByCM = 1 / screenSizeCM.x;
        float yBiasByCM = 1 / screenSizeCM.y;

        // Using delta time and acceleration, calculate new position
        float newXPosition = (float) (xPosition + xBiasByCM * xSpeed * 10 * deltaTime / 1000f);
        float newYPosition = (float) (yPosition + yBiasByCM * ySpeed * 10 * deltaTime / 1000f);

        if (newXPosition < 0) { // If marble has reached left bound
            // Correct position and bounce marble
            newXPosition = 0;
            bounceX();
        } else if (newXPosition > 1) { // If marble has reached right bound
            // Correct position and bounce marble
            newXPosition = 1;
            bounceX();
        }

        if (newYPosition < 0) { // If marble has reached lower bound
            // Correct position and bounce marble
            newYPosition = 0;
            bounceY();
        } else if (newYPosition > 1) { // If marble has reached upper bound
            // Correct position and bounce marble
            newYPosition = 1;
            bounceY();
        }

        setPositions(newXPosition, newYPosition);
    }

    private PointF getPhysicalScreenSizeCM() {
        PointF screenSize = new PointF();

        // Get screen size in cm
        DisplayMetrics metrics = activity.getBaseContext().getResources().getDisplayMetrics();
        float pixelWidth = metrics.widthPixels;
        float pixelHeight = metrics.heightPixels;
        pixelWidth /= metrics.xdpi;
        pixelHeight /= metrics.ydpi;
        screenSize.x = pixelWidth * 2.54f;
        screenSize.y = pixelHeight * 2.54f;

        return screenSize;
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
                viewBinding.setLayoutParams(params);
            }
        });

    }

    private void bounceX() {
        double randBounceFlux = (Math.random() - .5) * 0.1f;
        xSpeed *= -bounciness + randBounceFlux;
    }

    private void bounceY() {
        double randBounceFlux = (Math.random() - .5) * 0.1f;
        ySpeed *= -bounciness + randBounceFlux;
    }

    @Override
    protected void colliding(Collider other) {
        // Find where the collision happened
        float xDistance = other.viewBinding.getX() - viewBinding.getX();
        float yDistance = other.viewBinding.getY() - viewBinding.getY();

        if (xDistance > 0) { // If the collision happened on the right, bounce the marble on the x axis
            bounceX();
        } else { // else bounce on the y axis
            bounceY();
        }
        moveMarble(1);
    }

    /**
     * Run all the physics calculations
     * @param deltaTime The time since the last tick
     */
    @Override
    public void tick(long deltaTime) {
        super.tick(deltaTime);
        addAccelerationValues(deltaTime);
        moveMarble(deltaTime);
    }
}


