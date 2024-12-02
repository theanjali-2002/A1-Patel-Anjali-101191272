package org.example;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private boolean isGameInitialized = false;
    private final GameService gameService;

    public GameController() {
        this.gameService = new GameService(); // Initialize GameService
    }

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
        new Thread(() -> gameService.startGame()).start(); // Start the game in a separate thread
        return OutputRedirector.getOutput(); // Return the initial output
    }
}
