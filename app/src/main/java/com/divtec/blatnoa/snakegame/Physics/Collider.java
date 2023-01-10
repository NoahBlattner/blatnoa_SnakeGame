package com.divtec.blatnoa.snakegame.Physics;

import android.graphics.Rect;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.Tick.TickManager;
import com.divtec.blatnoa.snakegame.Tick.Tickable;

import java.util.ArrayList;

public class Collider implements Tickable {

    private static final ArrayList<Collider> colliders = new ArrayList<>();

    protected final ImageView viewBinding;
    protected Rect bounds = new Rect();

    public Collider(ImageView viewToBind) {
        viewBinding = viewToBind;

        TickManager.getTickManager().addTickObject(this);

        viewBinding.getViewTreeObserver().addOnGlobalLayoutListener(this::initializeCollider);
    }

    private void initializeCollider() {
        bounds = getRectFromImageView();
        colliders.add(this);
    }

    private Rect getRectFromImageView() {
        return new Rect(viewBinding.getLeft(), viewBinding.getTop(), viewBinding.getRight(), viewBinding.getBottom());
    }

    protected void colliding(Collider other) {
        System.out.println("Colliding with " + other);
    }

    synchronized private void checkForCollision() {
        ArrayList<Collider> collidersCopy = new ArrayList<>(colliders);
        for (Collider otherCollider : collidersCopy) {
            if (otherCollider != this && otherCollider != null) {
                if (otherCollider.bounds.intersect(bounds)) {
                    colliding(otherCollider);
                }
            }
        }
    }

    @Override
    synchronized public void tick(long deltaTime) {
        checkForCollision();
    }
}
