package org.example;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private boolean isGameInitialized = false;

    @PostMapping("/input")
    public void sendCommand(@RequestBody String command) {
        command = command.replaceAll("\"", "").trim(); // Remove any quotes or spaces
        System.out.println("Received command: " + command);
        ScannerSingleton.getInstance().setInput(command);
    }

    @GetMapping("/output")
    public String getGameOutput() {
        return OutputRedirector.getOutput(); // Fetch game output to send to the frontend
    }

    @GetMapping("/initialize")
    public String initializeGame() {
        if (!isGameInitialized) {
            // Step 1: Initialize game logic
            UserInterface userInterface = new UserInterface();
            userInterface.displayGameStartMessage(true);

            Game game = new Game();
            game.initializeGameEnvironment();
            game.initializePlayers();
            game.distributeAdventureCards();

            isGameInitialized = true; // Mark the game as initialized
        }
        return OutputRedirector.getOutput(); // Return the initial output to display in the browser
    }
}
