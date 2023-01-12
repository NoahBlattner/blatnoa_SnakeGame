package com.divtec.blatnoa.snakegame.Physics;

public class DuplicateColliderException extends RuntimeException {
    public DuplicateColliderException() {
        super("A collider is already bound to this view");
    }
}
