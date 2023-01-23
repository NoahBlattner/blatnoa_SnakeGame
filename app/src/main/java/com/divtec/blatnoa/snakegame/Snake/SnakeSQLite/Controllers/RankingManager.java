package com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Controllers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.Ranking;
import com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models.SnakeSQLiteOpenHelper;

import java.util.ArrayList;

public class RankingManager {

    private SnakeSQLiteOpenHelper helper;
    private ArrayList<Ranking> scoreList = new ArrayList<>();

    public RankingManager(Context context) {
        helper = new SnakeSQLiteOpenHelper(context);
        scoreList = initRankingList();
    }

    /**
     * Load the ranking from the database
     * @return The ranking list
     */
    private ArrayList<Ranking> initRankingList() {
        // Get all the scores from the database into a cursor
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(true, "tb_snakeScores", new String[]{"player", "score", "timeDate"}, null, null, null, null, "score", null);

        // Read the cursor
        ArrayList<Ranking> listScore = new ArrayList<>();
        while (cursor.moveToNext()) {
            // Add the score to the list
            listScore.add(new Ranking(cursor));
        }
        cursor.close();
        db.close();

        return listScore;
    }

    /**
     * Add a ranking to the database
     * @param player The player's name
     * @param score The player's score
     * @return True if the score was added, false otherwise
     */
    public boolean addRanking(String player, int score) {
        return helper.addScore(player, score);
    }

    /**
     * Clear the ranking list
     * @return True if the list was cleared
     */
    public boolean clearScores() {
        if (helper.clearScores()) {
            return true;
        }
        return false;
    }

    public Ranking getLowestRanking() {
        return scoreList.get(scoreList.size() - 1);
    }

    /**
     * Get the ranking list
     * @return The ranking list
     */
    public ArrayList<Ranking> getAllRankings() {
        return scoreList;
    }

    /**
     * Get all rankings up to a certain position
     * @param limit The amount of rankings to get
     * @return The rankings up to the limit
     */
    public ArrayList<Ranking> getRankings(int limit) {
        ArrayList<Ranking> list = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            list.add(scoreList.get(i));
        }
        return list;
    }
}
