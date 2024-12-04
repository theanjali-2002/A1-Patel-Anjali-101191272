package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        command = command.replaceAll("\"", "").trim(); // Remove any quotes or spaces
        System.out.println("Received command: " + command);
        ScannerSingleton.getInstance().setInput(command);
    }

    @GetMapping("/output")
    public String getGameOutput() {
        return OutputRedirector.getOutput(); // Fetch game output to send to the frontend
    }

    @GetMapping("/start")
    public ResponseEntity<String> startGame() {
        try {
            gameService.startGame(null, null, null);
            return ResponseEntity.ok("Game started successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/state")
    public Map<String, Object> getGameState() {
        return gameService.getGameState(); // Return the current game state
    }

    @PostMapping("/rig")
    public ResponseEntity<String> rigGame(@RequestBody Map<String, Object> rigData) {
        List<Card> riggedAdventureDeck = parseDeck((List<Map<String, Object>>) rigData.get("adventureDeck"));
        List<Card> riggedEventDeck = parseDeck((List<Map<String, Object>>) rigData.get("eventDeck"));
        Map<String, List<Card>> riggedHands = parseHands((Map<String, List<Map<String, Object>>>) rigData.get("hands"));

        gameService.rigDecksForGame(riggedEventDeck, riggedAdventureDeck);
        riggedHands.forEach((player, cards) -> gameService.rigHandsForPlayers(cards, player));

        return ResponseEntity.ok("Game rigged successfully");
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




    @PostMapping("/scenario1")
    public ResponseEntity<String> rigScenario1() {
        System.out.println("rigScenario1 method called");

        // Ensure game is fully initialized
        if (gameService.getGame() == null || gameService.getGame().getPlayers().isEmpty()) {
            return ResponseEntity.status(400).body("Error: Game or players are not initialized.");
        }

        List<Card> testCardsP1 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("B15", "H", 15, "Weapon"),
                new Card("B15", "D", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon")
        ));

        List<Card> testCardsP3 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon")
        ));

        List<Card> testCardsP4 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F40", "F", 40, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));

        List<Card> testCardsP2 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));

        List<Card> eventDeckList = List.of(
                new Card("Q4", "Q", 4, "Quest")
        );

        List<Card> adventureDeckList = List.of(
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon")
        );

        gameService.rigHandsForPlayers(testCardsP1, "P1");
        gameService.rigHandsForPlayers(testCardsP2, "P2");
        gameService.rigHandsForPlayers(testCardsP3, "P3");
        gameService.rigHandsForPlayers(testCardsP4, "P4");
        gameService.rigDecksForGame(eventDeckList, adventureDeckList);

        System.out.println("rigScenario1 method DONE--------");

        return ResponseEntity.ok("Scenario 1 rigged successfully");
    }


}
