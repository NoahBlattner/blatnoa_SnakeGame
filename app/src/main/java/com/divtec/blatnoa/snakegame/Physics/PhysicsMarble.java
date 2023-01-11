package com.divtec.blatnoa.snakegame.Physics;

import android.app.Activity;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.divtec.blatnoa.snakegame.Tick.Tickable;

public class PhysicsMarble extends Collider implements Tickable {

    private final float bounciness = 0.45f;

    enum CollisionSide {
        UNKNOWN,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    private final ConstraintLayout parentLayout;
    private int parentWidth = 0;
    private int parentHeight = 0;

    private float xPosition;
    private float yPosition;

    private static double lastXReading = 0;
    private static double lastYReading = 0;

    private double xSpeed = 0;
    private double ySpeed = 0;

    /**
     * Constructor for the physics marble
     * @param viewToBind The image view to use as marble
     */
    public PhysicsMarble(ImageView viewToBind) {
        super(viewToBind);

        parentLayout = (ConstraintLayout) viewToBind.getParent();

        viewBinding.getViewTreeObserver().addOnGlobalLayoutListener(this::setParentDimensions);

        setPositions(randomFloat(0, parentWidth-bounds.width()), randomFloat(0, parentHeight-bounds.height()));
    }

    /**
     * Constructor for the PhysicsMarble
     * @param viewToBind The image view to use as marble
     * @param useRandomStartPosition Whether to use a random start position or start at the center
     */
    public PhysicsMarble(ImageView viewToBind, boolean useRandomStartPosition) {
        super(viewToBind);

        parentLayout = (ConstraintLayout) viewToBind.getParent();

        viewBinding.getViewTreeObserver().addOnGlobalLayoutListener(this::setParentDimensions);

        if (useRandomStartPosition) {
            setPositions(randomFloat(0, parentWidth-bounds.width()), randomFloat(0, parentHeight-bounds.height()));
        }
    }

    /**
     * Sets the parent dimensions
     */
    private void setParentDimensions() {
        parentWidth = parentLayout.getWidth();
        parentHeight = parentLayout.getHeight();
    }

    /**
     * Gets a random float between two values
     * @param min The lower bound
     * @param max The upper bound
     * @return A random float between the two values
     */
    private float randomFloat(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    /**
     * Updates the accelerometer values
     * @param xAcceleration The current x acceleration
     * @param yAcceleration The current y acceleration
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
        float xPixelsByCM = getDisplayMetrics().widthPixels / screenSizeCM.x;
        float yPixelsByCM = getDisplayMetrics().heightPixels / screenSizeCM.y;

        // Using delta time and acceleration, calculate new position
        float newXPosition = (float) (xPosition + xPixelsByCM * xSpeed * 10 * deltaTime / 1000f);
        float newYPosition = (float) (yPosition + yPixelsByCM * ySpeed * 10 * deltaTime / 1000f);

        if (newXPosition < 0) { // If marble has reached left bound
            // Correct position and bounce marble
            newXPosition = 0;
            bounceX();
        } else if (newXPosition > parentWidth - bounds.width()) { // If marble has reached right bound
            // Correct position and bounce marble
            newXPosition = parentWidth - bounds.width();
            bounceX();
        }

        if (newYPosition < 0) { // If marble has reached lower bound
            // Correct position and bounce marble
            newYPosition = 0;
            bounceY();
        } else if (newYPosition > parentHeight - bounds.height()) { // If marble has reached upper bound
            // Correct position and bounce marble
            newYPosition = parentHeight - bounds.height();
            bounceY();
        }

        setPositions(newXPosition, newYPosition);
    }

    /**
     * Gets the display metrics from the activity
     * @return The display metrics
     */
    private DisplayMetrics getDisplayMetrics() {
        Activity activity = (Activity) viewBinding.getContext();
        return activity.getBaseContext().getResources().getDisplayMetrics();
    }

    /**
     * Gets the physical screen size in centimeters
     * @return A PointF with the width and height of the screen in centimeters
     */
    private PointF getPhysicalScreenSizeCM() {
        PointF screenSize = new PointF();

        // Get screen size in cm
        DisplayMetrics metrics = getDisplayMetrics();
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

        viewBinding.setX(newXPosition);
        viewBinding.setY(newYPosition);
    }

    /**
     * Bounce the marble on the x axis
     */
    private void bounceX() {
        double randBounceFlux = (Math.random() - .5) * 0.1f;
        xSpeed *= -bounciness + randBounceFlux;
    }

    /**
     * Bounce the marble on the y axis
     */
    private void bounceY() {
        double randBounceFlux = (Math.random() - .5) * 0.1f;
        ySpeed *= -bounciness + randBounceFlux;
    }

    /**
     * Find the side on which the marble is colliding with another collider
     * @param other The other collider
     * @return The side on which the marble is colliding
     */
    private CollisionSide findCollisionSide(Collider other) {
        CollisionSide side = CollisionSide.UNKNOWN;

        // Find where the collision happened
        if (bounds.left <= other.bounds.right) {
            side = CollisionSide.LEFT;
        } else if (bounds.right >= other.bounds.left) {
            side = CollisionSide.RIGHT;
        } else if (bounds.top <= other.bounds.bottom) {
            side = CollisionSide.TOP;
        } else if (bounds.bottom >= other.bounds.top) {
            side = CollisionSide.BOTTOM;
        }
        return side;
    }

    @Override
    protected void colliding(Collider other) {
        switch (findCollisionSide(other)) {
            case LEFT:
                setPositions(xPosition, other.bounds.bottom);
                bounceY();
                break;
            case RIGHT:
                setPositions(xPosition, other.bounds.top - bounds.height());
                bounceY();
                break;
            case TOP:
                setPositions(other.bounds.right, yPosition);
                bounceX();
                break;
            case BOTTOM:
                setPositions(other.bounds.left - bounds.width(), yPosition);
                bounceX();
                break;
        }
    }

    /**
     * Run all the physics calculations
     * @param deltaTime The time since the last tick
     */
    @Override
    synchronized public void tick(long deltaTime) {
        super.tick(deltaTime);
        addAccelerationValues(deltaTime);
        moveMarble(deltaTime);
    }
}


