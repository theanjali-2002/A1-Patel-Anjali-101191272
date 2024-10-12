package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
        game.initializeGameEnvironment();
    }

    @Test
    @DisplayName("R-Test-01: Initialization of game environment.")
    public void RESP_01_test_01() {
        assertNotNull(game.getAdventureDeck(), "Adventure deck should not be null"); // make sure adventure deck is initialized
        assertNotNull(game.getEventDeck(), "Event deck should not be null"); // make sure event deck is initialized
        assertEquals(0, game.getPlayerCount(), "Initial player count should be 0"); // make sure no players are set up yet
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

}