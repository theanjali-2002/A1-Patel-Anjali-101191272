package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
        //game.distributeAdventureCards(); //note we do not want extra 12 cards generated by the code/game itself for this test
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player
        assertNotNull(currentPlayer, "The current player should not be null.");

        // Setup: Create a list of cards for testing
        List<Card> testCards = new ArrayList<>();
        testCards.add(new Card("F25", "F", 25, "Foe"));
        testCards.add(new Card("F50", "F", 50, "Foe"));
        testCards.add(new Card("S10", "S", 10, "Weapon"));
        testCards.add(new Card("S10", "S", 10, "Weapon"));
        testCards.add(new Card("S10", "S", 10, "Weapon"));
        testCards.add(new Card("S10", "S", 10, "Weapon"));
        testCards.add(new Card("S10", "S", 10, "Weapon"));
        testCards.add(new Card("H10", "H", 10, "Weapon"));
        testCards.add(new Card("H10", "H", 10, "Weapon"));
        testCards.add(new Card("D5", "D", 5, "Weapon"));
        testCards.add(new Card("D5", "D", 5, "Weapon"));
        testCards.add(new Card("L20", "L", 20, "Weapon"));

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

        //System.out.println(output);

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

    @Test
    @DisplayName("R-TEST-11: Draws an Event card; No Event Cards in the Deck")
    public void RESP_11_test_01() {
        // Ensure the event deck is empty
        EventDeck eventDeck = game.getEventDeck();
        eventDeck.clearDeck();  // Clear all cards in the event deck

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Simulate user input to press 'e'
        ByteArrayInputStream in = new ByteArrayInputStream("e\n".getBytes());
        System.setIn(in);

        // Call the method
        Card result = game.drawEventCard();

        // Restore original System.out and System.in
        System.setOut(System.out);
        System.setIn(System.in);

        // Verify the result
        assertNull(result, "When no cards are in the deck, the result should be null.");
        assertTrue(outputStream.toString().contains("No event cards left in the deck!"), "The message should inform that no event cards are left.");
    }

    @Test
    @DisplayName("R-TEST-11: Draws an Event card; by Pressing 'e'")
    public void RESP_11_test_02() {
        // Prepare event deck with some cards
        EventDeck eventDeck = game.getEventDeck();
        List<Card> testCards = new ArrayList<>();
        testCards.add(new Card("ECard1", "E", 1, "Event"));
        testCards.add(new Card("QCard2", "Q", 2, "Quest"));
        eventDeck.setDeck(testCards);

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Simulate user input to press 'e'
        ByteArrayInputStream in = new ByteArrayInputStream("e\n".getBytes());
        System.setIn(in);

        // Call the method
        Card result = game.drawEventCard();

        // Restore original System.out and System.in
        System.setOut(System.out);
        System.setIn(System.in);

        // Verify the result
        assertNotNull(result, "The drawn card's category should not be null.");
        assertTrue(result.getCategory().equals("Event") || result.getCategory().equals("Quest"), "The drawn card should be of type 'Event' or 'Quest'.");
        assertTrue(outputStream.toString().contains("Drawn Card:"), "The output should indicate that a card was drawn.");
    }

    @Test
    @DisplayName("R-TEST-11: Draws an Event card; Quit Game by Pressing 'q'")
    public void RESP_11_test_03() {
        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Simulate user input to press 'q'
        ByteArrayInputStream in = new ByteArrayInputStream("q\n".getBytes());
        System.setIn(in);

        // Call the method
        Card result = game.drawEventCard();

        // Restore original System.out and System.in
        System.setOut(System.out);
        System.setIn(System.in);

        // Verify the result
        assertNull(result, "When the user presses 'q', the result should be null.");
        assertTrue(outputStream.toString().contains("Game Exiting..."), "The output should indicate that the game is exiting.");
    }

    @Test
    @DisplayName("R-TEST-11: Draws an Event card; Invalid Input Handling")
    public void RESP_11_test_04() {
        // Prepare event deck with some cards
        EventDeck eventDeck = game.getEventDeck();
        List<Card> testCards = new ArrayList<>();
        testCards.add(new Card("ECard1", "E", 1, "Event"));
        eventDeck.setDeck(testCards);

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Simulate user input with invalid input first, then 'e'
        ByteArrayInputStream in = new ByteArrayInputStream("x\ne\n".getBytes());
        System.setIn(in);

        // Call the method
        Card result = game.drawEventCard();

        // Restore original System.out and System.in
        System.setOut(System.out);
        System.setIn(System.in);

        // Verify the result
        assertNotNull(result.getCategory(), "The drawn card's category should not be null after valid input.");
        assertEquals("E", result.getType(), "The drawn card should be of type 'E'.");
        assertTrue(outputStream.toString().contains("Invalid input!"), "The output should indicate that the first input was invalid.");
    }

    @Test
    @DisplayName("R-TEST-13: Carrying out Event and discarding E-card logic; Handle Plague Event Card Effect")
    public void RESP_13_test_01() {
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.gainShields(5);

        // Create a Plague event card
        Card plagueCard = new Card("Plague", "E", -2, "Event");

        // Capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Call the method with the Plague card
        game.handleECardEffects(plagueCard, currentPlayer);

        // Restore original System.out
        System.setOut(originalOut);

        // Assertions
        assertEquals(3, currentPlayer.getShields(), "Current player should lose 2 shields.");
        assertTrue(outputStream.toString().contains("Plague card is drawn and current player loses 2 shields."),
                "Output should indicate the Plague card effect.");
    }

    @Test
    @DisplayName("R-TEST-13: Carrying out Event and discarding E-card logic; Handle Queen's Favor Event Card Effect")
    public void RESP_13_test_02() {
        // Get the current player
        Player currentPlayer = game.getCurrentPlayer();

        // Set the drawn card to Queen's Favor
        Card drawnCard = new Card("Queen's favor", "Event", 0, "Description");

        // Call the method to handle the effects of the drawn card
        game.handleECardEffects(drawnCard, currentPlayer);

        // Verify that the player received the correct cards
        assertEquals(2, currentPlayer.getHand().size(), "Current player should have drawn 2 adventure cards.");
    }

    @Test
    @DisplayName("R-TEST-13: Carrying out Event and discarding E-card logic; Handle Prosperity Event Card Effect")
    public void RESP_13_test_03() {
        // Mock players for testing
        List<Player> players = game.getPlayers();

        // Create a Prosperity event card
        Card prosperityCard = new Card("Prosperity", "Event", 0, "Event");

        // Capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Call the method with the Prosperity card
        game.handleECardEffects(prosperityCard, players.get(0)); // Current player doesn't matter here

        // Restore original System.out
        System.setOut(originalOut);

        // Assertions
        for (Player player : players) {
            assertEquals(2, player.getHand().size(), "Each player should have drawn 2 adventure cards.");
            assertTrue(outputStream.toString().contains(player.getName() + " draws 2 adventure cards."),
                    "Output should indicate that each player draws cards due to Prosperity.");
        }
    }

    @Test
    @DisplayName("R-TEST-14: Prompt players for quest sponsor; valid input 'y'")
    public void RESP_14_test_01() {
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Simulate user input for sponsoring the quest
        String simulatedInput = "y\n"; // Simulate 'yes' input
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); // Set the input stream to simulate user input

        // Call the method
        boolean result = game.promptToSponsor(currentPlayer);

        // Restore original System.out and System.in
        System.setOut(originalOut);
        System.setIn(System.in);

        // Verify output
        String output = outputStream.toString();
        assertTrue(output.contains(currentPlayer.getName() + " has chosen to sponsor the quest."),
                "Output should confirm the player sponsored the quest.");
        assertTrue(result, "The result should be true for sponsorship.");
    }

    @Test
    @DisplayName("R-TEST-14: Prompt players for quest sponsor; valid input 'n'")
    public void RESP_14_test_02() {
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Simulate user input for declining the sponsorship
        String simulatedInput = "n\n"; // Simulate 'no' input
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); // Set the input stream to simulate user input

        // Call the method
        boolean result = game.promptToSponsor(currentPlayer);

        // Restore original System.out and System.in
        System.setOut(originalOut);
        System.setIn(System.in);

        // Verify output
        String output = outputStream.toString();
        assertTrue(output.contains(currentPlayer.getName() + " has declined to sponsor the quest."),
                "Output should confirm the player declined the sponsorship.");
        assertFalse(result, "The result should be false for declining sponsorship.");
    }

    @Test
    @DisplayName("R-TEST-14: Prompt players for quest sponsor; invalid input")
    public void RESP_14_test_03() {
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Simulate user input for invalid response
        String simulatedInput = "invalid\nn\n"; // First input is invalid, then valid 'no'
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); // Set the input stream to simulate user input

        // Call the method
        boolean result = game.promptToSponsor(currentPlayer);

        // Restore original System.out and System.in
        System.setOut(originalOut);
        System.setIn(System.in);

        // Verify output
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input. Please enter 'y' or 'n':"),
                "Output should indicate an invalid input.");
        assertTrue(output.contains(currentPlayer.getName() + " has declined to sponsor the quest."),
                "Output should confirm the player declined the sponsorship after the valid input.");
        assertFalse(result, "The result should be false for declining sponsorship.");
    }

    @Test
    @DisplayName("R-TEST-15: Find the sponsor from all Players; One player agrees to sponsor the quest.")
    public void RESP_15_test_01() {
        // Arrange: Create a list of players
        Player currentPlayer = new Player("P1");
        Player p2 = new Player("P2");
        Player p3 = new Player("P3");
        Player p4 = new Player("P4");
        List<Player> players = new ArrayList<>();
        players.add(currentPlayer);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        // Mock the promptToSponsor method
        game = new Game() {
            @Override
            public boolean promptToSponsor(Player player) {
                return player.getName().equals("P2"); // Only P2 agrees to sponsor
            }
        };

        // Act: Call the findSponsor method
        Player sponsor = game.findSponsor(currentPlayer, players);

        // Assert: Check if P2 sponsors the quest
        assertNotNull(sponsor, "P2 should sponsor the quest.");
        assertEquals("P2", sponsor.getName(), "P2 should be the sponsor.");
    }

    @Test
    @DisplayName("R-TEST-15: Find the sponsor from all Players; All players decline to sponsor the quest.")
    public void RESP_15_test_02() {
        // Arrange: Create a list of players
        Player currentPlayer = new Player("P1");
        Player p2 = new Player("P2");
        Player p3 = new Player("P3");
        Player p4 = new Player("P4");
        List<Player> players = new ArrayList<>();
        players.add(currentPlayer);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        // Mock the promptToSponsor method
        game = new Game() {
            @Override
            public boolean promptToSponsor(Player player) {
                return false; // All players decline to sponsor
            }
        };

        // Act: Call the findSponsor method
        Player sponsor = game.findSponsor(currentPlayer, players);

        // Assert: Check if no player sponsors the quest
        assertNull(sponsor, "No player should sponsor the quest.");
    }


    @Test
    @DisplayName("R-TEST-15: Find the sponsor from all Players; Sponsor wraps around to the first player.")
    public void RESP_15_test_03() {
        // Arrange: Create a list of players
        Player currentPlayer = new Player("P3");
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(currentPlayer);

        // Mock the promptToSponsor method
        game = new Game() {
            @Override
            public boolean promptToSponsor(Player player) {
                return player.getName().equals("P1"); // P1 sponsors the quest after wrap around
            }
        };

        // Act: Call the findSponsor method
        Player sponsor = game.findSponsor(currentPlayer, players);

        // Assert: Check if P1 sponsors after wrap around
        assertNotNull(sponsor, "P1 should sponsor the quest after wrap around.");
        assertEquals("P1", sponsor.getName(), "P1 should sponsor the quest after wrapping around.");
    }

}