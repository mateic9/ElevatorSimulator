package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/elevators")
public class ElevatorController {

    private final ElevatorService elevatorService;

    public ElevatorController(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }

//    @PostMapping("/request")
//    public ResponseEntity<String> handleFloorRequest(@RequestParam int floor) {
//        elevatorService.sendFloorRequest(floor);
//        return ResponseEntity.ok("Request processed");
//    }

    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> handleFloorRequest(@RequestParam int floor) {
        System.out.println("reading from client");
        Elevator elevator = elevatorService.sendFloorRequest(floor);
        if (elevator != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("elevatorId", elevator.getId());
            System.out.println("Id: "+elevator.getId());
            response.put("floor", floor);
            response.put("message", "Request processed");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No elevator available"));
        }
    }

//    @PostMapping("/api/elevators/{elevatorId}/doors/open")
//    public ResponseEntity<Map<String, Object>> openElevatorDoors(@RequestParam String elevatorId) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            String numericPart = elevatorId.substring("elevator".length());
//            int elevatorNumber = Integer.parseInt(numericPart);
//             System.out.println("open doors");
//            System.out.println("open doors");
//            elevatorService.openDoors(elevatorNumber); // Assume this method handles the logic
//            response.put("open doors", true);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            // Handle any exceptions (e.g., NumberFormatException, door opening failure)
//            response.put("open doors", false);
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    @PostMapping("/{elevatorId}/doors/open")
    public ResponseEntity<Map<String, Object>> openElevatorDoors(@PathVariable String elevatorId) {
        Map<String, Object> response = new HashMap<>();
        try {
            String numericPart = elevatorId.substring("elevator".length());
            System.out.println("open doors");
            int elevatorNumber = Integer.parseInt(numericPart);
            System.out.println("Opening doors for elevator " + elevatorNumber);
            elevatorService.openDoors(elevatorNumber); // Handle the logic
            response.put("open doors", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exceptions, such as NumberFormatException or door opening failure
            System.out.println("Failed to open doors: " + e.getMessage());
            response.put("open doors", false);
            return ResponseEntity.badRequest().body(response);
        }
    }



    @PostMapping("/{elevatorId}/doors/close")
    public ResponseEntity<Map<String, Object>> closeElevatorDoors(@PathVariable String elevatorId) {
        Map<String, Object> response = new HashMap<>();
        try {
            String numericPart = elevatorId.substring("elevator".length());
            System.out.println("close doors");
            int elevatorNumber = Integer.parseInt(numericPart);
            System.out.println("Closing doors for elevator " + elevatorNumber);
            elevatorService.closeDoors(elevatorNumber); // Handle the logic
            response.put("close doors", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exceptions, such as NumberFormatException or door opening failure
            System.out.println("Failed to close doors: " + e.getMessage());
            response.put("close doors", false);
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/number")
    public ResponseEntity<String> updateNumberOfElevators(@RequestBody Map<String, Integer> data) {
        if (!data.containsKey("number")) {
            return ResponseEntity.badRequest().body("Error: 'number' key is missing in the payload.");
        }

        int number = data.get("number");
        System.out.println("Server received and processed number of elevators: " + number);
        elevatorService.getElevatorHandler().setNumElevators(number);
        // Here you might add logic to update some state in your service or database
        // For now, we'll just return a confirmation message.
        return ResponseEntity.ok("Server updated number of elevators to: " + number);
    }

//    @GetMapping("/{elevatorId}/state")
//    public ResponseEntity<String> getElevatorState(@PathVariable int elevatorId) {
//       String state = elevatorService.getElevatorState(elevatorId);
//        return ResponseEntity.ok(state);
//    }
}

//package com.example.demo;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/elevators")
//public class ElevatorController {
//
//    private final ElevatorService elevatorService;
//
//    public ElevatorController(ElevatorService elevatorService) {
//        this.elevatorService = elevatorService;
//    }
//
////    @PostMapping("/request")
////    public ResponseEntity<String> handleFloorRequest(@RequestBody FloorRequest request) {
////        elevatorService.sendFloorRequest(request.getFloor());
////        return ResponseEntity.ok("Request processed");
////    }
//
//    @PostMapping("/request")
//    public ResponseEntity<String> handleFloorRequest(@RequestParam int floor) {
//        elevatorService.sendFloorRequest(floor);
//        return ResponseEntity.ok("Request processed");
//    }
//
//    @PostMapping("/{elevatorId}/doors/open")
//    public ResponseEntity<String> openElevatorDoors(@PathVariable int elevatorId) {
//        elevatorService.openDoors(elevatorId);
//        return ResponseEntity.ok("Doors opened");
//    }
//
//    @PostMapping("/{elevatorId}/doors/close")
//    public ResponseEntity<String> closeElevatorDoors(@PathVariable int elevatorId) {
//        elevatorService.closeDoors(elevatorId);
//        return ResponseEntity.ok("Doors closed");
//    }
//
//
////    @GetMapping("/{elevatorId}/state")
////    public ResponseEntity<ElevatorStatus> getElevatorState(@PathVariable int elevatorId) {
////        ElevatorStatus status = elevatorService.getElevatorState(elevatorId);
////        return ResponseEntity.ok(status);
////    }
//}