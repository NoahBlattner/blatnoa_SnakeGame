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
    private final float MAX_RAND_BOUNCE_FLUX = .25f;
    private final float FRICTION = 0.95f;

    private float nextX;
    private float nextY;
    
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
        lastXReading += xAcceleration - lastXReading;
        lastYReading += yAcceleration - lastYReading;
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
     * Calculates the next positions of the marble
     * @param deltaTime The time since the last tick
     */
    private void calculateNextPosition(long deltaTime) {

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

        nextX = newXPosition;
        nextY = newYPosition;
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

        // Update the view position in the UI thread
        Activity activity = (Activity) viewBinding.getContext();
        activity.runOnUiThread(() -> {
            viewBinding.setX(newXPosition);
            viewBinding.setY(newYPosition);
        });
    }

    /**
     * Bounce the marble on the x axis
     */
    private void bounceX() {
        double randBounceFlux = randomFloat(0, MAX_RAND_BOUNCE_FLUX);
        xSpeed *= -BOUNCINESS + randBounceFlux;
    }

    /**
     * Bounce the marble on the y axis
     */
    private void bounceY() {
        double randBounceFlux = randomFloat(0, MAX_RAND_BOUNCE_FLUX);
        ySpeed *= -BOUNCINESS + randBounceFlux;
    }

    /**
     * Find the side on which the marble is colliding with another collider
     * @param other The other collider
     * @return The side on which the marble is colliding
     */
    private CollisionSide findCollisionSide(Collider other) {
        CollisionSide side = CollisionSide.UNKNOWN;

        // Get the distance between the two colliders for each side
        float leftDistance = Math.abs(getBounds().left - other.getBounds().right);
        float rightDistance = Math.abs(getBounds().right - other.getBounds().left);
        float topDistance = Math.abs(getBounds().top - other.getBounds().bottom);
        float bottomDistance = Math.abs(getBounds().bottom - other.getBounds().top);

        // Find the smallest distance
        float minDistance = Math.min(Math.min(leftDistance, rightDistance), Math.min(topDistance, bottomDistance));

        if (minDistance == leftDistance) {
            side = CollisionSide.LEFT;
        } else if (minDistance == rightDistance) {
            side = CollisionSide.RIGHT;
        } else if (minDistance == topDistance) {
            side = CollisionSide.TOP;
        } else if (minDistance == bottomDistance) {
            side = CollisionSide.BOTTOM;
        }
        return side;
    }

    /**
     * Set the speed of the marble
     * @param xSpeed The new x speed
     * @param ySpeed The new y speed
     */
    protected void setSpeed(double xSpeed, double ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    @Override
    protected void onCollision(Collider other) {
        CollisionSide side = findCollisionSide(other);

        // If other is a marble
        if (other instanceof PhysicsMarble) {
            // Transfer speed
            // TODO Marble passes speed to other, then other passes speed back to this when
            // TODO it's collision is processed -> Same speed is passed back and forth
            PhysicsMarble otherMarble = (PhysicsMarble) other;
            otherMarble.setSpeed(xSpeed * -BOUNCINESS, ySpeed * -BOUNCINESS);
        } else { // The marble is colliding with something else
            // Bounce the marble
            switch (side) {
                case TOP:
                case BOTTOM:
                    bounceY();
                    break;
                case LEFT:
                case RIGHT:
                    bounceX();
                    break;
            }
        }

        // Adapt next positions to collision
        switch (side) {
            case TOP:
                nextY = other.getBounds().bottom + 1;
                break;
            case BOTTOM:
                nextY = other.getBounds().top - getBounds().height() - 1;
                break;
            case LEFT:
                nextX = other.getBounds().right + 1;
                break;
            case RIGHT:
                nextX = other.getBounds().left - getBounds().width() - 1;
                break;
        }
    }

    @Override
    protected void checkForCollision(Rect bounds) {
        int xDiff = (int) (nextX - xPosition);
        int yDiff = (int) (nextY - yPosition);
        Rect futureRect = new Rect(bounds.left + xDiff, bounds.top + yDiff,
                                 bounds.right + xDiff, bounds.bottom + yDiff);
        super.checkForCollision(futureRect);
    }

    /**
     * Run all the physics calculations
     * @param deltaTime The time since the last tick
     */
    @Override
    public void tick(long deltaTime) {
        // Update the acceleration since the last tick
        addAccelerationValues(deltaTime);

        // Calculate the next position
        calculateNextPosition(deltaTime);

        // Use collider tick to check for collisions
        super.tick(deltaTime);

        // Set the new position
        setPositions(nextX, nextY);
    }
}


