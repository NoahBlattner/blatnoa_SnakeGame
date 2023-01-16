package com.divtec.blatnoa.snakegame.Snake;

import android.widget.GridLayout;
import android.widget.ImageView;

import com.divtec.blatnoa.snakegame.R;

import java.util.ArrayList;

public class SnakeAdapter {

    // TODO Implement this class to show the snake in the grid
    private final GridLayout grid;

    private Snake.GameCell prevHead;
    private Snake.GameCell prevLastBody;
    private ArrayList<Snake.GameCell> prevFoods;

    public SnakeAdapter(GridLayout snakeGrid) {
        this.grid = snakeGrid;
    }

    public void updateGrid(Snake.GameCell head, Snake.GameCell newBody, ArrayList<Snake.GameCell> foods) {
        // Remove old head
        if (prevHead != null) {
            ImageView prevHeadView = (ImageView) grid.getChildAt(coordToIndex(prevHead.x, prevHead.y));
            prevHeadView.setImageResource(0);
        }

        // Remove old bodies
        if (prevLastBody != null) {
            ImageView prevBodyView = (ImageView) grid.getChildAt(coordToIndex(prevLastBody.x, prevLastBody.y));
            prevBodyView.setImageResource(0);
        }

        // Remove old foods
        if (prevFoods != null) {
            for (Snake.GameCell food : prevFoods) {
                ImageView prevFoodView = (ImageView) grid.getChildAt(coordToIndex(food.x, food.y));
                prevFoodView.setImageResource(0);
            }
        }

        // Add new head
        if (!head.equals(prevHead)) {
            ImageView headView = (ImageView) grid.getChildAt(coordToIndex(head.x, head.y));
            headView.setImageResource(R.drawable.snake_head);
            headView.setRotation(head.direction.rotation);
            prevHead = head;
        }

        // Add new bodies
        if (newBody != null) {
            ImageView bodyView = (ImageView) grid.getChildAt(coordToIndex(newBody.x, newBody.y));

            if (newBody.direction == prevLastBody.direction) {
                bodyView.setImageResource(R.drawable.snake_body);
            } else {
                bodyView.setImageResource(R.drawable.snake_corner);
            }
            bodyView.setRotation(newBody.direction.rotation);

            prevLastBody = newBody;
        }

        // Add new foods
        if (!foods.equals(prevFoods)) {
            for (Snake.GameCell food : foods) {
                ImageView foodView = (ImageView) grid.getChildAt(coordToIndex(food.x, food.y));
                foodView.setImageResource(R.drawable.apple);
            }
            prevFoods = foods;
        }

    }

    private int coordToIndex(int x, int y) {
        return x + y * grid.getColumnCount();
    }

}
