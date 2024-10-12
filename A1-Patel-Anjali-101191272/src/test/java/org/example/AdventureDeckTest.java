package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdventureDeckTest {
    @Test
    @DisplayName("R-Test-02: Setup adventure deck.")
    public void RESP_02_test_01() {
        AdventureDeck adventureDeck = new AdventureDeck();
        adventureDeck.setupDeck();

        // Check the total number of cards in the deck
        assertEquals(100, adventureDeck.getTotalCards(), "The total number of cards should be 100.");

        // Check the count of specific types of cards
        assertEquals(50, adventureDeck.countFoeCards(), "There should be 50 Foe cards.");
        assertEquals(50, adventureDeck.countWeaponCards(), "There should be 50 Weapon cards.");
    }
}