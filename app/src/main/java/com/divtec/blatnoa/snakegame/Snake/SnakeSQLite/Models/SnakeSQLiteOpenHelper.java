package com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SnakeSQLiteOpenHelper extends SQLiteOpenHelper {
    static String DB_NAME = "SpeedQuiz.db";
    static int DB_VERSION = 1;

    public SnakeSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tb_snakeScores(idSnakeScore INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, score INTEGER);");
    }

    /**
     * Add a score to the database
     * @param score The score to add
     * @return
     */
    public boolean addScore(String player, int score) {
        // If the row count is 10
        if (getRowCount() >= 10) {
            // Delete the lowest score
            deleteLowestScore();
        }

        try {
            // Insert the new score
            getWritableDatabase().execSQL("INSERT INTO tb_snakeScores(score) VALUES(" + player + ", " + score + ");");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete the lowest score
     */
    private void deleteLowestScore() {
        // Get the id of the lowest score
        Cursor cursor = getReadableDatabase().rawQuery("SELECT idSnakeScore FROM tb_snakeScores ORDER BY score ASC LIMIT 1;", null);
        int lowestScoreId = cursor.getInt(0);
        cursor.close();

        // Delete the lowest score
        // Cant use score = MIN because it might delete multiple rows
        getWritableDatabase().execSQL("DELETE FROM tb_snakeScores WHERE idSnakeScore = " + lowestScoreId + ";");
    }

    /**
     * Remove all the scores from the database
     * @return True if the scores were removed
     */
    public boolean clearScores() {
        try {
            getWritableDatabase().execSQL("DELETE FROM tb_snakeScores;");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the row count
     * @return The row count
     */
    private int getRowCount() {
        return (int) getReadableDatabase().compileStatement("SELECT COUNT(*) FROM tb_snakeScores;").simpleQueryForLong();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}