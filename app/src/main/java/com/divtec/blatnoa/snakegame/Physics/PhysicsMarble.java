package com.divtec.blatnoa.snakegame.Physics;

import android.app.Activity;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.divtec.blatnoa.snakegame.Tick.Tickable;

public class PhysicsMarble extends Collider implements Tickable {
    
    private final float BOUNCINESS = 0.45f;
    private final float RAND_BOUNCE_FACTOR = .25f;
    private final float FRICTION = 0.95f;
    
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

        setPositions(randomFloat(0, parentWidth-getBounds().width()), randomFloat(0, parentHeight-getBounds().height()));
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
            setPositions(randomFloat(0, parentWidth-getBounds().width()), randomFloat(0, parentHeight-getBounds().height()));
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
        float newXPosition = (float) (xPosition + xPixelsByCM * xSpeed * FRICTION * 10 * deltaTime / 1000f);
        float newYPosition = (float) (yPosition + yPixelsByCM * ySpeed * FRICTION * 10 * deltaTime / 1000f);

        if (newXPosition < 0) { // If marble has reached left bound
            // Correct position and bounce marble
            newXPosition = 0;
            bounceX();
        } else if (newXPosition > parentWidth - getBounds().width()) { // If marble has reached right bound
            // Correct position and bounce marble
            newXPosition = parentWidth - getBounds().width();
            bounceX();
        }

        if (newYPosition < 0) { // If marble has reached lower bound
            // Correct position and bounce marble
            newYPosition = 0;
            bounceY();
        } else if (newYPosition > parentHeight - getBounds().height()) { // If marble has reached upper bound
            // Correct position and bounce marble
            newYPosition = parentHeight - getBounds().height();
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
        double randBounceFlux = (Math.random() - RAND_BOUNCE_FACTOR) * 0.1f;
        xSpeed *= -BOUNCINESS + randBounceFlux;
    }

    /**
     * Bounce the marble on the y axis
     */
    private void bounceY() {
        double randBounceFlux = (Math.random() - RAND_BOUNCE_FACTOR) * 0.1f;
        ySpeed *= -BOUNCINESS + randBounceFlux;
    }

    private float getAngleOfPoints(float originX, float originY, float targetX, float targetY) {
        float angle = (float) Math.toDegrees(Math.atan2(targetY - originY, targetX - originX));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Find the side on which the marble is colliding with another collider
     * @param other The other collider
     * @return The side on which the marble is colliding
     */
    private CollisionSide findCollisionSide(Collider other) {
        CollisionSide side = CollisionSide.UNKNOWN;

        // Find the side on which the marble is colliding
        float centerX = getBounds().centerX();
        float centerY = getBounds().centerY();
        float otherCenterX = other.getBounds().centerX();
        float otherCenterY = other.getBounds().centerY();

        float angleToTopLeft = getAngleOfPoints(otherCenterX, otherCenterY, other.getBounds().left, other.getBounds().top);
        float angleToTopRight = getAngleOfPoints(otherCenterX, otherCenterY, other.getBounds().right, other.getBounds().top);
        float angleToBottomLeft = getAngleOfPoints(otherCenterX, otherCenterY, other.getBounds().left, other.getBounds().bottom);
        float angleToBottomRight = getAngleOfPoints(otherCenterX, otherCenterY, other.getBounds().right, other.getBounds().bottom);

        float angle = getAngleOfPoints(otherCenterX, otherCenterY, centerX, centerY);

        if (angle >= angleToBottomRight && angle < angleToBottomLeft) {
            side = CollisionSide.TOP;
        } else if (angle >= angleToBottomLeft && angle < angleToTopLeft) {
            side = CollisionSide.RIGHT;
        } else if (angle >= angleToTopLeft && angle < angleToTopRight) {
            side = CollisionSide.BOTTOM;
        } else if (angle >= angleToTopRight || angle < angleToBottomRight) {
            side = CollisionSide.LEFT;
        }

        return side;
    }

    @Override
    protected void onCollision(Collider other) {
        switch (findCollisionSide(other)) {
            case TOP:
                setPositions(xPosition, other.getBounds().bottom);
                bounceY();
                break;
            case BOTTOM:
                setPositions(xPosition, other.getBounds().top - getBounds().height());
                bounceY();
                break;
            case LEFT:
                setPositions(other.getBounds().right, yPosition);
                bounceX();
                break;
            case RIGHT:
                setPositions(other.getBounds().left - getBounds().width(), yPosition);
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
        addAccelerationValues(deltaTime);
        moveMarble(deltaTime);
        super.tick(deltaTime);
    }
}


