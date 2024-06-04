package com.example.demo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Elevator implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Elevator.class.getName());
    private int id;
    private int currentFloor;
    private Direction direction;
    public static final int MAX_FLOOR = 10;
    private static ConcurrentHashMap<Integer, Boolean> requests = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private volatile boolean running = true;
    private ExecutorService executor;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.NONE;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void addRequest(int floor) {
        LOGGER.info("Adding request for floor: " + floor);
        if (floor > MAX_FLOOR || floor < 0) {
            LOGGER.warning("Invalid floor request: " + floor);
            return;
        }

        requests.put(floor, true);
        LOGGER.info("Current requests: " + requests);

        synchronized (lock) {
            if (direction == Direction.NONE) {
                direction = (floor > currentFloor) ? Direction.UP : Direction.DOWN;
            }
            lock.notifyAll(); // Notify the processing thread of a new request
        }
    }

    public void processNextFloor() {
        try {
            while (running) {
                synchronized (lock) {
                    while (!hasPendingRequests()) {
                        LOGGER.info("No pending requests. Waiting...");
                        lock.wait(); // Wait for new requests
                    }

                    LOGGER.info("Current direction: " + direction);

                    Integer nextFloor = findNextFloor();
                    if (nextFloor != null) {
                        LOGGER.info("Next floor to process: " + nextFloor);
                        moveToFloor(nextFloor);
                    }

                    updateDirection();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.severe("Elevator " + id + ": Thread interrupted: " + e.getMessage());
        }
    }

    private boolean hasPendingRequests() {
        return !requests.isEmpty();
    }

    private Integer findNextFloor() {
        if (direction == Direction.UP) {
            for (int i = currentFloor + 1; i <= MAX_FLOOR; i++) {
                if (requests.getOrDefault(i, false)) {
                    return i;
                }
            }
        } else if (direction == Direction.DOWN) {
            for (int i = currentFloor - 1; i >= 0; i--) {
                if (requests.getOrDefault(i, false)) {
                    return i;
                }
            }
        }
        return null;
    }

    private void moveToFloor(int targetFloor) throws InterruptedException {
        while (currentFloor != targetFloor) {
            synchronized (lock) {
                if (currentFloor < targetFloor) {
                    currentFloor++;
                } else {
                    currentFloor--;
                }

                LOGGER.info("--> Elevator " + id + ": Moving to floor " + currentFloor);
                Thread.sleep(50);

                if (requests.getOrDefault(currentFloor, false)) {
                    stopAtCurrentFloor();
                }

                Integer nextFloor = findNextFloor();
                if (nextFloor != null && !nextFloor.equals(targetFloor)) {
                    targetFloor = nextFloor;
                    LOGGER.info("Re-evaluated next floor to process: " + targetFloor);
                }
            }
        }
        stopAtCurrentFloor();
    }

    private void stopAtCurrentFloor() throws InterruptedException {
        LOGGER.info("--> Elevator " + id + ": Stopping at floor " + currentFloor);
        requests.remove(currentFloor);
        LOGGER.info("After stopping, requests: " + requests);
        Thread.sleep(2000);
    }

    private void updateDirection() {
        synchronized (lock) {
            if (!hasPendingRequests()) {
                direction = Direction.NONE;
            } else if (direction == Direction.UP && !hasUpwardRequests()) {
                direction = Direction.DOWN;
            } else if (direction == Direction.DOWN && !hasDownwardRequests()) {
                direction = Direction.UP;
            }

            LOGGER.info("Updated direction: " + direction);
        }
    }

    private boolean hasUpwardRequests() {
        for (int i = currentFloor + 1; i <= MAX_FLOOR; i++) {
            if (requests.getOrDefault(i, false)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDownwardRequests() {
        for (int i = currentFloor - 1; i >= 0; i--) {
            if (requests.getOrDefault(i, false)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMoving() {
        return direction != Direction.NONE;
    }

    public void stop() {
        synchronized (lock) {
            running = false;
            lock.notifyAll();
        }
        executor.shutdown();
    }

    @Override
    public void run() {
        processNextFloor();
    }

    public int getRequestsSize() {
        return requests.size();
    }

    public Direction getDirection() {
        return direction;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public static ConcurrentHashMap<Integer, Boolean> getRequests() {
        return requests;
    }

    public static void setRequests(ConcurrentHashMap<Integer, Boolean> requests) {
        Elevator.requests = requests;
    }
}

