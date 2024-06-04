package com.example.demo;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ElevatorService {


    private ElevatorHandler elevatorHandler;
    private ExecutorService elevatorExecutor;

    @PostConstruct
    public void init() {
        int numElevators = 5; // You can configure this number as needed
        this.elevatorHandler = new ElevatorHandler(numElevators);
        this.elevatorExecutor = Executors.newFixedThreadPool(numElevators);
        // Start processing requests
        elevatorHandler.getElevators().forEach(elevator -> elevatorExecutor.execute(elevator));
    }

    public synchronized Elevator sendFloorRequest(int floor) {
        return elevatorHandler.addRequest(floor);
    }

    public synchronized void openDoors(int elevatorId) {
        // Assuming openDoors is a new method that needs to be added to Elevator class
        elevatorHandler.getElevators().stream()
                .filter(elevator -> elevator.getId() == elevatorId)
                .findFirst()
                .ifPresent(elevator -> {
                    // Logic to open doors; assume a method like elevator.openDoors() exists
                });
    }

    public synchronized void closeDoors(int elevatorId) {
        // Assuming closeDoors is a new method that needs to be added to Elevator class
        elevatorHandler.getElevators().stream()
                .filter(elevator -> elevator.getId() == elevatorId)
                .findFirst()
                .ifPresent(elevator -> {
                    // Logic to close doors; assume a method like elevator.closeDoors() exists
                });
    }

    public ElevatorHandler getElevatorHandler() {
        return elevatorHandler;
    }

//     Uncomment and implement this if needed
//     public String getElevatorState(int elevatorId) {
//         return elevatorHandler.getElevators().stream()
//                 .filter(elevator -> elevator.getId() == elevatorId)
//                 .map(Elevator::getStatus)
//                 .findFirst()
//                 .orElse(null);
//     }
}
