package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

@RestController
@RequestMapping("/api/game")
//@SessionAttributes("gameService")
public class GameController {

//    private GameService gameService;
//
//    public GameController() {
//        this.gameService = new GameService();
//    }

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/input")
    public void sendCommand(@RequestBody String command) {
        System.out.println("DEBUG [GameController] Received input: " + command);
        command = command.replaceAll("\"", "").trim(); // Remove any quotes or spaces
        System.out.println("Received command: " + command);
        ScannerSingleton.getInstance().setInput(command);
        System.out.println("DEBUG [GameController] Input processed");
    }

    @GetMapping("/output")
    public String getGameOutput() {
        return OutputRedirector.getOutput(); // Fetch game output to send to the frontend
    }

    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestBody(required = false) Map<String, Object> rigData, HttpServletRequest request) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        System.out.println("[" + timestamp + "] /start endpoint called");

        if (gameService.getGame() != null) {
            return ResponseEntity.badRequest().body("Game already running");
        }

        // Add these debug statements here
        System.out.println("DEBUG [GameController] Request Content-Type: " + request.getContentType());
        System.out.println("DEBUG [GameController] Received rigData: " + (rigData != null ? "not null" : "null"));

        try {
            if (rigData != null) {
                System.out.println("DEBUG [GameController] rigData content: " + new ObjectMapper().writeValueAsString(rigData));
                // Parse the rigged data
                List<Card> adventureDeck = parseDeck((List<Map<String, Object>>) rigData.get("adventureDeck"));
                List<Card> eventDeck = parseDeck((List<Map<String, Object>>) rigData.get("eventDeck"));
                Map<String, List<Card>> hands = parseHands((Map<String, List<Map<String, Object>>>) rigData.get("hands"));

                System.out.println("DEBUG [GameController] Parsed Adventure Deck size: " + adventureDeck.size());
                System.out.println("DEBUG [GameController] Parsed Event Deck size: " + eventDeck.size());
                System.out.println("DEBUG [GameController] Parsed Hands size: " + hands.size());

                // Start the game with rigged data
                gameService.startGame(adventureDeck, eventDeck, hands);
            } else {
                // Start the game with default data
                gameService.startGame(null, null, null);
            }

            // Return the game ID in the response
            return ResponseEntity.ok("Game started successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/state")
    public Map<String, Object> getGameState() {
        return gameService.getGameState(); // Return the current game state
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetGame() {
        // Stop current game and clean up
        gameService.cleanup();

        // Generate new game ID
        gameService.setGameId(UUID.randomUUID().toString());

        // Log the new game instance
        System.out.println("=== NEW GAME INSTANCE ===");
        System.out.println("GameService reset with new ID: " + gameService.getGameId());
        System.out.println("Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        System.out.println("======================");

        return ResponseEntity.ok("Game reset with new ID: " + gameService.getGameId());
    }

    private List<Card> parseDeck(List<Map<String, Object>> rawDeck) {
        return rawDeck.stream()
                .map(data -> new Card(
                        (String) data.get("cardName"),
                        (String) data.get("type"),
                        (Integer) data.get("value"),
                        (String) data.get("category")
                ))
                .collect(Collectors.toList());
    }

    private Map<String, List<Card>> parseHands(Map<String, List<Map<String, Object>>> rawHands) {
        return rawHands.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> parseDeck(entry.getValue())
                ));
    }

}
