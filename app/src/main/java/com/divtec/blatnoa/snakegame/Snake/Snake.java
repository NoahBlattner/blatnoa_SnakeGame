package com.divtec.blatnoa.snakegame.Snake;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.R;
import com.divtec.blatnoa.snakegame.Tick.TickManager;
import com.divtec.blatnoa.snakegame.Tick.Tickable;

public class Snake implements Tickable {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public Direction opposite() {
            switch (this) {
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
            }
            return null;
        }

        public boolean isOpposite(Direction other) {
            return this.opposite() == other;
        }
    }

    private final GridLayout grid;

    private Direction direction = Direction.RIGHT;

    int moveTimeMS = 1000;
    int timeSinceLastMove = 0;

    public Snake(GridLayout grid, int cellSize) {
        TickManager.getTickManager().addTickObject(this);
        this.grid = grid;
        ImageView centerImage = (ImageView) getCenterCellView();
        centerImage.setImageResource(R.drawable.snake_head);
        centerImage.setAdjustViewBounds(true);
    }

    private View getCenterCellView() {
        int x = grid.getColumnCount() / 2;
        int y = grid.getRowCount() / 2;

        return grid.getChildAt(x + y * grid.getColumnCount());
    }

    public void turn(Direction newDirection) {
        if (!direction.isOpposite(newDirection)) {
            direction = newDirection;
        }
    }

    private void move() {

    }

    @Override
    public void tick(long deltaTime) {
        timeSinceLastMove += deltaTime;
        if (timeSinceLastMove >= moveTimeMS) {
            move();
            timeSinceLastMove = 0;
        }
    }
}
