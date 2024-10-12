package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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

}