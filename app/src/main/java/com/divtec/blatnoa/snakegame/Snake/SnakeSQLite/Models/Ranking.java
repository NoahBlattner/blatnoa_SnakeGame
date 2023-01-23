package com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models;

import android.database.Cursor;

import androidx.annotation.NonNull;

public class Ranking {

    private String player;
    private int score;
    private String timeDate;

    /**
     * Constructor
     * @param cursor Cursor containing the ranking data
     */
    public Ranking(Cursor cursor) {

        try {
            player = cursor.getString(cursor.getColumnIndexOrThrow("player"));
            score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
            timeDate = cursor.getString(cursor.getColumnIndexOrThrow("timeDate"));
        } catch (IllegalArgumentException e) {
            throw new InvalidRankingCursor();
        }
    }

    /**
     * Get the player's name
     * @return The player's name
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Get the player's score
     * @return The player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Get the time and date of the ranking
     * @return The time and date of the ranking
     */
    public String getTimeDate() {
        return timeDate;
    }

    @NonNull
    @Override
    public String toString() {
        return player + " : " + score + " : " + timeDate;
    }
}
