package com.divtec.blatnoa.snakegame.Snake;

import android.widget.GridLayout;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.R;
import com.divtec.blatnoa.snakegame.Tick.TickManager;
import com.divtec.blatnoa.snakegame.Tick.Tickable;

import java.util.ArrayList;

public class Snake implements Tickable {

    protected static class GameCell {
        public int x;
        public int y;
        public Direction direction = Direction.NONE;

        public GameCell(int x, int y, Direction direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        public GameCell(int x, int y, boolean isFood) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof GameCell) {
                GameCell other = (GameCell) obj;
                return other.x == x && other.y == y;
            }
            return false;
        }
    }

    /**
     * Direction enum
     */
    public enum Direction {
        UP(180),
        DOWN(0),
        LEFT(90),
        RIGHT(270),
        NONE(-1);

        public final int rotation;

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

    private GridLayout grid;

    private SnakeAdapter adapter;
    private final ArrayList<GameCell> bodyCells = new ArrayList<>();
    private final GameCell head;
    private final ArrayList<GameCell> foodCells = new ArrayList<>();

    private Direction nextDirection = Direction.RIGHT;

    int moveTimeMS = 1000;
    int timeSinceLastMove = 0;

    /**
     * Constructor for the snake
     * @param playingFieldGrid The grid to use as playing field
     */
    public Snake(GridLayout playingFieldGrid) {
        this.grid = playingFieldGrid;

        // Bind to adapter
        adapter = new SnakeAdapter(grid);

        TickManager.getTickManager().addTickObject(this);

        // Get approx center of grid
        int x = grid.getColumnCount() / 2;
        int y = grid.getRowCount() / 2;

        // Create snake head
        head = new GameCell(x, y, nextDirection);

        ImageView centerImage = (ImageView) grid.getChildAt(x + y * grid.getColumnCount());
        centerImage.setImageResource(R.drawable.snake_head);
        centerImage.setAdjustViewBounds(true);

        createFood();
    }

    private int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public void turn(Direction newDirection) {
        if (!nextDirection.isOpposite(newDirection)) {
            nextDirection = newDirection;
        }
    }

    private void createFood() {
        // Create food
        int randX;
        int randY;
        do {
            randX = (int) (Math.random() * grid.getColumnCount());
            randY = (int) (Math.random() * grid.getRowCount());
        } while (!positionFree(randX, randY));

        foodCells.add(new GameCell(randX, randY, null));
    }

    private boolean positionFree(int x, int y) {
        if (x < 0 || x >= grid.getColumnCount() || y < 0 || y >= grid.getRowCount()) {
            return false;
        }

        if (head.x == x && head.y == y) {
            return false;
        }

        for (GameCell cell : bodyCells) {
            if (cell.x == x && cell.y == y) {
                return false;
            }
        }
        return true;
    }

    private boolean isFoodCell(int x, int y) {
        for (GameCell cell : foodCells) {
            if (cell.x == x && cell.y == y) {
                return true;
            }
        }
        return false;
    }

    private void move() {
        // Move head
        int newX = head.x;
        int newY = head.y;
        switch (nextDirection) {
            case UP:
                newY--;
                break;
            case DOWN:
                newY++;
                break;
            case LEFT:
                newX--;
                break;
            case RIGHT:
                newX++;
                break;
        }

        // Check if new position is out of bounds
        if (!positionFree(newX, newY)) {
            // TODO game over
            return;
        }

        if (bodyCells.size() > 0) { // If has body add body cell to take the head's place
        }
        // Move head
        head.x = newX;
        head.y = newY;
        head.direction = nextDirection;

        boolean removeTail = true;
        if (isFoodCell(newX, newY)) { // If new position is food
            // Don't remove tail
            removeTail = false;
            foodCells.remove(0);

            // Create new food
            createFood();
        }

        GameCell firstBodyCell = null;
        if (bodyCells.size() > 0) { // If has body
            // Move body
            firstBodyCell = new GameCell(head.x, head.y, nextDirection);
            bodyCells.add(firstBodyCell);
            if (removeTail) {
                // Remove tail
                bodyCells.remove(0);
            }
        }

        // Update grid
        adapter.updateGrid(head, firstBodyCell, foodCells);
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
