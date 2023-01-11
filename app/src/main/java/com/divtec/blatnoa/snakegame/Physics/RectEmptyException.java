package com.divtec.blatnoa.snakegame.Physics;

public class RectEmptyException extends RuntimeException {
    public RectEmptyException() {
        super("The rect of a image view bound to a collider cannot be empty");
    }
}
