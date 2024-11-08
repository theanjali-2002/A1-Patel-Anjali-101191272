package org.example;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.System.in;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private Game game;
    private final InputStream originalIn = in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        game = new Game();

        // Check the test name and skip initializing players for RESP_01_test_01
        if (!testInfo.getDisplayName().equals("A-TEST JP-Scenario")) {
            //game = new Game();
            game.initializeGameEnvironment();
        }

        // Check the test name and skip initializing players for RESP_01_test_01
        if (!testInfo.getDisplayName().equals("R-Test-01: Initialization of game environment.") || !testInfo.getDisplayName().equals("A-TEST JP-Scenario")) {
            game.initializePlayers(); // Initialize players for all other tests
        }

        // Set up to capture output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void simulateInput(String input) {
        ScannerSingleton.resetScanner(new ByteArrayInputStream(input.getBytes()));
        System.setIn(in); // Set System.in to new ByteArrayInputStream
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
        game.distributeAdventureCards();
        for (Player player : game.getPlayers()) {
            assertEquals(12, player.getHand().size(), "Each player should have 12 cards in their hand.");
        }
    }

    @Test
    @DisplayName("R-Test-08: Manage current player and transition to next player.")
    public void RESP_08_test_01() {
        assertEquals("P1", game.getCurrentPlayer().getName(), "Current player should be P1.");
        simulateInput("r\n");
        game.nextPlayer();
        assertEquals("P2", game.getCurrentPlayer().getName(), "Current player should be P2.");
        assertTrue(outputStream.toString().contains("Leaving the Hot Seat..."), "Output should indicate the end of turn.");
    }

    @Test
    @DisplayName("R-TEST-09: Display current player's hand.")
    public void RESP_09_test_01() {
        Player currentPlayer = game.getCurrentPlayer();
        assertNotNull(currentPlayer, "The current player should not be null.");

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

        currentPlayer.receiveCards(testCards);

        game.displayCurrentPlayerHand();
        String output = outputStream.toString();

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
    @DisplayName("R-TEST-11: Draws an Event card; No Event Cards in the Deck - Refill it")
    public void RESP_11_test_01() {
        EventDeck eventDeck = game.getEventDeck();
        eventDeck.clearDeck();
        eventDeck.discardEventCard(new Card("Plague", "E", -2, "Event"));
        eventDeck.discardEventCard(new Card("Queen's Favor", "E", 2, "Event"));

        simulateInput("e\n");
        Card result = game.drawEventCard();

        // Verify the result
        assertNotNull(result, "Expected a card to be drawn after refilling the deck.");
        assertTrue(outputStream.toString().contains("Drawn Card: "), "The drawn card should be displayed.");
        assertTrue(outputStream.toString().contains("The Event deck has been refilled from the discard pile."), "Should indicate the deck was refilled.");
    }

    @Test
    @DisplayName("R-TEST-11: Draws an Event card; by Pressing 'e'")
    public void RESP_11_test_02() {
        EventDeck eventDeck = game.getEventDeck();
        eventDeck.setDeck(Arrays.asList(
                new Card("ECard1", "E", 1, "Event"),
                new Card("QCard2", "Q", 2, "Quest")
        ));

        simulateInput("e\n");
        Card result = game.drawEventCard();

        // Verify the result
        assertNotNull(result, "The drawn card's category should not be null.");
        assertTrue(result.getCategory().equals("Event") || result.getCategory().equals("Quest"), "The drawn card should be of type 'Event' or 'Quest'.");
        assertTrue(outputStream.toString().contains("Drawn Card:"), "The output should indicate that a card was drawn.");
    }

    @Test
    @DisplayName("R-TEST-11: Draws an Event card; Quit Game by Pressing 'q'")
    public void RESP_11_test_03() {
        simulateInput("q\n");
        Card result = game.drawEventCard();

        // Verify the result
        assertNull(result, "When the user presses 'q', the result should be null.");
        assertTrue(outputStream.toString().contains("Game Exiting..."), "The output should indicate that the game is exiting.");
    }

    @Test
    @DisplayName("R-TEST-11: Draws an Event card; Invalid Input Handling")
    public void RESP_11_test_04() {
        EventDeck eventDeck = game.getEventDeck();
        eventDeck.setDeck(Arrays.asList(new Card("ECard1", "E", 1, "Event")));

        simulateInput("x\ne\n");
        Card result = game.drawEventCard();

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
        simulateInput("r\n");

        Card plagueCard = new Card("Plague", "E", -2, "Event");
        game.handleECardEffects(plagueCard, currentPlayer);

        // Assertions
        assertEquals(3, currentPlayer.getShields(), "Current player should lose 2 shields.");
        assertTrue(outputStream.toString().contains("Card Drawn: Plague card."),
                "Output should indicate the Plague card effect.");
    }

    @Test
    @DisplayName("R-TEST-13: Carrying out Event and discarding E-card logic; Handle Queen's Favor Event Card Effect")
    public void RESP_13_test_02() {
        // Get the current player
        Player currentPlayer = game.getCurrentPlayer();

        // Set the drawn card to Queen's Favor
        Card drawnCard = new Card("Queen's Favor", "Event", 0, "Description");

        // Simulate user input for valid response 'r'
        simulateInput("r\n");

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

        // Simulate user input for valid response 'r'
        simulateInput("r\n");

        // Call the method with the Prosperity card effect
        game.handleECardEffects(prosperityCard, players.get(0)); // Passing any player, as it affects all

        // Assertions
        for (Player player : players) {
            assertEquals(2, player.getHand().size(), "Each player should have drawn 2 adventure cards.");
            assertTrue(outputStream.toString().contains(player.getName() + ", you have drawn 2 adventure cards."),
                    "Output should indicate that each player draws cards due to Prosperity.");
        }
    }


    @Test
    @DisplayName("R-TEST-14: Prompt players for quest sponsor; valid input 'y'")
    public void RESP_14_test_01() {
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player

        // Setup the player's hand with enough Foe cards
        List<Card> hand = Arrays.asList(
                new Card("F5","F",5, "Foe"), new Card("F5","F",5, "Foe"), new Card("F5","F",5, "Foe"), new Card("A5","A",5, "Weapon")
        );
        currentPlayer.setHand(hand);

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Simulate user input for sponsoring the quest
        simulateInput("y\n");
        // Call the method
        boolean result = game.promptToSponsor(currentPlayer);


        // Verify output
        String output = outputStream.toString();
        assertTrue(output.contains(currentPlayer.getName() + " has chosen to sponsor the quest."),
                "Output should confirm the player sponsored the quest.");
        assertTrue(result, "The result should be true for sponsorship.");
    }

    @Test
    @DisplayName("R-TEST-14: Prompt players for quest sponsor; valid input 'n'")
    public void RESP_14_test_02() {
        Player currentPlayer = game.getCurrentPlayer();

        simulateInput("n\n");
        boolean result = game.promptToSponsor(currentPlayer);

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

        // Simulate user input with an invalid response first, then a valid 'no'
        simulateInput("invalid\nn\n");

        // Call the method
        boolean result = game.promptToSponsor(currentPlayer);

        // Verify output
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input. Please enter 'y' or 'n':"),
                "Output should indicate an invalid input.");
        assertTrue(output.contains(currentPlayer.getName() + " has declined to sponsor the quest."),
                "Output should confirm the player declined the sponsorship after the valid input.");
        assertFalse(result, "The result should be false for declining sponsorship.");
    }

    @Test
    @DisplayName("R-TEST-14: Prompt players for quest sponsor; valid input 'y'")
    public void RESP_14_test_04() {
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player

        // Setup the player's hand with not enough Foe cards
        List<Card> hand = Arrays.asList(
                new Card("F5","F",5, "Foe"), new Card("A5","A",5, "Weapon")
        );
        currentPlayer.setHand(hand);

        // Simulate user input for sponsoring the quest, first 'yes' and then 'no'
        simulateInput("y\nn\n");

        // Call the method
        boolean result = game.promptToSponsor(currentPlayer);

        // Verify output
        String output = outputStream.toString();
        assertTrue(output.contains("You do not have enough Foe cards to sponsor the quest."),
                "Output should inform the player about not having enough Foe cards.");
        assertTrue(output.contains(currentPlayer.getName() + " has declined to sponsor the quest. (Ineligibility)"),
                "Output should confirm the player declined to sponsor the quest.");
        assertFalse(result, "The result should be false for sponsorship.");
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

    @Test
    @DisplayName("R-TEST-25: Displaying hand for any player.")
    public void RESP_25_test_01() {
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
        for (Player player : game.getPlayers()) {
            player.receiveCards(testCards);
        }

        // Setup to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Call the method to display the player's hand
        for (Player player : game.getPlayers()) {
            game.displayPlayerHand(player);
            assertFalse(player.getHand().isEmpty(), "The current player's hand should have cards.");
        }

        System.setOut(originalOut);
        String output = outputStream.toString();

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

    }

    @Test
    @DisplayName("R-TEST-26: Turn management for players - Player 1 (P1) to Player 2 (P2)")
    public void RESP_26_test_01() {
        // Initial assertions to check the current player is P1
        assertEquals("P1", game.getCurrentPlayer().getName(), "Current player should be P1.");

        // Simulate user input for valid response 'r'
        simulateInput("r\n");

        // Call nextPlayer to move to the next player
        game.nextPlayer();

        // Assert that the next player is now P2
        assertEquals("P2", game.getCurrentPlayer().getName(), "Current player should be P2.");
        assertTrue(outputStream.toString().contains("Hot Seat (current player): P2"),
                "Output should indicate that the current player is now P2.");
    }

    @Test
    @DisplayName("R-TEST-26: Turn management for players - Player 4 (P4) to Player 1 (P1)")
    public void RESP_26_test_02() {
        // Move to the last player (P4)
        game.setCurrentPlayer(3);

        // Assert that the current player is P4
        assertEquals("P4", game.getCurrentPlayer().getName(), "Current player should be P4.");

        // Simulate user input for valid response 'r'
        simulateInput("r\n");

        // Call nextPlayer to move to the next player, wrapping around
        game.nextPlayer();

        // Assert that the next player is now P1
        assertEquals("P1", game.getCurrentPlayer().getName(), "Current player should wrap around to P1.");
        assertTrue(outputStream.toString().contains("Hot Seat (current player): P1"),
                "Output should indicate that the current player has wrapped around to P1.");
    }


    @Test
    @DisplayName("A-TEST JP-Scenario")
    public void A_TEST_JP_Scenario() {
        simulateInput(
                // Game Start sequence
                "s\n" +   // Start the game
                "e\n" +   // Draw an event card

                // Sponsorship decision
                "n\n" +   // Decline sponsorship
                "y\n" +   // Accept sponsorship

                // Quest setup (Stage 1)
                "1\n" +   // Select card position 1
                "6\n" +   // Select card position 6
                "q\n" +

                // Quest setup (Stage 2)
                "2\n" +   // Select card position 2
                "5\n" +   // Select card position 5
                "q\n" +

                // Quest setup (Stage 3)
                "2\n" +   // Select card position 2
                "2\n" +   // Select card position 2
                "q\n" +

                // Quest setup (Stage 4)
                "1\n" +
                "1\n" +   // Select card position 2
                "4\n" +   // Select card position 5
                "q\n" +

                //Prompt each participant if they want to play the quest
                "y\n" + "y\n" + "y\n" +

                //Prompt each participant if they want to play in the STAGE 1
                "y\n" + "y\n" + "y\n" +

                // discard cards when player draws because they will be playing stage 1
                "1\n" + "1\n" + "1\n" +

                // P1 prepare attack Stage 1
                "5\n" + "9\n" + "q\n" +

                // P3 prepare attack Stage 1
                "4\n" + "10\n" + "q\n" +

                // P4 prepare attack Stage 1
                "6\n" + "7\n" + "q\n" +

                //Prompt each participant if they want to play in the STAGE 2
                "y\n" + "y\n" + "y\n" +

                // P1 prepare attack Stage 2
                "6\n" + "7\n" + "q\n" +

                // P3 prepare attack Stage 2
                "4\n" + "9\n" + "q\n" +

                // P4 prepare attack Stage 2
                "5\n" + "7\n" + "q\n" +

                //Prompt each participant if they want to play in the STAGE 3
                "y\n" + "y\n" +

                // P3 prepare attack Stage 3
                "5\n" + "6\n" + "9\n" + "q\n" +

                // P4 prepare attack Stage 3
                "4\n" + "7\n" + "8\n" + "q\n" +

                //Prompt each participant if they want to play in the STAGE 4
                "y\n" + "y\n" +

                // P3 prepare attack Stage 4
                "6\n" + "7\n" + "8\n" + "q\n" +

                // P4 prepare attack Stage 4
                "4\n" + "5\n" + "6\n" + "8\n" + "q\n" +

                // After Quest ends, P2 has to trim down to 12 cards, discard 4 cards.
                "1\n" + "3\n" + "2\n" + "7\n"

        );


        UserInterface userInterface = new UserInterface(); // Initialize user interface
        userInterface.displayGameStartMessage(true); // Display the game start message

        // Prepare the expected output for Game Start
        String expectedOutput = "***********************************************************\r\n" +
                "          Welcome to the 4004 Assignment 1 Game!          \r\n" +
                "***********************************************************\r\n" +
                "\r\nInstructions:\r\n" +
                "1. 🧙‍♂️ Accumulate 7 shields to become a knight!\r\n" +
                "2. 🃏 Draw adventure cards to complete quests.\r\n" +
                "3. 🎯 Successfully complete quests to earn shields.\r\n" +
                "4. 🏆 Players with 7 or more shields at the end of a quest win!\r\n" +
                "\r\n" +
                "✨ Good luck, and may the best knight prevail! ✨\r\n" +
                "***********************************************************\r\n" +
                "Press 's' to Start Game\r\n" +
                "Press 'q' to Quit Game\r\n"; // Include button instructions in the expected output

        // Assert the Game Start
        assertTrue(outputStream.toString().contains(expectedOutput), "The displayed game start message is incorrect.");

        game = new Game(() -> 0);
        Quest quest = new Quest();

        // Step 1: Start game, decks are created, hands of the 4 players are set up with random cards
        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();

        assertNotNull(game.getAdventureDeck(), "Adventure deck should be initialized.");
        assertNotNull(game.getEventDeck(), "Event deck should be initialized.");
        assertEquals(4, game.getPlayers().size(), "There should be exactly 4 players initialized.");
        assertEquals(52, game.getAdventureDeck().getTotalCards(), "Adventure deck should have 52 cards after initial distribution.");
        assertEquals(17, game.getEventDeck().getTotalCards(), "Event deck should contain 17 cards.");

        //Assert 4 player initialization in game
        assertEquals(4, game.getPlayers().size());
        //Assert current Player is P1 by default
        assertEquals("P1",game.getCurrentPlayer().getName());

        //Assert each player has a hand of 12 cards initially before rigging
        for (Player player : game.getPlayers()) {
            assertEquals(12, game.getPlayerByName(player.getName()).getHand().size());
        }

        // Step 2: Rig the 4 hands to hold the cards of the 4 posted initial hands
        // Setup: Create a list of cards for testing
        List<Card> testCardsP1 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("S10", "S", 10, "Weapon"), //s1
                new Card("S10", "S", 10, "Weapon"), //s2
                new Card("H10", "H", 10, "Weapon"), //s2
                new Card("H10", "H", 10, "Weapon"),
                new Card("D5", "D", 5, "Weapon"), //s1
                new Card("B15", "H", 15, "Weapon"),
                new Card("B15", "D", 15, "Weapon"),
                new Card("L20", "L", 20, "Weapon")
        ));


        // Setup: Create a list of cards for Player 3 (P3)
        List<Card> testCardsP3 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("S10", "S", 10, "Weapon"), //s1
                new Card("S10", "S", 10, "Weapon"), //s2
                new Card("S10", "S", 10, "Weapon"), //s3
                new Card("H10", "H", 10, "Weapon"), //s3
                new Card("H10", "H", 10, "Weapon"),
                new Card("D5", "D", 5, "Weapon"), //s1
                new Card("B15", "B", 15, "Weapon"), //s2
                new Card("L20", "L", 20, "Weapon") //s3
        ));

        // Setup: Create a list of cards for Player 4 (P4)
        List<Card> testCardsP4 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F40", "F", 40, "Foe"),
                new Card("S10", "S", 10, "Weapon"), //s3
                new Card("H10", "H", 10, "Weapon"), //s2
                new Card("H10", "H", 10, "Weapon"), //s1
                new Card("D5", "D", 5, "Weapon"), //s1
                new Card("D5", "D", 5, "Weapon"),
                new Card("B15", "B", 15, "Weapon"), //s2
                //s3
                new Card("L20", "L", 20, "Weapon"), //s3
                new Card("E30", "E", 30, "Weapon")
        ));

        // Setup: Create a list of cards for Player 2 (P2 - Sponsor)
        List<Card> testCardsP2 = new ArrayList<>(Arrays.asList(
                new Card("F5", "F", 5, "Foe"),
                new Card("F10", "F", 10, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("S10", "S", 15, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("L30", "L", 30, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));

        game.getPlayerByName("P1").setClearHand(testCardsP1);
        game.getPlayerByName("P2").setClearHand(testCardsP2);
        game.getPlayerByName("P3").setClearHand(testCardsP3);
        game.getPlayerByName("P4").setClearHand(testCardsP4);

        //Assert each player has a hand of 12 cards after rigging
        for (Player player : game.getPlayers()) {
            assertEquals(12, game.getPlayerByName(player.getName()).getHand().size());
        }

        //Assert the rigged hand of players with above defined list
        // Expected hand for Player 1 (P1)
        List<String> expectedHandP1 = Arrays.asList("F5", "F5", "F15", "F15", "S10", "S10", "H10", "H10", "B15", "D5", "B15", "L20");
        List<String> actualHandP1 = game.getPlayerByName("P1")
                .getHand()
                .stream()
                .map(Card::getCardName)
                .collect(Collectors.toList());
        assertEquals(expectedHandP1, actualHandP1, "The cards in P1's hand do not match the expected hand.");

        // Expected hand for Player 2 (P2)
        List<String> expectedHandP2 = Arrays.asList("F5", "F10", "F15", "F20", "F20", "F20", "S10", "S10", "S10", "B15", "L30", "E30");
        List<String> actualHandP2 = game.getPlayerByName("P2")
                .getHand()
                .stream()
                .map(Card::getCardName)
                .collect(Collectors.toList());
        assertEquals(expectedHandP2, actualHandP2, "The cards in P2's hand do not match the expected hand.");

        // Expected hand for Player 3 (P3)
        List<String> expectedHandP3 = Arrays.asList("F5", "F5", "F5", "F15", "S10", "S10", "S10", "H10", "H10", "D5", "B15", "L20");
        List<String> actualHandP3 = game.getPlayerByName("P3")
                .getHand()
                .stream()
                .map(Card::getCardName)
                .collect(Collectors.toList());
        assertEquals(expectedHandP3, actualHandP3, "The cards in P3's hand do not match the expected hand.");

        // Expected hand for Player 4 (P4)
        List<String> expectedHandP4 = Arrays.asList("F5", "F15", "F15", "F40", "S10", "H10", "H10", "D5", "D5", "B15", "L20", "E30");
        List<String> actualHandP4 = game.getPlayerByName("P4")
                .getHand()
                .stream()
                .map(Card::getCardName)
                .collect(Collectors.toList());
        assertEquals(expectedHandP4, actualHandP4, "The cards in P4's hand do not match the expected hand.");








        //Set Event Deck
        EventDeck eventDeck = game.getEventDeck();
        //Assert Event Deck is populated randomly
        assertEquals(17, game.getEventDeck().getTotalCards());
        //Rigging the Event Deck
        eventDeck.setDeck(Arrays.asList(new Card("Q4", "Q", 4, "Quest")));

        //Set Adventure Deck
        AdventureDeck adventureDeck = game.getAdventureDeck();
        //Assert Adventure Deck is populated randomly
        assertEquals(52, game.getAdventureDeck().getTotalCards()); //since 48 are already distributed
        //Rigging the Adventure Deck
        adventureDeck.clearDeck();
        adventureDeck.setDeck(Arrays.asList(
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                //other random; above are required for playing the given scenario
                new Card("F30", "F", 30, "Foe"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon")
        ));

        Card drewCard = game.drawEventCard();
        assertNotNull(drewCard, "The drawn card should not be null.");
        //Assert the drawn card is the rigged card Q4
        assertEquals("Quest", drewCard.getCategory());
        assertEquals(4, drewCard.getValue());

        game.findSponsor(game.getCurrentPlayer(), game.getPlayers());
        assertEquals("P2",game.getCurrentPlayer().getName()); //sponsor

        assertEquals(12, game.getCurrentPlayer().getHand().size());
        quest.setupQuest(game, drewCard);
        assertEquals(3, game.getCurrentPlayer().getHand().size());
        assertEquals(4, quest.getStages().size());
        Game.clearConsole();

        quest.promptParticipants(game.getPlayers(), game.getCurrentPlayer());
        //Assert P1 declined the offer and is not the sponsor
        assertFalse(game.getPlayerByName("P1").isSponsor());
        //Assert P2 accepted to sponsor the Quest
        assertTrue(game.getPlayerByName("P2").isSponsor());


        for (int i=0; i<drewCard.getValue(); i++){
            quest.prepareForQuest(game, i);

            //Assert that P2 builds the 4 stages as required
            assertEquals(4, quest.getStages().size());

            //Assert eligible Participants for all stages
            if (i == 0 || i == 1) {
                // Assert participants are P1, P3, and P4 for the first two stages
                assertEquals(List.of("P1", "P3", "P4"), quest.getParticipants(), "Expected participants are P1, P3, and P4 for stages 0 and 1");
            } else {
                // Assert participants are P3 and P4 for later stages
                assertEquals(List.of("P3", "P4"), quest.getParticipants(), "Expected participants are P3 and P4 for stages after 1");
            }


            quest.prepareForStage(i, game, quest);
            Game.clearConsole(); //here
            quest.resolveStage(i, game);

            if (i == 0) {
                //Assert drawn card for each player


                //Assert each player's attack values
                //assertEquals(15, game.getPlayerByName("P1").getAttackValue(), "P1's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P3").getAttackValue(), "P3's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P4").getAttackValue(), "P4's attack value in Stage 1 should be 15.");

                //Assert each player now has 2 less cards on hand since they all used 2
                assertEquals(10, game.getPlayerByName("P1").getHand().size());
                assertEquals(10, game.getPlayerByName("P3").getHand().size());
                assertEquals(10, game.getPlayerByName("P4").getHand().size());

            }

            if (i == 1) {
                //Assert drawn card for each player
                //assertEquals("F30", quest.getParticipants().get(0));

                //Assert each player's attack values
                //assertEquals(15, game.getPlayerByName("P1").getAttackValue(), "P1's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P3").getAttackValue(), "P3's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P4").getAttackValue(), "P4's attack value in Stage 1 should be 15.");

                //Assert each player now has 9 cards (12 - S1(2) + Draw 1 - S2(2) = 9)
                assertEquals(9, game.getPlayerByName("P1").getHand().size());
                assertEquals(9, game.getPlayerByName("P3").getHand().size());
                assertEquals(9, game.getPlayerByName("P4").getHand().size());

                //P1 elimination
                assertEquals(0, game.getPlayerByName("P1").getShields(), "P1 should have 0 shields after elimination.");
                // Expected hand for Player 1 (P1)
                List<String> expectedHandP1Eliminate = Arrays.asList("F5", "F10", "F15", "F15", "F30", "H10", "D5", "B15", "L20");
                List<String> actualHandP1Eliminate = game.getPlayerByName("P1")
                        .getHand()
                        .stream()
                        .map(Card::getCardName)
                        .collect(Collectors.toList());
                assertEquals(expectedHandP1Eliminate, actualHandP1Eliminate, "The cards in P1's hand do not match the expected hand.");

            }

            if (i == 2) {
                //Assert drawn card for each player
                //assertEquals("F30", quest.getParticipants().get(0));

                //Assert each player's attack values
                //assertEquals(15, game.getPlayerByName("P1").getAttackValue(), "P1's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P3").getAttackValue(), "P3's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P4").getAttackValue(), "P4's attack value in Stage 1 should be 15.");

                //Assert each player now has 9 cards (12 - S1(2) + Draw 1 - S2(2) + Draws(1) - S3(3)= 7)
                assertEquals(7, game.getPlayerByName("P3").getHand().size());
                assertEquals(7, game.getPlayerByName("P4").getHand().size());

            }

            if (i == 3) {
                //Assert drawn card for each player
                //assertEquals("F30", quest.getParticipants().get(0));

                //Assert each player's attack values
                //assertEquals(15, game.getPlayerByName("P1").getAttackValue(), "P1's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P3").getAttackValue(), "P3's attack value in Stage 1 should be 15.");
                //assertEquals(15, game.getPlayerByName("P4").getAttackValue(), "P4's attack value in Stage 1 should be 15.");

                //Assert each player now has 9 cards (12 - S1(2) + Draw 1 - S2(2) + Draws(1) - S3(3) + Draws(1) - S4(3/4) = 5/4)
                assertEquals(5, game.getPlayerByName("P3").getHand().size());
                assertEquals(4, game.getPlayerByName("P4").getHand().size());

                //P3 elimination
                assertEquals(0, game.getPlayerByName("P3").getShields(), "P3 should have 0 shields after elimination.");
                // Expected hand for Player 3
                List<String> expectedHandP3Eliminate = Arrays.asList("F5", "F5", "F15", "F30", "S10");
                List<String> actualHandP3Eliminate = game.getPlayerByName("P3")
                        .getHand()
                        .stream()
                        .map(Card::getCardName)
                        .collect(Collectors.toList());
                assertEquals(expectedHandP3Eliminate, actualHandP3Eliminate, "The cards in P3's hand do not match the expected hand.");

                //Assert P4 wins - shields
                assertEquals(4, game.getPlayerByName("P4").getShields(), "P4 should have 4 shields after winning Stage 4.");

                // Expected hand for Player 4
                List<String> expectedHandP4Wins = Arrays.asList("F15", "F15", "F40", "L20");
                List<String> actualHandP4Wins = game.getPlayerByName("P4")
                        .getHand()
                        .stream()
                        .map(Card::getCardName)
                        .collect(Collectors.toList());
                assertEquals(expectedHandP4Wins, actualHandP4Wins, "The cards in P4's hand do not match the expected hand.");

            }






        }



        // Assert P2 trims their card to 12 again after getting 13 cards on top of 3 remaining ones
        assertEquals(12, game.getCurrentPlayer().getHand().size(), "P2 should have exactly 12 cards after the quest.");

        assertEquals(4, game.getPlayerByName("P4").getShields());
        assertEquals(0, game.getPlayerByName("P3").getShields());


    }

}