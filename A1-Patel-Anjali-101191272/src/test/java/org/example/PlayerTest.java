package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Game game;
    private Player player;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        game = new Game(); // Initialize the game
        game.initializeGameEnvironment(); // Set up the decks

        // Check the test name and skip initializing players for RESP_04_test_01
        if (!testInfo.getDisplayName().equals("R-Test-04: Setup 4 players.")) {
            game.initializePlayers(); // Initialize players for all other tests
        }
    }

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

    @Test
    @DisplayName("R-Test-07: Distribute 12 adventure cards to each player.")
    public void RESP_07_test_01() {
        // Retrieve players from the game
        List<Player> players = game.getPlayers();

        // Create a list of 12 cards (mocked for testing purposes)
        List<Card> cardsToReceive = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cardsToReceive.add(new Card("test","W", 10, "Weapon")); // Example of a Weapon card
        }

        //System.out.println("receive the cards: "+cardsToReceive);

        // Ensure the player's hand is initially empty
        assertTrue(players.get(0).getHand().isEmpty(), "Player's hand should initially be empty.");

        // Call the receiveCards method
        players.get(0).receiveCards(cardsToReceive);

        // Ensure the player's hand contains exactly 12 cards after receiving
        assertEquals(12, players.get(0).getHand().size(), "Player should have received 12 cards.");

        // Ensure the cards in the hand are the same as the ones added
        assertEquals(cardsToReceive, players.get(0).getHand(), "The cards in the player's hand should match the cards received.");
    }

    @Test
    @DisplayName("R-TEST-12: Modify the Player class to include shields logic; Test gaining shields")
    public void RESP_12_test_01() {
        player = new Player("TestPlayer");
        player.gainShields(1); // Gain 1 shield
        assertEquals(1, player.getShields(), "Player should have 1 shield after gaining 1.");

        // Gain additional shields
        player.gainShields(2); // Gain 2 more shields
        assertEquals(3, player.getShields(), "Player should have 3 shields after gaining 2.");
    }

    @Test
    @DisplayName("R-TEST-12: Modify the Player class to include shields logic; Test losing shields")
    public void RESP_12_test_02() {
        player = new Player("TestPlayer");
        player.gainShields(2);
        // Player starts with 2 shields
        player.loseShields(1); // Lose 1 shield
        assertEquals(1, player.getShields(), "Player should have 1 shield after losing 1.");

        // Lose more shields than remaining
        player.loseShields(2); // Attempt to lose 2 shields
        assertEquals(0, player.getShields(), "Player shields should not go below 0.");
    }

    @Test
    @DisplayName("R-TEST-19: Always return player's sorted hand")
    public void RESP_19_test_01() {
        // Create a player and a hand of cards
        player = new Player("TestPlayer");

        // List of unsorted cards with different categories and values
        List<Card> hand = new ArrayList<>();

        // Add Foes with different values
        hand.add(new Card("F5", "F", 5, "Foe"));   // Foe with value 5
        hand.add(new Card("F3", "F", 3, "Foe"));   // Foe with value 3
        hand.add(new Card("F7", "F", 7, "Foe"));   // Foe with value 7

        // Add Weapons with different types and values
        hand.add(new Card("H8", "H", 8, "Weapon")); // Horse (type "H")
        hand.add(new Card("S6", "S", 6, "Weapon")); // Sword (type "S")
        hand.add(new Card("S4", "S", 4, "Weapon")); // Sword (type "S")
        hand.add(new Card("D5", "D", 5, "Weapon"));    // Other Weapon (type "D")

        player.receiveCards(hand);

        // Retrieve the sorted hand using the method to be tested
        List<Card> sortedHand = player.getHand();

        // Check that foes come first, sorted by value
        assertEquals("F3", sortedHand.get(0).getCardName(), "First card should be F3 with value 3.");
        assertEquals("F5", sortedHand.get(1).getCardName(), "Second card should be F5 with value 5.");
        assertEquals("F7", sortedHand.get(2).getCardName(), "Third card should be F7 with value 7.");

        // Check that weapons come next, swords first, sorted by value
        assertEquals("S4", sortedHand.get(3).getCardName(), "Fourth card should be S4 with value 4.");
        assertEquals("S6", sortedHand.get(4).getCardName(), "Fifth card should be S6 with value 6.");

        // Then other weapons, followed by horses
        assertEquals("H8", sortedHand.get(5).getCardName(), "Sixth card should be H8 with value 8.");
        assertEquals("D5", sortedHand.get(6).getCardName(), "Seventh card should be D5 with value 5.");
    }

}