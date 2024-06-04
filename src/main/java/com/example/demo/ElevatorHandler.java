

package com.example.demo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorHandler {
    private List<Elevator> elevators;
    private ExecutorService elevatorExecutor;
    private int numElevators=0;
    private int currentElevatorIndex = 0;
    private boolean configurationPause=false;
    private final ReentrantLock lock = new ReentrantLock();

    public ElevatorHandler(int numElevators) {
        this.numElevators=numElevators;
        elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i + 1));
        }
        this.elevatorExecutor = Executors.newFixedThreadPool(numElevators);
    }

    public Elevator addRequest(int floor) {
        while (configurationPause);
        Elevator selectedElevator = selectElevator(floor);
        if (selectedElevator != null) {
            selectedElevator.addRequest(floor);
            elevatorExecutor.execute(selectedElevator);
            return selectedElevator;
        }
        return null;
    }

    private Elevator selectElevator(int requestFloor) {
        for (int i = 0; i < numElevators; i++) {
            while (configurationPause);
            Elevator elevator = elevators.get(currentElevatorIndex);
            System.out.println("Checking elevator " + elevator.getId() + " with " + elevator.getRequestsSize() + " requests");
            currentElevatorIndex = (currentElevatorIndex + 1) % numElevators;
            if (!elevator.isMoving()) {
                System.out.println("Selected elevator " + elevator.getId());
                return elevator;
            }
        }

        Elevator selectedElevator = elevators.stream()
                .min(Comparator.comparingLong(e -> calculateWaitTime(e, requestFloor)))
                .orElse(null);
        if (selectedElevator != null) {
            System.out.println("No inactive elevators. Selected elevator " + selectedElevator.getId() + " based on smallest distance");
        }
        System.out.println("Current elevator index after selection: " + currentElevatorIndex);
        return selectedElevator;
    }

    private long calculateWaitTime(Elevator elevator, int requestFloor) {
        int distance = Math.abs(elevator.getCurrentFloor() - requestFloor);
        long directionPenalty = elevator.getDirection() != Direction.NONE && (requestFloor > elevator.getCurrentFloor()) != (elevator.getDirection() == Direction.UP) ? 10 : 0;
        return distance + directionPenalty;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void processRequests() {
        elevators.forEach(elevator -> elevatorExecutor.execute(elevator));
    }
    public void setNumElevators(int number){
     try {
         setConfigurationPause(true);
         Thread.sleep(1000);
         numElevators = number;
         setConfigurationPause(false);
     }
     catch(Exception e){
         System.out.println(e.getMessage());
     }

    }
    public boolean isConfigurationPause() {
        return configurationPause;
    }

    public void setConfigurationPause(boolean configurationPause) {
        this.configurationPause = configurationPause;
    }

    public static void main(String[] args) {
        ElevatorHandler handler = new ElevatorHandler(3);  // Assume 3 elevators for simplicity
        handler.processRequests();  // Start all elevators

        Scanner scanner = new Scanner(System.in);
        System.out.println("Elevator controller started.");
        System.out.println("Enter floor requests (negative number to stop):");

        while (true) {
            System.out.print("Request floor: ");
            int requestedFloor = scanner.nextInt();
            if (requestedFloor < 0) {
                break;
            }
            handler.addRequest(requestedFloor);
        }

        System.out.println("Stopping elevator controller...");
        handler.elevatorExecutor.shutdown();
        System.out.println("Elevator controller stopped.");
        scanner.close();
    }
}

//package com.example.demo;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Scanner;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class ElevatorHandler {
//    private List<Elevator> elevators;
//    private ExecutorService elevatorExecutor;
//
//    private int currentElevatorIndex = 0;
//
//    private final ReentrantLock lock = new ReentrantLock();
//
//    public ElevatorHandler(int numElevators) {
//        elevators = new ArrayList<>();
//        for (int i = 0; i < numElevators; i++) {
//            elevators.add(new Elevator(i + 1));
//        }
//        this.elevatorExecutor = Executors.newFixedThreadPool(numElevators);
//    }
//
//
//
//    public void addRequest(int floor) {
//        Elevator selectedElevator = selectElevator(floor);
//        if (selectedElevator != null) {
//            selectedElevator.addRequest(floor);
//            elevatorExecutor.execute(selectedElevator);
//        }
//    }
//
//    private Elevator selectElevator(int requestFloor) {
//        for (int i = 0; i < elevators.size(); i++) {
//            Elevator elevator = elevators.get(currentElevatorIndex);
//            System.out.println("Checking elevator " + elevator.getId() + " with " + elevator.getRequestsSize() + " requests");
//            currentElevatorIndex = (currentElevatorIndex + 1) % elevators.size();
//            if (!elevator.isMoving()) {
//                System.out.println("Selected elevator " + elevator.getId());
//                return elevator;
//            }
//        }
//
//        Elevator selectedElevator = elevators.stream()
//                .min(Comparator.comparingLong(e -> calculateWaitTime(e, requestFloor)))
//                .orElse(null);
//        if (selectedElevator != null) {
//            System.out.println("No inactive elevators. Selected elevator " + selectedElevator.getId() + " based on smallest distance");
//        }
//        System.out.println("Current elevator index after selection: " + currentElevatorIndex);
//        return selectedElevator;
//    }
//    private long calculateWaitTime(Elevator elevator, int requestFloor) {
//        int distance = Math.abs(elevator.getCurrentFloor() - requestFloor);
//        long directionPenalty = elevator.getDirection() != Direction.NONE && (requestFloor > elevator.getCurrentFloor()) != (elevator.getDirection() == Direction.UP) ? 10 : 0;
//        return distance + directionPenalty;
//    }
//
//    public List<Elevator> getElevators() {
//        return elevators;
//    }
//    public void processRequests() {
//        elevators.forEach(elevator -> elevatorExecutor.execute(elevator));
//    }
//    public static void main(String[] args) {
//        ElevatorController controller = new ElevatorController(3);
//        processRequests();
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Elevator controller started.");
//        System.out.println("Enter floor requests (negative number to stop):");
//
//        while (true) {
//            System.out.print("Request floor: ");
//            int requestedFloor = scanner.nextInt();
//            if (requestedFloor < 0) {
//                break;
//            }
//            addRequest(requestedFloor);
//        }
//
//        System.out.println("Stopping elevator controller...");
//        controller.elevatorExecutor.shutdown();
//
//        System.out.println("Elevator controller stopped.");
//        scanner.close();
//    }
//}
//
//
