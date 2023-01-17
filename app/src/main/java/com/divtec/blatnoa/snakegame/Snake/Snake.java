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

        public Direction getTurn(Direction other) {
            if (this == other) {
                return this;
            }
            if (this == UP) {
                if (other == LEFT) {
                    return LEFT;
                } else if (other == RIGHT) {
                    return RIGHT;
                }
            } else if (this == DOWN) {
                if (other == LEFT) {
                    return RIGHT;
                } else if (other == RIGHT) {
                    return LEFT;
                }
            } else if (this == LEFT) {
                if (other == UP) {
                    return RIGHT;
                } else if (other == DOWN) {
                    return LEFT;
                }
            } else if (this == RIGHT) {
                if (other == UP) {
                    return LEFT;
                } else if (other == DOWN) {
                    return RIGHT;
                }
            }
            return NONE;
        }
    }

    private GridLayout grid;

    private SnakeAdapter adapter;
    private final ArrayList<GameCell> bodyCells = new ArrayList<>();
    private GameCell head;
    private final ArrayList<GameCell> foodCells = new ArrayList<>();

    private Direction currentDirection = Direction.NONE;
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
        adapter = new SnakeAdapter(grid);

        TickManager.getTickManager().addTickObject(this);

        // Get approx center of grid
        int x = grid.getColumnCount() / 2;
        int y = grid.getRowCount() / 2;

        // Create snake head
        head = new GameCell(x, y, nextDirection);

        ImageView centerImage = (ImageView) grid.getChildAt(x + y * grid.getColumnCount());
        centerImage.setImageResource(R.drawable.snake_head);

        createFood(1);

        adapter.initGrid(head, foodCells);
    }

    /**
     * Turn the snake if it is not turning in the opposite direction
     * @param newDirection The new direction to turn to
     */
    public void turn(Direction newDirection) {
        if (!currentDirection.isOpposite(newDirection)) {
            nextDirection = newDirection;
        }
    }

    /**
     * Create new food cells
     * @param numberOfFood The number of food cells to create
     */
    private void createFood(int numberOfFood) {
        for (int i = 0; i < numberOfFood; i++) {
            // Create food
            int randX;
            int randY;
            do {
                randX = (int) (Math.random() * grid.getColumnCount());
                randY = (int) (Math.random() * grid.getRowCount());
            } while (!positionValid(randX, randY));

            foodCells.add(new GameCell(randX, randY, null));
        }

        adapter.updateFood(foodCells);
    }

    /**
     * Check if the position is valid (not occupied by snake and in bounds)
     * @param x The x position
     * @param y The y position
     * @return True if the position is valid (not occupied by snake and in bounds)
     */
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

    /**
     * Check if the position contains food
     * @param x The x position
     * @param y The y position
     * @return True if the position contains food
     */
    private boolean isFoodCell(int x, int y) {
        for (GameCell cell : foodCells) {
            if (cell.x == x && cell.y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Move the snake by 1 cell
     */
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

        // Update direction
        currentDirection = nextDirection;

        if (!positionValid(newX, newY)) { // If new position isn't free
            // Game over
            System.out.println("Game over");
            return;
        }

        boolean hasEaten = false;
        if (isFoodCell(newX, newY)) { // If new position is food
            hasEaten = true;
            onEatFood();
        }

        if (bodyCells.size() > 0 || hasEaten) { // If has body or has eaten
            // Place first body cell at old head position
            bodyCells.add(new GameCell(head.x, head.y, nextDirection));
        }

        if (!hasEaten && bodyCells.size() > 0) { // If snake hasn't eaten and has body
            // Remove last body cell
            bodyCells.remove(0);
        }

        // Move head
        head = new GameCell(newX, newY, nextDirection);

        // Update grid
        adapter.updateSnakeOnMove(head, new ArrayList<>(bodyCells));
    }

    /**
     * Called when the snake eats food
     */
    private void onEatFood() {
        // Remove food
        foodCells.remove(0);

        // Create new food
        createFood(1);

        // Increase move time
        if (moveTimeMS > MIN_MOVE_TIME_MS) {
            moveTimeMS -= 15;
        }
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
