package com.divtec.blatnoa.snakegame;

public class StackLimitReachedException extends RuntimeException {
    public StackLimitReachedException( ) {
        super("Reached maximum number of tick objects");
    }
}

