package com.divtec.blatnoa.snakegame.Snake;

import android.app.Activity;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.R;

import java.util.ArrayList;

public class SnakeAdapter {

    // TODO Implement this class to show the snake in the grid
    private final GridLayout grid;
    private final Activity activity;

    private Snake.GameCell prevHeadCell;
    private Snake.GameCell prevFirstBodyCell;
    private Snake.GameCell prevLastBodyCell;

    public SnakeAdapter(GridLayout grid) {
        this.grid = grid;
        activity = (Activity) grid.getContext();
    }

    /**
     * Update the snake in the grid when it moves
     * @param head The new head cell
     * @param bodies The body cells
     */
    public void updateSnakeOnMove(Snake.GameCell head, ArrayList<Snake.GameCell> bodies) {
        activity.runOnUiThread(() -> {

            // Remove the tail if the snake is not growing
            if (prevLastBodyCell != null && !bodies.contains(prevLastBodyCell)) {
                // Get the image view
                ImageView tailView = (ImageView) grid.getChildAt(coordToIndex(prevLastBodyCell.x, prevLastBodyCell.y));

                // Remove the tail
                tailView.setImageResource(0);
            }

            // Get the image view
            ImageView bodyView = (ImageView) grid.getChildAt(coordToIndex(prevHeadCell.x, prevHeadCell.y));

            if (!bodies.isEmpty()) { // If the snake has bodies

                Snake.GameCell firstBody = bodies.get(bodies.size() - 1);

                // Place body at previous head
                // Check if the snake is turning
                if (prevFirstBodyCell == null || firstBody.direction == prevFirstBodyCell.direction) { // If not turning
                    // Place straight body
                    bodyView.setImageResource(R.drawable.snake_body);
                    // Rotate the image view
                    bodyView.setRotation(firstBody.direction.rotation);
                } else { // If turning
                    if (prevFirstBodyCell.direction.getTurn(firstBody.direction) == Snake.Direction.LEFT) { // If turning left
                        bodyView.setImageResource(R.drawable.snake_corner_left);
                    } else { // If turning right
                        bodyView.setImageResource(R.drawable.snake_corner_right);
                    }
                    // Place turning body
                    bodyView.setRotation(firstBody.direction.rotation);
                }

                prevFirstBodyCell = firstBody;

                // Remember last body
                prevLastBodyCell = bodies.get(0);
            } else { // If snake is only head
                // Clear the previous head position
                bodyView.setImageResource(0);
            }

            // Place the head
            placeHead(head);
        });
    }

    /**
     * Regenerates the foods in the grid
     * @param foods The foods cells
     */
    public void updateFood(ArrayList<Snake.GameCell> foods) {
        activity.runOnUiThread(() -> {
            placeApples(foods);
        });
    }

    /**
     * Initialize the grid with the snake and the foods
     * @param head The head cell
     * @param foods The food cells
     */
    public void initGrid(Snake.GameCell head, ArrayList<Snake.GameCell> foods) {
        activity.runOnUiThread(() -> {
            // Clear grid
            for (int i = 0; i < grid.getChildCount(); i++) {
                ImageView view = (ImageView) grid.getChildAt(i);
                view.setImageResource(0);
            }

            // Add head
            placeHead(head);

            // Add foods
            placeApples(foods);
        });
    }

    /**
     * Place the head in the grid
     * @param head The head cell
     */
    private void placeHead(Snake.GameCell head) {
        ImageView headView = (ImageView) grid.getChildAt(coordToIndex(head.x, head.y));
        headView.setImageResource(R.drawable.snake_head);
        headView.setRotation(head.direction.rotation);
        prevHeadCell = head;
    }

    /**
     * Place the foods in the grid
     * @param foods The food cells
     */
    private void placeApples(ArrayList<Snake.GameCell> foods) {
        // Add foods
        for (Snake.GameCell food : foods) {
            ImageView foodView = (ImageView) grid.getChildAt(coordToIndex(food.x, food.y));
            foodView.setImageResource(R.drawable.apple);
            foodView.setRotation(0);
        }
    }

    /**
     * Convert the coordinates to the index in the grid
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The index corresponding to the coordinates
     */
    private int coordToIndex(int x, int y) {
        return x + y * grid.getColumnCount();
    }

}
