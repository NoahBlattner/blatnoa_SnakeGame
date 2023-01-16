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
    private GameCell head;
    private final ArrayList<GameCell> foodCells = new ArrayList<>();

    private Direction nextDirection = Direction.RIGHT;

    private final int MIN_MOVE_TIME_MS = 100;
    private int moveTimeMS = 1000;
    private int timeSinceLastMove = 0;

    /**
     * Constructor for the snake
     * @param playingFieldGrid The grid to use as playing field
     */
    public Snake(GridLayout playingFieldGrid) {
        this.grid = playingFieldGrid;

        // Bind to adapter
        adapter = new SnakeAdapter(this);

        TickManager.getTickManager().addTickObject(this);

        // Get approx center of grid
        int x = grid.getColumnCount() / 2;
        int y = grid.getRowCount() / 2;

        // Create snake head
        head = new GameCell(x, y, nextDirection);

        ImageView centerImage = (ImageView) grid.getChildAt(x + y * grid.getColumnCount());
        centerImage.setImageResource(R.drawable.snake_head);

        createFood();

        adapter.updateGrid(head, null, foodCells);
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
        } while (!positionValid(randX, randY));

        foodCells.add(new GameCell(randX, randY, null));
    }

    private boolean positionValid(int x, int y) {
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

        boolean removeTail = true;
        if (isFoodCell(newX, newY)) { // If new position is food
            // Don't remove tail
            removeTail = false;
            onEatFood();
        } else if (!positionValid(newX, newY)) { // Check if new position is out of bounds
            System.out.println("Game over");
            return;
        }

        // Move head
        head = new GameCell(newX, newY, nextDirection);

        // Add new body
        GameCell firstBodyCell = new GameCell(head.x, head.y, nextDirection);
        bodyCells.add(firstBodyCell);
        if (removeTail) { // If not eating food
            // Remove last body
            bodyCells.remove(0);
        }

        // Update grid
        adapter.updateGrid(head, firstBodyCell, foodCells);
    }

    private void onEatFood() {
        // Remove food
        foodCells.remove(0);

        // Create new food
        createFood();

        // Increase move time
        if (moveTimeMS > MIN_MOVE_TIME_MS) {
            moveTimeMS -= 15;
        }
    }

    public ArrayList<GameCell> getBodyCells() {
        return bodyCells;
    }

    public GridLayout getGrid() {
        return grid;
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
