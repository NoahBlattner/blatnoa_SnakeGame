package com.divtec.blatnoa.snakegame.Physics;

import android.graphics.Rect;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.Tick.TickManager;
import com.divtec.blatnoa.snakegame.Tick.Tickable;

import java.util.ArrayList;

public class Collider implements Tickable {

    private static final ArrayList<Collider> colliders = new ArrayList<>();

    protected final ImageView viewBinding;

    /**
     * Constructor for a collider
     * @param viewToBind The view that is bound to the collider
     */
    public Collider(ImageView viewToBind) {
        // Check if another collider bound to this view exists
        colliders.forEach(collider -> {
            if (collider.viewBinding == viewToBind) {
                // If so, throw an exception to avoid double binding
                throw new DuplicateColliderException();
            }
        });

        viewBinding = viewToBind;

        TickManager.getTickManager().addTickObject(this);

        viewBinding.getViewTreeObserver().addOnGlobalLayoutListener(this::initializeCollider);
    }

    /**
     * Initializes the collider
     */
    private void initializeCollider() {
        if (!colliders.contains(this)) {
            colliders.add(this);
        }
    }

    /**
     * Get the rect dimensions of the bound image view
     * @return A rect containing the dimensions of the bound image view
     */
    protected Rect getBounds() {
        int x = (int) viewBinding.getX();
        int y = (int) viewBinding.getY();
        int width = viewBinding.getWidth();
        int height = viewBinding.getHeight();
        return new Rect(x, y, x + width, y + height);
    }

    /**
     * Function defining what happens on a collision event
     * Should probably be overridden in subclasses
     * @param other The other collider of the collision event
     */
    protected void onCollision(Collider other) {
        System.out.println("Colliding with " + other);
    }

    /**
     * Checks for collision with all other colliders
     * onCollision event is called if a collision is detected
     */
    protected void checkForCollision(Rect bounds) {
        ArrayList<Collider> collidersCopy = new ArrayList<>(colliders);
        for (Collider otherCollider : collidersCopy) {
            if (otherCollider != this && otherCollider != null) {
                Rect otherBounds = otherCollider.getBounds();
                if (otherBounds.intersect(bounds)) {
                    onCollision(otherCollider);
                }
            }
        }
    }

    /**
     * Clears all colliders
     */
    public static void clearStack() {
        colliders.clear();
    }

    /**
     * Function called on every tick
     */
    @Override
    public void tick(long deltaTime) {
        checkForCollision(getBounds());
    }
}
