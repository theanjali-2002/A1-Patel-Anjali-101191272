package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumTests {

    private GameService gameService;
    private WebDriver driver;

    //helper method to test number of shields and hand of a player
    private void validatePlayerState(int playerNumber, String expectedShields, String expectedHand) throws InterruptedException {
        Thread.sleep(1000); // Add delay between commands to let the UI process
        WebElement playerShields = driver.findElement(By.id("player" + playerNumber + "-shields"));
        WebElement playerHand = driver.findElement(By.id("player" + playerNumber + "-hand"));

        assertEquals(expectedShields, playerShields.getText(), "Player " + playerNumber + " shields mismatch.");
        assertEquals(expectedHand, playerHand.getText(), "Player " + playerNumber + " hand mismatch.");
    }

    private void validatePlayerCards(int playerNumber, List<String> expectedCards) {
        WebElement playerCards = driver.findElement(By.id("player" + playerNumber + "-cards"));
        String actualCardsText = playerCards.getText();
        
        // Convert the actual cards text into a list of card names
        List<String> actualCards = Arrays.stream(actualCardsText.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        Collections.sort(expectedCards);
        Collections.sort(actualCards);
        
        assertEquals(expectedCards, actualCards, 
            "Player " + playerNumber + " cards mismatch. Expected: " + expectedCards + ", but got: " + actualCards);
    }

    private String generateAttackCommands(int playerNumber, List<String> cardsToPlay) {
        // Get the player's current cards from the browser
        WebElement playerCards = driver.findElement(By.id("player" + playerNumber + "-cards"));
        String cardsText = playerCards.getText();
        List<String> currentCards = Arrays.asList(cardsText.split(", "));
        
        StringBuilder commands = new StringBuilder();
        
        // For each card we want to play, find its index in the current hand
        for (String cardToPlay : cardsToPlay) {
            int cardIndex = -1;
            for (int i = 0; i < currentCards.size(); i++) {
                if (currentCards.get(i).equals(cardToPlay)) {
                    cardIndex = i + 1; // Convert to 1-based index
                    break;
                }
            }
            if (cardIndex != -1) {
                commands.append(cardIndex).append("\n");
            }
        }
        commands.append("q\n");
        
        return commands.toString();
    }

    private String generateSponsorStageCommands(int sponsorNumber, Map<Integer, List<String>> stageCards) {
        try {
            // Wait for and get player cards
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            WebElement playerCards = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("player" + sponsorNumber + "-cards")));
            String cardsText = playerCards.getText();
            List<String> currentCards = new ArrayList<>(Arrays.asList(cardsText.split(", ")));
            
            StringBuilder commands = new StringBuilder();
            for (int stage = 1; stage <= stageCards.size(); stage++) {
                List<String> cardsForStage = stageCards.get(stage);
                List<String> remainingCards = new ArrayList<>(currentCards); // Copy of current cards
                
                for (String cardToPlay : cardsForStage) {
                    int cardIndex = findCardIndex(remainingCards, cardToPlay);
                    if (cardIndex != -1) {
                        commands.append(cardIndex + 1).append("\n");
                        // Remove the card from remaining cards to adjust indices for next selections
                        remainingCards.remove(cardIndex);
                    }
                }
                commands.append("q\n");
                
                // Update currentCards for next stage by removing used cards
                for (String cardPlayed : cardsForStage) {
                    int index = findCardIndex(currentCards, cardPlayed);
                    if (index != -1) {
                        currentCards.remove(index);
                    }
                }
            }
            return commands.toString();
        } catch (Exception e) {
            System.out.println("Error generating sponsor commands: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private int findCardIndex(List<String> cards, String targetCard) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).trim().equals(targetCard.trim())) {
                return i;
            }
        }
        return -1;
    }

    private String generatePlayerParticipationCommands(List<Integer> eligiblePlayers, List<Integer> participatingPlayers) {
        StringBuilder commands = new StringBuilder();
        for (int playerNum : eligiblePlayers) {
            if (participatingPlayers.contains(playerNum)) {
                commands.append("y\n");
            } else {
                commands.append("n\n");
            }
        }
        return commands.toString();
    }

    private String generateTrimHandCommands(int playerNumber, List<String> cardsToDiscard) {
        WebElement playerCards = driver.findElement(By.id("player" + playerNumber + "-cards"));
        String cardsText = playerCards.getText();
        List<String> currentCards = Arrays.asList(cardsText.split(", "));

        StringBuilder commands = new StringBuilder();
        for (String cardToDiscard : cardsToDiscard) {
            int cardIndex = -1;
            for (int i = 0; i < currentCards.size(); i++) {
                if (currentCards.get(i).equals(cardToDiscard)) {
                    cardIndex = i + 1;
                    break;
                }
            }
            if (cardIndex != -1) {
                commands.append(cardIndex).append("\n");
            }
        }
        return commands.toString();
    }

    //helper method for input commands for longer games
    private void executeCommands(WebElement commandInput, String commands) throws InterruptedException {
        Thread.sleep(1000);
        for (String command : commands.split("\n")) {
            commandInput.sendKeys(command);
            commandInput.sendKeys(Keys.RETURN);
            Thread.sleep(2000); // Add delay between commands to let the UI process
        }
    }

    @BeforeEach
    public void setUp() throws IOException {
        System.setProperty("webdriver.chrome.driver", "C:/Users/thean/OneDrive/Desktop/Anjali/Fall24/comp4004/A1-Patel-Anjali-101191272/chromedriver-win64/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080");

        // Wait for initial page load without starting the game
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            //driver.quit();
        }
    }

    private List<Card> getRiggedAdventureDeckScenario1() {
        return Arrays.asList(
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
    }

    private List<Card> getRiggedEventDeckScenario1() {
        return Arrays.asList(
                new Card("Q4", "Q", 4, "Quest")
        );
    }

    private Map<String, List<Card>> getRiggedHandsScenario1() {
        Map<String, List<Card>> riggedHands = new HashMap<>();
        riggedHands.put("P1", Arrays.asList(
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
        riggedHands.put("P2", Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));
        riggedHands.put("P3", Arrays.asList(
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
        riggedHands.put("P4", Arrays.asList(
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

        return riggedHands;
    }

    private void rigGameForScenario1() throws IOException {
        List<Card> adventureDeck = getRiggedAdventureDeckScenario1();
        List<Card> eventDeck = getRiggedEventDeckScenario1();
        Map<String, List<Card>> hands = getRiggedHandsScenario1();

        System.out.println("DEBUG [SeleniumTests] Adventure Deck size: " + adventureDeck.size());
        System.out.println("DEBUG [SeleniumTests] Event Deck size: " + eventDeck.size());
        System.out.println("DEBUG [SeleniumTests] Hands map size: " + hands.size());

        System.out.println("Rigging game, preparing HTTP connection...");
        System.out.println("DEBUG [SeleniumTests] Starting HTTP request...");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/api/game/start").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        System.out.println("Preparing JSON payload...");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        payload.put("adventureDeck", adventureDeck);
        payload.put("eventDeck", eventDeck);
        payload.put("hands", hands);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = objectMapper.writeValueAsBytes(payload);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to rig game scenario. Response code: " + responseCode);
        }

        System.out.println("DEBUG [SeleniumTests] Response Code: " + responseCode);
        System.out.println("Game rigged successfully!");
    }





    @Test
    public void A1_scenario() throws MalformedURLException, ProtocolException, IOException, InterruptedException {
        rigGameForScenario1(); // Rig the game

        WebElement commandInput = driver.findElement(By.id("commandInput"));
        Thread.sleep(1000);

        // Validate initial state
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        String phase1Commands = "e\nn\ny\n"; //p1 draws a card and declines to sponsor, p2 sponsors it
        executeCommands(commandInput, phase1Commands);

        // Sponsor (P2) sets up all quest stages
        Map<Integer, List<String>> stageSetup = new HashMap<>();
        stageSetup.put(1, Arrays.asList("F5", "S10"));
        stageSetup.put(2, Arrays.asList("S10", "F15"));
        stageSetup.put(3, Arrays.asList("F20", "F20"));
        stageSetup.put(4, Arrays.asList("F10", "F20", "E30"));
        String sponsorCommands = generateSponsorStageCommands(2, stageSetup);
        executeCommands(commandInput, sponsorCommands);
        validatePlayerState(2, "0", "3");

        // Players decide to join quest
        String joinQuestCommands = generatePlayerParticipationCommands(
                Arrays.asList(1, 3, 4), // eligible players
                Arrays.asList(1, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinQuestCommands);

        // Players decide to join STAGE 1
        String joinStage1Commands = generatePlayerParticipationCommands(
                Arrays.asList(1, 3, 4), // eligible players
                Arrays.asList(1, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinStage1Commands);


        String phase7Commands = "1\n1\n1\n"; //all 3 players discards one card since they drew one to play the quest
        executeCommands(commandInput, phase7Commands);
        validatePlayerState(1, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 1 attacks
        executeCommands(commandInput, generateAttackCommands(1, Arrays.asList("S10", "D5")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("S10", "D5")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("H10", "D5")));

        // Stage 2 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(1, 3, 4),
                Arrays.asList(1, 3, 4)
        ));
        validatePlayerState(1, "0", "11");
        validatePlayerState(3, "0", "11");
        validatePlayerState(4, "0", "11");

        // Stage 2 attacks
        executeCommands(commandInput, generateAttackCommands(1, Arrays.asList("S10", "H10")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("S10", "B15")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("H10", "B15")));

        validatePlayerCards(1, Arrays.asList("F5", "F10", "F15", "F15", "F30", "H10", "B15", "B15", "L20"));

        // Stage 3 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(3, 4),
                Arrays.asList(3, 4)
        ));
        validatePlayerState(3, "0", "10");
        validatePlayerState(4, "0", "10");

        // Stage 3 attacks
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("S10", "H10", "L20")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("S10", "B15", "L20")));

        // Stage 4 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(3, 4),
                Arrays.asList(3, 4)
        ));
        validatePlayerState(3, "0", "8");
        validatePlayerState(4, "0", "8");

        // Stage 4 attacks
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("B15", "H10", "L20")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("S10", "D5", "L20", "E30")));

        validatePlayerState(2, "0", "16");
        // Final hand trimming for P2
        executeCommands(commandInput, generateTrimHandCommands(2, Arrays.asList("B15", "S10", "L20", "S10")));

        //End of Scenario Assert
        validatePlayerState(2, "0", "12"); //sponsor p2
        validatePlayerState(1, "0", "9");
        validatePlayerState(3, "0", "5");
        validatePlayerState(4, "4", "4");

        //specific hand of each player to be asserted at the end of the scenario
        validatePlayerCards(3, Arrays.asList("F5", "F5", "F15", "F30", "S10"));
        validatePlayerCards(4, Arrays.asList("F15", "F15", "F40", "L20"));

    }




    private List<Card> getRiggedAdventureDeckScenario2() {
        return Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F40", "F", 40, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                //not in game below
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon")
        );
    }

    private List<Card> getRiggedEventDeckScenario2() {
        return Arrays.asList(
                new Card("Q4", "Q", 4, "Quest"),
                new Card("Q3", "Q", 3, "Quest")
        );
    }

    private Map<String, List<Card>> getRiggedHandsScenario2() {
        Map<String, List<Card>> riggedHands = new HashMap<>();
        riggedHands.put("P1", Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("B15", "H", 15, "Weapon"),
                new Card("B15", "D", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon")
        ));
        riggedHands.put("P2", Arrays.asList(
                new Card("F40", "F", 40, "Foe"),
                new Card("F50", "F", 50, "Foe"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));
        riggedHands.put("P3", Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon")
        ));
        riggedHands.put("P4", Arrays.asList(
                new Card("F50", "F", 50, "Foe"),
                new Card("F70", "F", 70, "Foe"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));

        return riggedHands;
    }

    private void rigGameForScenario2() throws IOException {
        List<Card> adventureDeck = getRiggedAdventureDeckScenario2();
        List<Card> eventDeck = getRiggedEventDeckScenario2();
        Map<String, List<Card>> hands = getRiggedHandsScenario2();

        System.out.println("DEBUG [SeleniumTests 2] Adventure Deck size: " + adventureDeck.size());
        System.out.println("DEBUG [SeleniumTests 2] Event Deck size: " + eventDeck.size());
        System.out.println("DEBUG [SeleniumTests 2] Hands map size: " + hands.size());
        System.out.println("Rigging game 2, preparing HTTP connection...");
        System.out.println("DEBUG [SeleniumTests 2] Starting HTTP request...");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/api/game/start").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        System.out.println("Preparing JSON payload 2...");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        payload.put("adventureDeck", adventureDeck);
        payload.put("eventDeck", eventDeck);
        payload.put("hands", hands);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = objectMapper.writeValueAsBytes(payload);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to rig game scenario 2. Response code: " + responseCode);
        }
        System.out.println("DEBUG [SeleniumTests 2] Response Code: " + responseCode);
        System.out.println("Game 2 rigged successfully!");
    }

    @Test
    public void scenario2_2winner_game_2winner_quest() throws IOException, InterruptedException {
        rigGameForScenario2(); // Rig the game
        WebElement commandInput = driver.findElement(By.id("commandInput"));
        Thread.sleep(1000);

        // Validate initial state
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        String phase1Commands = "e\ny\n"; //p1 draws a card and sponsors it
        executeCommands(commandInput, phase1Commands);

        // Sponsor (P1) sets up all quest stages
        Map<Integer, List<String>> stageSetup = new HashMap<>();
        stageSetup.put(1, Arrays.asList("F5"));
        stageSetup.put(2, Arrays.asList("F5", "D5"));
        stageSetup.put(3, Arrays.asList("F10", "H10"));
        stageSetup.put(4, Arrays.asList("F10", "B15"));
        String sponsorCommands = generateSponsorStageCommands(1, stageSetup);
        executeCommands(commandInput, sponsorCommands);
        validatePlayerState(1, "0", "5");

        // Players decide to join quest
        String joinQuestCommands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinQuestCommands);

        // Players decide to join STAGE 1
        String joinStage1Commands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinStage1Commands);

        //trimming hands before quest play
        executeCommands(commandInput, generateTrimHandCommands(2, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(3, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(4, Arrays.asList("F10")));
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 1 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("H10")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("H10")));

        // Stage 2 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 4),
                Arrays.asList(2, 4)
        ));
        validatePlayerState(2, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 2 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("S10")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("S10")));

        // Stage 3 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 4),
                Arrays.asList(2, 4)
        ));
        validatePlayerState(2, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 3 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("S10", "H10")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("S10", "H10")));

        // Stage 4 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 4),
                Arrays.asList(2, 4)
        ));
        validatePlayerState(2, "0", "11");
        validatePlayerState(4, "0", "11");

        // Stage 4 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("S10", "B15")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("S10", "B15")));

        validatePlayerState(1, "0", "16");
        // Final hand trimming for P1
        String phase3Commands = "1\n1\n1\n1\n";
        executeCommands(commandInput, phase3Commands);

        validatePlayerState(1, "0", "12"); //sponsor p1
        validatePlayerState(2, "4", "9");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "4", "9");

        // QUEST 3 ============================>
        String phase2Commands = "r\ne\nn\ny\n"; //p2 draws a card and declines to sponsor, p3 sponsors it
        executeCommands(commandInput, phase2Commands);

        // Sponsor (P3) sets up all quest stages
        Map<Integer, List<String>> stage2Setup = new HashMap<>();
        stage2Setup.put(1, Arrays.asList("F5"));
        stage2Setup.put(2, Arrays.asList("F5", "D5"));
        stage2Setup.put(3, Arrays.asList("F5", "H10"));
        String sponsor2Commands = generateSponsorStageCommands(3, stage2Setup);
        executeCommands(commandInput, sponsor2Commands);
        validatePlayerState(3, "0", "7");

        // Players decide to join quest3
        String joinQuest2Commands = generatePlayerParticipationCommands(
                Arrays.asList(1, 2, 4), // eligible players
                Arrays.asList(2, 4)  // participating players
        );
        executeCommands(commandInput, joinQuest2Commands);

        // Players decide to join STAGE 1
        String joinQ3Stage1Commands = generatePlayerParticipationCommands(
                Arrays.asList(2, 4), // eligible players
                Arrays.asList(2, 4)  // participating players
        );
        executeCommands(commandInput, joinQ3Stage1Commands);

        // Stage 1 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("D5")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("D5")));

        // Stage 2 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 4),
                Arrays.asList(2, 4)
        ));

        // Stage 2 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("B15")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("B15")));

        // Stage 3 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 4),
                Arrays.asList(2, 4)
        ));

        // Stage 3 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("E30")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("E30")));
        Thread.sleep(2000);

        validatePlayerState(3, "0", "15");
        // Final hand trimming for P3
        String phase4Commands = "2\n2\n2\n";
        executeCommands(commandInput, phase4Commands);

        //End of Scenario Assert
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "7", "9");
        validatePlayerState(3, "0", "12"); //SPONSOR P3
        validatePlayerState(4, "7", "9");

        //specific hand of each player to be asserted at the end of the scenario
        validatePlayerCards(1, Arrays.asList("F15", "F15", "F20", "F20", "F20", "F20", "F25", "F25", "F30", "H10", "B15", "L20"));
        validatePlayerCards(2, Arrays.asList("F10", "F15", "F15", "F25", "F30", "F40", "F50", "L20", "L20"));
        validatePlayerCards(3, Arrays.asList("F20", "F40", "D5", "D5", "S10", "H10", "H10", "H10", "H10", "B15", "B15", "L20"));
        validatePlayerCards(4, Arrays.asList("F15", "F15", "F20", "F25", "F30", "F50", "F70", "L20", "L20"));
    }


    private List<Card> getRiggedAdventureDeckScenario3() {
        return Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F40", "F", 40, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("F50", "F", 50, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F40", "F", 40, "Foe"),
                new Card("F50", "F", 50, "Foe"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F35", "F", 35, "Foe"),
                //not in game below
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon")
        );
    }

    private List<Card> getRiggedEventDeckScenario3() {
        return Arrays.asList(
                new Card("Q4", "Q", 4, "Quest"),
                new Card("Plague", "E", -2, "Event"),
                new Card("Prosperity", "E", 2, "Event"),
                new Card("Queen's Favor", "E", 2, "Event"),
                new Card("Q3", "Q", 3, "Quest")
        );
    }

    private Map<String, List<Card>> getRiggedHandsScenario3() {
        Map<String, List<Card>> riggedHands = new HashMap<>();
        riggedHands.put("P1", Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon")
        ));
        riggedHands.put("P2", Arrays.asList(
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));
        riggedHands.put("P3", Arrays.asList(
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));
        riggedHands.put("P4", Arrays.asList(
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("F70", "F", 70, "Foe"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon")
        ));

        return riggedHands;
    }

    private void rigGameForScenario3() throws IOException {
        List<Card> adventureDeck = getRiggedAdventureDeckScenario3();
        List<Card> eventDeck = getRiggedEventDeckScenario3();
        Map<String, List<Card>> hands = getRiggedHandsScenario3();

        System.out.println("DEBUG [SeleniumTests 3] Adventure Deck size: " + adventureDeck.size());
        System.out.println("DEBUG [SeleniumTests 3] Event Deck size: " + eventDeck.size());
        System.out.println("DEBUG [SeleniumTests 3] Hands map size: " + hands.size());
        System.out.println("Rigging game 3, preparing HTTP connection...");
        System.out.println("DEBUG [SeleniumTests 3] Starting HTTP request...");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/api/game/start").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        System.out.println("Preparing JSON payload 3...");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        payload.put("adventureDeck", adventureDeck);
        payload.put("eventDeck", eventDeck);
        payload.put("hands", hands);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = objectMapper.writeValueAsBytes(payload);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to rig game scenario 3. Response code: " + responseCode);
        }
        System.out.println("DEBUG [SeleniumTests 3] Response Code: " + responseCode);
        System.out.println("Game 3 rigged successfully!");
    }



    @Test
    public void scenario3_1winner_game_with_events() throws IOException, InterruptedException {
        rigGameForScenario3(); // Rig the game
        WebElement commandInput = driver.findElement(By.id("commandInput"));
        Thread.sleep(1000);

        // Validate initial state
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        String phase1Commands = "e\ny\n"; //p1 draws a card and sponsors it
        executeCommands(commandInput, phase1Commands);

        // Sponsor (P1) sets up all quest stages
        Map<Integer, List<String>> stageSetup = new HashMap<>();
        stageSetup.put(1, Arrays.asList("F5"));
        stageSetup.put(2, Arrays.asList("F10"));
        stageSetup.put(3, Arrays.asList("F15"));
        stageSetup.put(4, Arrays.asList("F20"));
        String sponsorCommands = generateSponsorStageCommands(1, stageSetup);
        executeCommands(commandInput, sponsorCommands);
        validatePlayerState(1, "0", "8");

        // Players decide to join quest
        String joinQuestCommands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinQuestCommands);

        // Players decide to join STAGE 1
        String joinStage1Commands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinStage1Commands);

        //trimming hands before quest play
        executeCommands(commandInput, generateTrimHandCommands(2, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(3, Arrays.asList("F10")));
        executeCommands(commandInput, generateTrimHandCommands(4, Arrays.asList("F20")));
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 1 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("S10")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("S10")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("S10")));
        validatePlayerState(2, "0", "11");
        validatePlayerState(3, "0", "11");
        validatePlayerState(4, "0", "11");

        // Stage 2 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4),
                Arrays.asList(2, 3, 4)
        ));
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 2 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("H10")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("H10")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("H10")));
        validatePlayerState(2, "0", "11");
        validatePlayerState(3, "0", "11");
        validatePlayerState(4, "0", "11");

        // Stage 3 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4),
                Arrays.asList(2, 3, 4)
        ));
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 3 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("B15")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("B15")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("B15")));
        validatePlayerState(2, "0", "11");
        validatePlayerState(3, "0", "11");
        validatePlayerState(4, "0", "11");

        // Stage 4 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4),
                Arrays.asList(2, 3, 4)
        ));
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 4 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("L20")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("L20")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("L20")));
        validatePlayerState(2, "4", "11");
        validatePlayerState(3, "4", "11");
        validatePlayerState(4, "4", "11");

        validatePlayerState(1, "0", "16");
        // Final hand trimming for P1
        //String phase3Commands = "2\n2\n2\n2\n"; //ELSE 2
        //executeCommands(commandInput, phase3Commands);
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F10")));
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F10")));

        validatePlayerState(1, "0", "12"); //sponsor p1
        validatePlayerState(2, "4", "11");
        validatePlayerState(3, "4", "11");
        validatePlayerState(4, "4", "11");

        // EVENT CARDS NOW ============================>
        String phase2Commands = "r\ne\n"; //P1 RETURNS AND p2 draws a card
        executeCommands(commandInput, phase2Commands);
        validatePlayerState(2, "2", "11"); //LOSES TWO SHIELDS

        String phase3Commands = "r\ne\n"; //P2 RETURNS AND p3 draws a card
        executeCommands(commandInput, phase3Commands);
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F10")));
        validatePlayerState(1, "0", "12");

        executeCommands(commandInput, generateTrimHandCommands(2, Arrays.asList("F5")));
        validatePlayerState(2, "2", "12");

        executeCommands(commandInput, generateTrimHandCommands(3, Arrays.asList("F5")));
        validatePlayerState(3, "4", "12");

        executeCommands(commandInput, generateTrimHandCommands(4, Arrays.asList("F20")));
        validatePlayerState(4, "4", "12");

        String phase4Commands = "r\ne\n"; //P3 RETURNS AND p4 draws a card
        executeCommands(commandInput, phase4Commands);
        executeCommands(commandInput, generateTrimHandCommands(4, Arrays.asList("F25")));
        executeCommands(commandInput, generateTrimHandCommands(4, Arrays.asList("F30")));
        validatePlayerState(4, "4", "12");

        // QUEST 3 ============================>
        String phase6Commands = "r\ne\ny\n"; //p4 RETURNS AND P1 draws a card and sponsors it
        executeCommands(commandInput, phase6Commands);

        // Sponsor (P1) sets up all Q3 quest stages ============>
        Map<Integer, List<String>> stage2Setup = new HashMap<>();
        stage2Setup.put(1, Arrays.asList("F15"));
        stage2Setup.put(2, Arrays.asList("F15", "D5"));
        stage2Setup.put(3, Arrays.asList("F20", "D5"));
        String sponsor2Commands = generateSponsorStageCommands(1, stage2Setup);
        executeCommands(commandInput, sponsor2Commands);
        validatePlayerState(1, "0", "7");

        // Players decide to join quest3
        String joinQuest2Commands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinQuest2Commands);

        // Players decide to join STAGE 1
        String joinQ3Stage1Commands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinQ3Stage1Commands);

        //trimming hands before quest play
        executeCommands(commandInput, generateTrimHandCommands(2, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(3, Arrays.asList("F10")));
        executeCommands(commandInput, generateTrimHandCommands(4, Arrays.asList("F20")));
        validatePlayerState(2, "2", "12");
        validatePlayerState(3, "4", "12");
        validatePlayerState(4, "4", "12");

        // Stage 1 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("B15")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("B15")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("H10")));
        validatePlayerState(2, "2", "11");
        validatePlayerState(3, "4", "11");
        validatePlayerState(4, "4", "11");

        // Stage 2 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 3),
                Arrays.asList(2, 3)
        ));
        validatePlayerState(2, "2", "12");
        validatePlayerState(3, "4", "12");

        // Stage 2 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("B15", "H10")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("B15", "S10")));
        validatePlayerState(2, "2", "10");
        validatePlayerState(3, "4", "10");

        // Stage 3 participation
        executeCommands(commandInput, generatePlayerParticipationCommands(
                Arrays.asList(2, 3),
                Arrays.asList(2, 3)
        ));

        // Stage 3 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("L20", "S10")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("E30")));
        validatePlayerState(2, "5", "9");
        validatePlayerState(3, "7", "10");
        Thread.sleep(2000);

        validatePlayerState(1, "0", "15");
        // Final hand trimming for P1
        //String phase5Commands = "2\n2\n2\n";
        //executeCommands(commandInput, phase5Commands);
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F15")));
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F15")));
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F15")));

        //End of Scenario Assert
        validatePlayerState(1, "0", "12"); //SPONSOR P1
        validatePlayerState(2, "5", "9");
        validatePlayerState(3, "7", "10");
        validatePlayerState(4, "4", "11");

        //specific hand of each player to be asserted at the end of the scenario
        validatePlayerCards(1, Arrays.asList("D5", "D5", "H10", "H10", "H10", "S10", "S10", "S10", "S10", "F25", "F25", "F35"));
        validatePlayerCards(2, Arrays.asList("F15", "F25", "F30", "F40", "H10", "S10", "S10", "S10", "E30"));
        validatePlayerCards(3, Arrays.asList("F10", "F25", "F30", "F40", "F50", "H10", "H10", "S10", "S10", "L20"));
        validatePlayerCards(4, Arrays.asList("F25", "F25", "F30", "F50", "F70", "D5", "D5", "S10", "S10", "B15", "L20"));
    }



    private List<Card> getRiggedAdventureDeckScenario4() {
        return Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                //not in game below
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon")
        );
    }

    private List<Card> getRiggedEventDeckScenario4() {
        return Arrays.asList(
                new Card("Q2", "Q", 2, "Quest")
        );
    }

    private Map<String, List<Card>> getRiggedHandsScenario4() {
        Map<String, List<Card>> riggedHands = new HashMap<>();
        riggedHands.put("P1", Arrays.asList(
                new Card("F50", "F", 50, "Foe"),
                new Card("F70", "F", 70, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon")
                ));
        riggedHands.put("P2", Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("F40", "F", 40, "Foe"),
                new Card("E30", "E", 30, "Weapon")
        ));
        riggedHands.put("P3", Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("F40", "F", 40, "Foe"),
                new Card("L20", "L", 20, "Weapon")
        ));
        riggedHands.put("P4", Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F25", "F", 25, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("F50", "F", 50, "Foe"),
                new Card("E30", "E", 30, "Weapon")
        ));

        return riggedHands;
    }

    private void rigGameForScenario4() throws IOException {
        List<Card> adventureDeck = getRiggedAdventureDeckScenario4();
        List<Card> eventDeck = getRiggedEventDeckScenario4();
        Map<String, List<Card>> hands = getRiggedHandsScenario4();

        System.out.println("DEBUG [SeleniumTests 4] Adventure Deck size: " + adventureDeck.size());
        System.out.println("DEBUG [SeleniumTests 4] Event Deck size: " + eventDeck.size());
        System.out.println("DEBUG [SeleniumTests 4] Hands map size: " + hands.size());
        System.out.println("Rigging game 4, preparing HTTP connection...");
        System.out.println("DEBUG [SeleniumTests 4] Starting HTTP request...");

        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/api/game/start").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        System.out.println("Preparing JSON payload 4...");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        payload.put("adventureDeck", adventureDeck);
        payload.put("eventDeck", eventDeck);
        payload.put("hands", hands);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = objectMapper.writeValueAsBytes(payload);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to rig game scenario 4. Response code: " + responseCode);
        }
        System.out.println("DEBUG [SeleniumTests 4] Response Code: " + responseCode);
        System.out.println("Game 4 rigged successfully!");
    }



    @Test
    public void scenario4_0winner_QUEST() throws IOException, InterruptedException {
        rigGameForScenario4(); // Rig the game
        WebElement commandInput = driver.findElement(By.id("commandInput"));
        Thread.sleep(1000);

        // Validate initial state
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        String phase1Commands = "e\ny\n"; //p1 draws a card and sponsors it
        executeCommands(commandInput, phase1Commands);

        // Sponsor (P1) sets up all Q2 quest stages
        Map<Integer, List<String>> stageSetup = new HashMap<>();
        stageSetup.put(1, Arrays.asList("F50", "D5", "S10", "H10", "B15", "L20"));
        stageSetup.put(2, Arrays.asList("F70", "D5", "S10", "H10", "B15", "L20"));
        String sponsorCommands = generateSponsorStageCommands(1, stageSetup);
        executeCommands(commandInput, sponsorCommands);
        validatePlayerState(1, "0", "0");

        // Players decide to join quest
        String joinQuestCommands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinQuestCommands);

        // Players decide to join STAGE 1
        String joinStage1Commands = generatePlayerParticipationCommands(
                Arrays.asList(2, 3, 4), // eligible players
                Arrays.asList(2, 3, 4)  // participating players
        );
        executeCommands(commandInput, joinStage1Commands);

        //trimming hands before quest play
        executeCommands(commandInput, generateTrimHandCommands(2, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(3, Arrays.asList("F15")));
        executeCommands(commandInput, generateTrimHandCommands(4, Arrays.asList("F10")));
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        // Stage 1 attacks
        executeCommands(commandInput, generateAttackCommands(2, Arrays.asList("E30")));
        executeCommands(commandInput, generateAttackCommands(3, Arrays.asList("")));
        executeCommands(commandInput, generateAttackCommands(4, Arrays.asList("")));
        validatePlayerState(2, "0", "11");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");
        Thread.sleep(2000);

        validatePlayerState(1, "0", "14");
        // Final hand trimming for P1
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F5")));
        executeCommands(commandInput, generateTrimHandCommands(1, Arrays.asList("F10")));

        //End of Scenario Assert
        validatePlayerState(1, "0", "12"); //sponsor p1
        validatePlayerState(2, "0", "11");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        //specific hand of each player to be asserted at the end of the scenario
        validatePlayerCards(1, Arrays.asList("F15", "D5", "D5", "D5", "D5", "H10", "H10", "H10", "H10", "S10", "S10", "S10"));
        validatePlayerCards(2, Arrays.asList("F5", "F5", "F10", "F15", "F15", "F20", "F20", "F25", "F30", "F30", "F40"));
        validatePlayerCards(3, Arrays.asList("F5", "F5", "F10", "F15", "F15", "F20", "F20", "F25", "F25", "F30", "F40", "L20"));
        validatePlayerCards(4, Arrays.asList("F5", "F5", "F10", "F15", "F15", "F20", "F20", "F25", "F25", "F30", "F50", "E30"));
    }



}
