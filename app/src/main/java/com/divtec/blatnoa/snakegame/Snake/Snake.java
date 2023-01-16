package com.divtec.blatnoa.snakegame.Snake;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.R;
import com.divtec.blatnoa.snakegame.Tick.TickManager;
import com.divtec.blatnoa.snakegame.Tick.Tickable;

import java.util.ArrayList;

public class Snake implements Tickable {

    private static class SnakeCell {
        private enum CellType {
            HEAD,
            BODY,
            TAIL,
            FOOD,
            EMPTY
        }
        private final int x;
        private final int y;
        private final Direction direction;
        private final CellType type;

        public SnakeCell(int x, int y, Direction direction, CellType cellType) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.type = cellType;
        }
    }

    /**
     * Direction enum
     */
    public enum Direction {
        UP(180),
        DOWN(0),
        LEFT(90),
        RIGHT(270);

        final int rotation;

        /**
         * Enum constructor
         * @param rotation The rotation associated with this direction
         */
        Direction(int rotation) {
            this.rotation = rotation;
        }

        /**
         * Get the opposite direction of this direction
         * @return
         */
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

        /**
         * Check if this direction is opposite to another direction
         * @param other The other direction
         * @return True if this direction is opposite to the other direction
         */
        public boolean isOpposite(Direction other) {
            return this.opposite() == other;
        }
    }

    private final GridLayout grid;
    private final SnakeCell[][] cells;

    private Direction direction = Direction.RIGHT;

    int moveTimeMS = 1000;
    int timeSinceLastMove = 0;

    /**
     * Constructor for the snake
     * @param playingFieldGrid The grid to use as playing field
     */
    public Snake(GridLayout playingFieldGrid) {
        // Create cells
        this.grid = playingFieldGrid;
        cells = new SnakeCell[grid.getColumnCount()][grid.getRowCount()];

        TickManager.getTickManager().addTickObject(this);

        // Create snake head
        int x = grid.getColumnCount() / 2;
        int y = grid.getRowCount() / 2;

        cells[x][y] = new SnakeCell(x, y, direction, SnakeCell.CellType.HEAD);

        ImageView centerImage = (ImageView) grid.getChildAt(x + y * grid.getColumnCount());
        centerImage.setImageResource(R.drawable.snake_head);
        centerImage.setAdjustViewBounds(true);
    }

    public void turn(Direction newDirection) {
        if (!direction.isOpposite(newDirection)) {
            direction = newDirection;
        }
    }

    private void move() {
        // TODO
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
