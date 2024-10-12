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
    }

    @Test
    @DisplayName("R-Test-01: Initialization of game environment.")
    public void RESP_01_test_01() {
        game.initializeGameEnvironment();
        assertNotNull(game.getAdventureDeck(), "Adventure deck should not be null"); // make sure adventure deck is initialized
        assertNotNull(game.getEventDeck(), "Event deck should not be null"); // make sure event deck is initialized
        assertEquals(0, game.getPlayerCount(), "Initial player count should be 0"); // make sure no players are set up yet
    }

}