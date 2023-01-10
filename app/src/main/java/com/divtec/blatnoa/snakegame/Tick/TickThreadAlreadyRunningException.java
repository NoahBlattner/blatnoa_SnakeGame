package com.divtec.blatnoa.snakegame.Tick;

public class TickThreadAlreadyRunningException extends RuntimeException {
    public TickThreadAlreadyRunningException( ) {
        super("Tick thread already running");
    }
}