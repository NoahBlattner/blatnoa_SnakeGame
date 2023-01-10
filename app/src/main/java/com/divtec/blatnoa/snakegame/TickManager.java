package com.divtec.blatnoa.snakegame;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TickManager implements Runnable {

    private final int MAX_TICK_OBJECTS = 15;

    private static TickManager instance;

    private ArrayList<PhysicsMarble> tickObjects = new ArrayList<>();

    long lastTime;
    boolean running;

    /**
     * Private constructor to allow singleton
     */
    private TickManager() {
        instance = this;
    }

    /**
     * Get the tick manager from singleton. If no instance exists, create a new one
     * @return The instance of the tick manager
     */
    public static TickManager getTickManager() {
        if (instance == null) {
            new TickManager();
            return instance;
        }

        return instance;
    }

    /**
     * Whether or not the tick object stack can accept another tick object
     * @return True if a tick object can be added, false otherwise
     */
    public boolean canAddTickObject() {
        return tickObjects.size() < MAX_TICK_OBJECTS;
    }

    /**
     * Adds a tick object to the tick object stack
     * @param newTickObject The tick object to push to the stack
     * @return True if the object was added to the stack, false otherwise
     */
    public boolean addTickObject(PhysicsMarble newTickObject) {
        if (canAddTickObject()) {
            tickObjects.add(newTickObject);
            return true;
        }
        return false;
    }

    /**
     * Starts the tick thread
     */
    public void start() {
        lastTime = System.currentTimeMillis();

        running = true;

        Thread tickThread = new Thread(this);
        tickThread.start();
    }

    /**
     * Stops the tick thread
     */
    public void stop() {
        running = false;
    }

    /**
     * The runnable for the tick thread
     */
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
