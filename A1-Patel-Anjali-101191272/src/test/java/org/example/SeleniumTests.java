package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
        
        // Sort both lists to ensure order-independent comparison
        Collections.sort(expectedCards);
        Collections.sort(actualCards);
        
        assertEquals(expectedCards, actualCards, 
            "Player " + playerNumber + " cards mismatch. Expected: " + expectedCards + ", but got: " + actualCards);
    }

    //helper method for input commands for longer games
    private void executeCommands(WebElement commandInput, String commands) throws InterruptedException {
        for (String command : commands.split("\n")) {
            commandInput.sendKeys(command);
            commandInput.sendKeys(Keys.RETURN);
            Thread.sleep(1000); // Add delay between commands to let the UI process
        }
    }


    @BeforeEach
    public void setUp() throws IOException {
        //gameService = new GameService(); // Initialize a fresh game service for each test
        // Set the ChromeDriver path (skip this if added to PATH)
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

//    @Test
//    public void testGameTitle() throws InterruptedException {
//        // Verify the game title
//        WebElement title = driver.findElement(By.className("game-title"));
//        assertEquals("4004 Quest Game", title.getText());
//
//        WebElement commandInput = driver.findElement(By.id("commandInput"));
//        commandInput.sendKeys("s");
//        commandInput.sendKeys(Keys.RETURN);
//        Thread.sleep(1000);
//
//        // Player 1
//        WebElement player1Shields = driver.findElement(By.id("player1-shields"));
//        WebElement player1Hand = driver.findElement(By.id("player1-hand"));
//        assertEquals("0", player1Shields.getText());
//        assertEquals("12", player1Hand.getText());
//
//        // Validate initial state
//        validatePlayerState(1, "0", "12");
//        validatePlayerState(2, "0", "12");
//        validatePlayerState(3, "0", "12");
//        validatePlayerState(4, "0", "12");
//
//        driver.quit();
//    }

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
                new Card("S10", "S", 15, "Weapon"),
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
        //connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        //connection.setConnectTimeout(5000); // Add timeout
        //connection.setReadTimeout(10000);  // Add timeout
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
        rigGameForScenario1(); // Rig the game with this ID

        // Find the input field and simulate user input
        WebElement commandInput = driver.findElement(By.id("commandInput"));
        Thread.sleep(1000);

        // Validate initial state
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        String phase1Commands = "e\nn\ny\n"; //p1 draws a card and declines to sponsor, p2 sponsors it
        executeCommands(commandInput, phase1Commands);

        String phase2Commands = "1\n6\nq\n"; //p2 sets up the Quest Stage 1
        executeCommands(commandInput, phase2Commands);
        validatePlayerState(2, "0", "10");

        String phase3Commands = "2\n5\nq\n"; //p2 sets up the Quest Stage 2
        executeCommands(commandInput, phase3Commands);
        validatePlayerState(2, "0", "8");

        String phase4Commands = "2\n2\nq\n"; //p2 sets up the Quest Stage 3
        executeCommands(commandInput, phase4Commands);
        validatePlayerState(2, "0", "6");

        String phase5Commands = "1\n1\n4\nq\n"; //p2 sets up the Quest Stage 4
        executeCommands(commandInput, phase5Commands);
        validatePlayerState(2, "0", "3");

        String phase6Commands = "y\ny\ny\ny\ny\ny\n"; //asking players to play the quest and stage1
        executeCommands(commandInput, phase6Commands);

        String phase7Commands = "1\n1\n1\n"; //all 3 players discards one card since they drew one to play the quest
        executeCommands(commandInput, phase7Commands);
        validatePlayerState(1, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        String phase8Commands = "5\n6\nq\n"; //p1 prepares attack stage 1 //either 9 or 10
        executeCommands(commandInput, phase8Commands);

        String phase9Commands = "4\n5\nq\n"; //p3 prepares attack stage 1 //either 9 or 10
        executeCommands(commandInput, phase9Commands);

        String phase10Commands = "5\n7\nq\n"; //p4 prepares attack stage 1 //6-7
        executeCommands(commandInput, phase10Commands);

        String phase11Commands = "y\ny\ny\n"; //asking players to play stage 2
        executeCommands(commandInput, phase11Commands);
        // 11 because they all drew one card to play the next stage
        validatePlayerState(1, "0", "11");
        validatePlayerState(3, "0", "11");
        validatePlayerState(4, "0", "11");

        String phase12Commands = "6\n7\nq\n"; //p1 prepares attack stage 2
        executeCommands(commandInput, phase12Commands);

        String phase13Commands = "4\n9\nq\n"; //p3 prepares attack stage 2
        executeCommands(commandInput, phase13Commands);

        String phase14Commands = "6\n7\nq\n"; //p4 prepares attack stage 2
        executeCommands(commandInput, phase14Commands);

        validatePlayerCards(1, Arrays.asList("F5", "F10", "F15", "F15", "F30", "H10", "B15", "B15", "L20"));

        String phase15Commands = "y\ny\n"; //asking players to play stage 3 (p1 got out)
        executeCommands(commandInput, phase15Commands);
        // 11 because they all drew one card to play the next stage
        validatePlayerState(3, "0", "10");
        validatePlayerState(4, "0", "10");

        String phase16Commands = "5\n6\n9\nq\n"; //p3 prepares attack stage 3
        executeCommands(commandInput, phase16Commands);

        String phase17Commands = "6\n7\n8\nq\n"; //p4 prepares attack stage 3
        executeCommands(commandInput, phase17Commands);

        String phase18Commands = "y\ny\n"; //asking players to play stage 4
        executeCommands(commandInput, phase18Commands);
        // 11 because they all drew one card to play the next stage
        validatePlayerState(3, "0", "8");
        validatePlayerState(4, "0", "8");

        String phase19Commands = "6\n7\n8\nq\n"; //p3 prepares attack stage 4
        executeCommands(commandInput, phase19Commands);

        String phase20Commands = "4\n5\n7\n8\nq\n"; //p4 prepares attack stage 4
        executeCommands(commandInput, phase20Commands);

        validatePlayerState(2, "0", "16");
        String phase21Commands = "1\n3\n2\n7\nq\n"; //After Quest ends, P2 has to trim down to 12 cards, discard 4 cards.
        executeCommands(commandInput, phase21Commands);

        //End of Scenario Assert
        validatePlayerState(2, "0", "12"); //sponsor p2
        validatePlayerState(1, "0", "9");
        validatePlayerState(3, "0", "5");
        validatePlayerState(4, "4", "4");

        //specific hand of each player to be asserted at the end of the scenario
        validatePlayerCards(3, Arrays.asList("F5", "F5", "F15", "F30", "S10"));
        validatePlayerCards(4, Arrays.asList("F15", "F15", "F40", "L20"));



    }

//    @Test
//    public void scenario2_2winner_game_2winner_quest() throws IOException, InterruptedException {
//        WebElement title = driver.findElement(By.className("game-title"));
//        assertEquals("4004 Quest Game", title.getText());
//
//        driver.quit();
//    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


}
