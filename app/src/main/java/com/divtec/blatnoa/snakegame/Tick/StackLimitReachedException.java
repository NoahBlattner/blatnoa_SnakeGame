package com.divtec.blatnoa.snakegame.Tick;

public class StackLimitReachedException extends RuntimeException {
    public StackLimitReachedException( ) {
        super("Reached maximum number of tick objects");
    }
}

