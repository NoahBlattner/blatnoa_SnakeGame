package com.divtec.blatnoa.snakegame.Tick;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TickManager implements Runnable {

    private enum ThreadState {
        UNINITIALIZED,
        RUNNING,
        PAUSED,
        STOPPED
    }

    private final int MAX_TICK_OBJECTS = 300;
    
    private ThreadState threadState = ThreadState.UNINITIALIZED;

    private static TickManager instance;
    private static Thread tickThread;

    private ArrayList<Tickable> tickables = new ArrayList<>();

    long lastTime;

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
        return tickables.size() < MAX_TICK_OBJECTS;
    }

    /**
     * Adds a tick object to the tick object stack
     * @param newTickObject The tick object to push to the stack
     */
    public void addTickObject(Tickable newTickObject) {
        if (!canAddTickObject()) {
            throw new StackLimitReachedException();
        }
        if (tickables.contains(newTickObject)) {
            throw new TickObjectAlreadyExistsException();
        }

        tickables.add(newTickObject);
    }

    /**
     * Starts the tick thread
     */
    public void start() {
        if (tickThread != null) {
            throw new TickThreadAlreadyRunningException();
        }

        if (threadState == ThreadState.UNINITIALIZED || threadState == ThreadState.STOPPED) {
            threadState = ThreadState.RUNNING;

            lastTime = System.currentTimeMillis();

            tickThread = new Thread(this);
            tickThread.start();
        } else if (threadState == ThreadState.PAUSED) {
            resume();
        }
    }

    public void resume() {
        if (threadState == ThreadState.PAUSED) {
            lastTime = System.currentTimeMillis();
            threadState = ThreadState.RUNNING;
        }
    }

    /**
     * Stops the tick thread
     */
    public void pause() {
        if (threadState == ThreadState.RUNNING) {
            threadState = ThreadState.PAUSED;
        }
    }

    public void stop() {
        if (threadState == ThreadState.RUNNING || threadState == ThreadState.PAUSED) {
            threadState = ThreadState.STOPPED;
            tickThread = null;
            tickables.clear();
        }
    }

    /**
     * The runnable for the tick thread
     */
    @Override
    public void run() {
        while (threadState == ThreadState.RUNNING
                || threadState == ThreadState.PAUSED) {

            while (threadState == ThreadState.RUNNING) {
                long currentTime = System.currentTimeMillis();
                long deltaMS = currentTime - lastTime;

                // Copy the tickables array to prevent concurrent modification
                ArrayList<Tickable> tickablesCopy = new ArrayList<>(tickables);
                for (Tickable current : tickablesCopy) {
                    current.tick(deltaMS);
                }

                lastTime = currentTime;

                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // If the thread is paused, wait for it to be resumed
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
