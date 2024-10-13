package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        game = new Game();
        game.initializeGameEnvironment();

        // Check the test name and skip initializing players for RESP_01_test_01
        if (!testInfo.getDisplayName().equals("R-Test-01: Initialization of game environment.")) {
            game.initializePlayers(); // Initialize players for all other tests
        }
    }

    @Test
    @DisplayName("R-Test-01: Initialization of game environment.")
    public void RESP_01_test_01() {
        assertNotNull(game.getAdventureDeck(), "Adventure deck should not be null"); // make sure adventure deck is initialized
        assertNotNull(game.getEventDeck(), "Event deck should not be null"); // make sure event deck is initialized
        assertEquals(0, game.getPlayers().size(), "Initial player count should be 0"); // make sure no players are set up yet
    }

    @Test
    @DisplayName("R-Test-02: Setup adventure deck") //Adventure deck has 100 cards after game initialization.
    public void RESP_02_test_02() {
        // Ensure the AdventureDeck has exactly 100 cards after initialization
        AdventureDeck adventureDeck = game.getAdventureDeck();
        assertEquals(100, adventureDeck.getTotalCards(), "The total number of cards in the AdventureDeck should be 100.");
    }

    @Test
    @DisplayName("R-Test-03: Setup event deck") //Event deck has 17 cards after game initialization.
    public void RESP_03_test_02() {
        // Ensure the EventDeck has exactly 17 cards after initialization
        EventDeck eventDeck = game.getEventDeck();
        assertEquals(17, eventDeck.getTotalCards(), "The total number of cards in the EventDeck should be 17.");
    }

    @Test
    @DisplayName("R-Test-04: Setup 4 players.")
    public void RESP_04_test_02() {
        assertEquals(4, game.getPlayers().size(), "There should be exactly 4 players.");

        assertEquals("P1", game.getPlayers().get(0).getName(), "Player 1 should be named P1.");
        assertEquals("P2", game.getPlayers().get(1).getName(), "Player 2 should be named P2.");
        assertEquals("P3", game.getPlayers().get(2).getName(), "Player 3 should be named P3.");
        assertEquals("P4", game.getPlayers().get(3).getName(), "Player 4 should be named P4.");
    }

    @Test
    @DisplayName("R-Test-07: Distribute 12 adventure cards to each player.")
    public void RESP_07_test_02() {
        game.distributeAdventureCards(); // Distribute 12 adventure cards to each player

        List<Player> players = game.getPlayers(); // Get the list of players

        // Assert that each player has 12 cards in their hand
        for (Player player : players) {
            assertEquals(12, player.getHand().size(),
                    "Player " + player.getName() + " should have 12 cards in their hand.");
        }
    }

    @Test
    @DisplayName("R-Test-08: Manage current player and transition to next player.")
    public void RESP_08_test_01() {
        assertEquals("P1", game.getCurrentPlayer().getName(), "Current player should be P1.");

        game.nextPlayer(); // Move to next player
        assertEquals("P2", game.getCurrentPlayer().getName(), "Current player should be P2.");

        game.nextPlayer(); // Move to next player
        assertEquals("P3", game.getCurrentPlayer().getName(), "Current player should be P3.");

        game.nextPlayer(); // Move to next player
        assertEquals("P4", game.getCurrentPlayer().getName(), "Current player should be P4.");

        game.nextPlayer(); // Move to next player
        assertEquals("P1", game.getCurrentPlayer().getName(), "Current player should wrap around to P1.");
    }

    @Test
    @DisplayName("R-TEST-09: Display current player's hand.")
    public void RESP_09_test_01() {
        //game.distributeAdventureCards();
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player
        assertNotNull(currentPlayer, "The current player should not be null.");

        // Setup: Create a list of cards for testing
        List<Card> testCards = new ArrayList<>();
        testCards.add(new Card("F25", "F", 25, "Foe")); // Example Foe card
        testCards.add(new Card("F50", "F", 50, "Foe")); // Example Foe card
        testCards.add(new Card("S10", "S", 10, "Weapon")); // Example Sword card
        testCards.add(new Card("S10", "S", 10, "Weapon")); // Another Sword card
        testCards.add(new Card("S10", "S", 10, "Weapon")); // Another Sword card
        testCards.add(new Card("S10", "S", 10, "Weapon")); // Another Sword card
        testCards.add(new Card("S10", "S", 10, "Weapon")); // Another Sword card
        testCards.add(new Card("H10", "H", 10, "Weapon")); // Example Horse card
        testCards.add(new Card("H10", "H", 10, "Weapon")); // Another Horse card
        testCards.add(new Card("D5", "D", 5, "Weapon")); // Example Dagger card
        testCards.add(new Card("D5", "D", 5, "Weapon")); // Another Dagger card
        testCards.add(new Card("L20", "L", 20, "Weapon")); // Example Lance card

        // Use the receiveCards method to add cards to the player's hand
        currentPlayer.receiveCards(testCards);

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Call the method to display the current player's hand
        game.displayCurrentPlayerHand();

        // Restore original System.out
        System.setOut(originalOut);

        // Verify output
        String output = outputStream.toString();

        System.out.println(output);

        // Assertions: Check that the output contains the expected card order
        assertTrue(output.contains("[1] F25"), "Output should contain F25 first.");
        assertTrue(output.contains("[2] F50"), "Output should contain F50 second.");
        assertTrue(output.contains("[3] S10"), "Output should contain S10.");
        assertTrue(output.contains("[4] S10"), "Output should contain S10.");
        assertTrue(output.contains("[5] S10"), "Output should contain S10.");
        assertTrue(output.contains("[6] S10"), "Output should contain S10.");
        assertTrue(output.contains("[7] S10"), "Output should contain S10.");
        assertTrue(output.contains("[8] H10"), "Output should contain H10.");
        assertTrue(output.contains("[9] H10"), "Output should contain H10.");
        assertTrue(output.contains("[10] D5"), "Output should contain D5.");
        assertTrue(output.contains("[11] D5"), "Output should contain D5.");
        assertTrue(output.contains("[12] L20"), "Output should contain L20.");

        // Also check that the current player's hand is not empty
        assertFalse(currentPlayer.getHand().isEmpty(), "The current player's hand should have cards.");
    }

}