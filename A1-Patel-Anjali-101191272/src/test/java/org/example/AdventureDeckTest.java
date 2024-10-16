package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    @DisplayName("R-Test-06: Draw Adventure Cards with given number; test to Draw Cards with Correct Number")
    public void RESP_06_test_01() {
        AdventureDeck adventureDeck = new AdventureDeck();
        adventureDeck.setupDeck(); // Initialize the deck before each test

        int initialSize = adventureDeck.getTotalCards();
        int cardsToDraw = 12;
        List<Card> drawnCards = adventureDeck.drawACards(cardsToDraw);

        // Assert that the number of drawn cards is as requested
        assertEquals(cardsToDraw, drawnCards.size(), "The number of drawn cards should match the requested amount.");

        // Assert that the size of the deck has decreased accordingly
        assertEquals(initialSize - cardsToDraw, adventureDeck.getTotalCards(), "The deck size should be reduced by the number of drawn cards.");
    }

    @Test
    @DisplayName("R-Test-06: Draw Adventure Cards with given number; Should draw only the available cards")
    public void RESP_06_test_02() {
        AdventureDeck adventureDeck = new AdventureDeck();
        adventureDeck.setupDeck(); // Initialize the deck before each test

        int initialSize = adventureDeck.getTotalCards();
        int cardsToDraw = initialSize + 10; // Attempting to draw more cards than available
        List<Card> drawnCards = adventureDeck.drawACards(cardsToDraw);

        // Assert that only the available cards are drawn
        assertEquals(initialSize, drawnCards.size(), "Should draw only the available cards.");

        // Assert that the deck is now empty
        assertEquals(0, adventureDeck.getTotalCards(), "The deck should be empty after drawing all cards.");
    }

    @Test
    @DisplayName("R-Test-06: Draw Adventure Cards with given number; Drawing from an empty deck should return an empty list")
    public void RESP_06_test_03() {
        AdventureDeck adventureDeck = new AdventureDeck();
        adventureDeck.setupDeck(); // Initialize the deck before each test

        // Draw all cards from the deck
        adventureDeck.drawACards(adventureDeck.getTotalCards());

        // Now the deck should be empty
        assertEquals(0, adventureDeck.getTotalCards(), "The deck should be empty after drawing all cards.");

        // Drawing again should return an empty list
        List<Card> drawnCards = adventureDeck.drawACards(12);
        assertTrue(drawnCards.isEmpty(), "Drawing from an empty deck should return an empty list.");
    }
}