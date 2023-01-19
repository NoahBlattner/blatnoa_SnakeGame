package com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models;

import android.database.Cursor;

import androidx.annotation.NonNull;

public class Score {

    private String player;
    private int score;
    private String timeDate;

    public Score(Cursor cursor) {
        player = cursor.getString(cursor.getColumnIndex("player"));
        score = cursor.getInt(cursor.getColumnIndex("score"));
        timeDate = cursor.getString(cursor.getColumnIndex("timeDate"));
    }

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public String getTimeDate() {
        return timeDate;
    }

    @NonNull
    @Override
    public String toString() {
        return player + " : " + score + " : " + timeDate;
    }
}
