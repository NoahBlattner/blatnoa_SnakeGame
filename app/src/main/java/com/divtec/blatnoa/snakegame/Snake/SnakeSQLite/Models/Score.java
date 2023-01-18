package com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models;

import android.database.Cursor;

public class Score {

    private String player;
    private int score;

    public Score(Cursor cursor) {
        player = cursor.getString(cursor.getColumnIndex("player"));
        score = cursor.getInt(cursor.getColumnIndex("score"));
    }

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }
}
