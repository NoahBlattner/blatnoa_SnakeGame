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
    private boolean wasUpdated = false;

    public RankingManager(Context context) {
        helper = new SnakeSQLiteOpenHelper(context);
        scoreList = getRankingsFromDB();
    }

    /**
     * Load the ranking from the database
     * @return The ranking list
     */
    private ArrayList<Ranking> getRankingsFromDB() {
        // Get all the scores from the database into a cursor
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(false, "tb_snakeScores", new String[]{"player", "score", "timeDate"}, null, null, null, null, "score", null);

        // Read the cursor
        ArrayList<Ranking> listScore = new ArrayList<>();
        while (cursor.moveToNext()) {
            // Add the score to the list
            listScore.add(new Ranking(cursor));
        }
        cursor.close();
        db.close();

        // Sort the list
        listScore.sort((o1, o2) -> o2.getScore() - o1.getScore());

        return listScore;
    }

    /**
     * Add a ranking to the database
     * @param player The player's name
     * @param score The player's score
     * @return True if the score was added, false otherwise
     */
    public boolean addRanking(String player, int score) {
        wasUpdated = true;
        return helper.addScore(player, score);
    }

    /**
     * Clear the ranking list
     * @return True if the list was cleared
     */
    public boolean clearScores() {
        if (helper.clearScores()) {
            scoreList.clear();
            wasUpdated = true;
            return true;
        }
        return false;
    }

    /**
     * Get the lowest ranking
     * @return The lowest ranking if it exists, null otherwise
     */
    public Ranking getLowestRanking() {
        // If any changes were made to the ranking list
        reloadListIfUpdated();

        if (scoreList.size() > 0) {
            // Get the lowest ranking
            return scoreList.get(scoreList.size() - 1);
        }
        return null;
    }

    /**
     * Get the ranking list
     * @return The ranking list
     */
    public ArrayList<Ranking> getAllRankings() {
        reloadListIfUpdated();
        return scoreList;
    }

    /**
     * Get all rankings up to a certain position
     * @param limit The amount of rankings to get
     * @return The rankings up to the limit
     */
    public ArrayList<Ranking> getRankings(int limit) {
        reloadListIfUpdated();

        // If the limit is greater than the list size
        if (limit > scoreList.size()) {
            // Set the limit to the list size
            limit = scoreList.size();
        }

        ArrayList<Ranking> list = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            list.add(scoreList.get(i));
        }
        return list;
    }

    /**
     * Reload the ranking list from the DB if it was updated
     */
    private void reloadListIfUpdated() {
        // If any changes were made to the ranking list
        if (wasUpdated) {
            // Reload the ranking list
            scoreList = getRankingsFromDB();
            wasUpdated = false;
        }
    }
}
