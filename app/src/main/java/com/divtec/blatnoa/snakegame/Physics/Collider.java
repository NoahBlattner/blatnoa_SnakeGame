package com.divtec.blatnoa.snakegame.Physics;

import android.graphics.Rect;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.Tick.Tickable;

import java.util.ArrayList;

public class Collider implements Tickable {

    private static ArrayList<Collider> colliders = new ArrayList<>();

    protected final ImageView viewBinding;
    protected Rect bounds;
    protected boolean initialized = false;

    public Collider(ImageView viewToBind) {
        viewBinding = viewToBind;

        viewBinding.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            bounds = getRectFromImageView();
            initialized = true;
        });

        colliders.add(this);
    }

    private Rect getRectFromImageView() {
        return new Rect(viewBinding.getLeft(), viewBinding.getTop(), viewBinding.getRight(), viewBinding.getBottom());
    }

    protected void colliding(Collider other) {
        System.out.println("Colliding with " + other);
    }

    private void checkForCollision() {
        for (Collider collider : colliders) {
            if (collider != this && collider != null && collider.initialized) {
                if (collider.bounds.intersect(bounds)) {
                    colliding(collider);
                }
            }
        }
    }

    @Override
    public void tick(long deltaTime) {
        if (initialized) {
            checkForCollision();
        }
    }
}
