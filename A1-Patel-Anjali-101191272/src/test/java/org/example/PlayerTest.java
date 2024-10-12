package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    @DisplayName("R-Test-04: Setup 4 players.")
    public void RESP_04_test_01() {
        Player player1 = new Player("P1"); //// Create a player with the name "P1"
        assertEquals("P1", player1.getName(), "Player name should be P1.");

        Player player2 = new Player("P2"); //// Create a player with the name "P2"
        assertEquals("P2", player2.getName(), "Player name should be P2.");

        Player player3 = new Player("P3"); //// Create a player with the name "P3"
        assertEquals("P3", player3.getName(), "Player name should be P3.");

        Player player4 = new Player("P4"); //// Create a player with the name "P4"
        assertEquals("P4", player4.getName(), "Player name should be P4.");

    }
}