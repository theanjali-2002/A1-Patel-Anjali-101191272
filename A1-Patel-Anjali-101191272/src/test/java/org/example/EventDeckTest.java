package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventDeckTest {
    @Test
    @DisplayName("R-Test-03: Setup event deck.")
    public void RESP_03_test_01() {
        EventDeck eventDeck = new EventDeck();
        eventDeck.setupDeck();

        // Check the total number of cards in the deck (12 quest cards + 5 event cards = 17)
        assertEquals(17, eventDeck.getTotalCards(), "The total number of cards should be 17.");

        // Check the count of quest cards
        assertEquals(12, eventDeck.countQuestCards(), "There should be 12 Quest cards.");

        // Check the count of event cards
        assertEquals(5, eventDeck.countEventCards(), "There should be 5 Event cards.");
    }
}