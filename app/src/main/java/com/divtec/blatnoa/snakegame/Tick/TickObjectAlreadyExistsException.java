package com.divtec.blatnoa.snakegame.Tick;

public class TickObjectAlreadyExistsException extends RuntimeException {
    public TickObjectAlreadyExistsException() {
        super("TickObject already exists in the tick object stack");
    }
}
