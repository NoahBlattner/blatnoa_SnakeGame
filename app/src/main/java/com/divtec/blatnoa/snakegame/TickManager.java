package com.divtec.blatnoa.snakegame;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TickManager implements Runnable {

    private static TickManager instance;

    private ArrayList<PhysicsMarble> tickObjects = new ArrayList<>();

    long lastTime;
    boolean running;

    private TickManager() {

    }

    public static TickManager getTickManager() {
        if (instance == null) {
            TickManager tickManager = new TickManager();
            instance = tickManager;
            return tickManager;
        }

        return instance;
    }

    public void addTickObject(PhysicsMarble newTickObject) {
        tickObjects.add(newTickObject);
    }

    public void start() {
        lastTime = System.currentTimeMillis();

        running = true;

        Thread tickThread = new Thread(this);
        tickThread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while(running) {
            long currentTime = System.currentTimeMillis();
            long deltaMS = currentTime - lastTime;

            for (PhysicsMarble current : tickObjects) {
                current.tick(deltaMS);
            }

            lastTime = currentTime;

            try {
                TimeUnit.MILLISECONDS.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
