package com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Controllers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.divtec.blatnoa.snakegame.Snake.Snake;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.Score;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.SnakeSQLiteOpenHelper;

import java.util.ArrayList;

public class ScoreManager {

    private SnakeSQLiteOpenHelper helper;
    private ArrayList<Score> scoreList = new ArrayList<>();

    public ScoreManager(Context context) {
        helper = new SnakeSQLiteOpenHelper(context);
        scoreList = initScoreList();
    }

    /**
     * Load the scores from the database
     * @return
     */
    private ArrayList<Score> initScoreList() {
        // Get all the scores from the database into a cursor
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(true, "tb_snakeScores", new String[]{"player", "score", "timeDate"}, null, null, null, null, "score", null);

        // Read the cursor
        ArrayList<Score> listScore = new ArrayList<>();
        while (cursor.moveToNext()) {
            // Add the score to the list
            listScore.add(new Score(cursor));
        }
        cursor.close();
        db.close();

        return listScore;
    }

    /**
     * Add a score to the database
     * @param player The player's name
     * @param score The player's score
     * @return True if the score was added, false otherwise
     */
    public boolean addScore(String player, int score) {
        return helper.addScore(player, score);
    }

    /**
     * Clear the score list
     * @return True if the list was cleared
     */
    public boolean clearScores() {
        if (helper.clearScores()) {
            return true;
        }
        return false;
    }

    public Score getLowestScore() {
        return scoreList.get(scoreList.size() - 1);
    }

    /**
     * Get the score list
     * @return The score list
     */
    public ArrayList<Score> getScores() {
        return scoreList;
    }
}
