package com.divtec.blatnoa.snakegame.Snake.SnakeSQLite.Models;

public class InvalidRankingCursor extends RuntimeException {

    public InvalidRankingCursor() {
        super("The ranking cursor is invalid. It must contain the following columns: player, score, timeDate");
    }
}
