package org.example;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class QuestTest {
    private Game game;
    private Quest quest;
    private Player player;
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        // Initialize the game and current player before each test
        game = new Game();
        quest = new Quest();
        game.initializeGameEnvironment();
        game.initializePlayers();

        // Set up mock player hand for testing
        Player currentPlayer = game.getCurrentPlayer(); // Get the current player
        assertNotNull(currentPlayer, "The current player should not be null.");

        //Setup: Create a list of cards for testing
        List<Card> testCards = new ArrayList<>();
        testCards.add(new Card("F25", "F", 25, "Foe"));
        testCards.add(new Card("F50", "F", 50, "Foe"));
        testCards.add(new Card("S10", "S", 10, "Weapon"));
        testCards.add(new Card("S10", "S", 10, "Weapon"));
        testCards.add(new Card("H10", "H", 10, "Weapon"));
        testCards.add(new Card("H10", "H", 10, "Weapon"));
        testCards.add(new Card("D5", "D", 5, "Weapon"));
        testCards.add(new Card("D5", "D", 5, "Weapon"));
        testCards.add(new Card("L20", "L", 20, "Weapon"));
        testCards.add(new Card("H10", "H", 10, "Weapon"));
        testCards.add(new Card("D5", "D", 5, "Weapon"));
        testCards.add(new Card("F50", "F", 50, "Foe"));

        // Use the receiveCards method to add cards to the player's hand
        //currentPlayer.receiveCards(testCards);

        for (Player player : game.getPlayers()){
            player.receiveCards(testCards);
        }


        // Set number of stages for the quest
        quest.setNumberOfStages(2);

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
    }


    @Test
    @DisplayName("R-Test-16: Implementing Quest logics")
    public void RESP_16_test_01() {
        // Creating a Quest instance
        Quest quest = new Quest("Q3", "Q3", "P1", 3);

        // Manually updating fields after construction for testing
        quest.getParticipants().addAll(Arrays.asList("P1", "P2", "P3"));
        quest.getWinners().addAll(Arrays.asList("P2"));
        quest.setTotalShieldsAwarded(3);
        quest.setCurrentStage(2); // Setting current stage directly
        quest.setStatus("completed"); // Setting status directly
        quest.getDiscardedCards().addAll(Arrays.asList("F10", "H10", "D5"));

        // Testing Quest properties
        assertEquals("Q3", quest.getQuestId(), "Quest ID should be Q3");
        assertEquals("Q3", quest.getQuestType(), "Quest Type should be Q3");
        assertEquals("completed", quest.getStatus(), "Quest status should be completed");
        assertEquals(3, quest.getTotalShieldsAwarded(), "Total Shields Awarded should be 3");
    }

    @Test
    @DisplayName("R-TEST-18: Set up stages of the quest")
    public void RESP_18_test_01() {
        //game.distributeAdventureCards();
        // Simulate user input: choosing two cards and completing each stage
        simulateInput("1\nq\n3\nq\n1\nq\n"); // Ensure this matches the expected input sequence

        List<Card> testCards = new ArrayList<>();
        testCards.add(new Card("F25", "F", 25, "Foe"));
        // Run the setupQuest method
        quest.setupQuest(game, testCards.get(0));

        // Validate that the stages were set up correctly
        assertEquals(2, quest.getStages().size(), "Expected 2 stages to be set up.");

        // Validate the values of the stages
        assertEquals("Stage-1", quest.getStages().get(0).getStageId(), "First stage ID should be 'Stage-1'");
        assertTrue(quest.getStages().get(0).getStageValue() > 0, "Stage value should be greater than 0");

        assertEquals("Stage-2", quest.getStages().get(1).getStageId(), "Second stage ID should be 'Stage-2'");
        assertTrue(quest.getStages().get(1).getStageValue() > quest.getStages().get(0).getStageValue(), "Stage 2 value should be greater than Stage 1 value");
    }

    @Test
    @DisplayName("R-TEST-21: Participant Management for the Quest")
    public void RESP_21_test_01() {
        // Simulating user input for participants: Player1 says yes, Player2 says no
        simulateInput("y\nn\n");

        // Setting up players and sponsor
        Player sponsor = new Player("P1");
        Player player1 = new Player("P2");
        Player player2 = new Player("P3");

        List<Player> players = new ArrayList<>();
        players.add(sponsor);
        players.add(player1);
        players.add(player2);

        Quest quest = new Quest("Q1", "Adventure", sponsor.getName(), 3);
        quest.promptParticipants(players, sponsor);

        // Testing participant list
        List<String> expectedParticipants = List.of("P2"); // Only Player2 joins
        assertEquals(expectedParticipants, quest.getParticipants(), "Participant list should contain only Player2.");
    }

    @Test
    @DisplayName("R-TEST-22: Preparing for the Quest (Drawing cards)")
    public void RESP_22_test_01() {
        quest.getParticipants().add("P1");

        System.out.println("participants: "+ quest.getParticipants());

        String simulatedInput = "1\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        // Each player should draw one card and potentially trim hand
        quest.prepareForQuest(game);

        System.out.println("hand: "+ game.getPlayerByName("P1").getHand());

        assertEquals(12, game.getPlayerByName("P1").getHand().size(), "Player should have trimmed the hand to 12 cards.");
        assertEquals(12, game.getPlayerByName("P2").getHand().size(), "Player should have trimmed the hand to 12 cards.");
        assertEquals(12, game.getPlayerByName("P3").getHand().size(), "Player should have trimmed the hand to 12 cards.");
        assertEquals(12, game.getPlayerByName("P4").getHand().size(), "Player should have trimmed the hand to 12 cards.");

    }

    @Test
    @DisplayName("R-TEST-23: Preparing attacks for each stage")
    public void RESP_23_test_02() {

        Scanner scanner = new Scanner(System.in);

        Quest quest = new Quest("Q1", "Adventure", "P1", 2);
        quest.getParticipants().add(game.getCurrentPlayer().getName());
        System.out.println("Participants: " + quest.getParticipants());

        List<Card> stage1Cards = new ArrayList<>();
        stage1Cards.add(new Card("Sword", "W", 10, "Weapon"));
        stage1Cards.add(new Card("Horse", "H", 15, "Weapon"));

        List<Card> stage2Cards = new ArrayList<>();
        stage2Cards.add(new Card("Bow", "W", 12, "Weapon"));
        stage2Cards.add(new Card("Shield", "H", 18, "Weapon"));

        simulateInput("1\n4\nq\n");

        quest.setNumberOfStages(2);
        quest.getStages().add(new Stage("Stage-1", 20, stage1Cards)); // Stage 1 with value 20
        quest.getStages().add(new Stage("Stage-2", 30, stage2Cards)); // Stage 2 with value 30

        quest.prepareForStage(0, game, quest);  // Stage 0 (Stage-1)

        Stage stage1 = quest.getStages().get(0);
        assertNotNull(stage1.getAttacks().get("P1"), "P1's attack should have been recorded.");
        assertEquals(10, (int) stage1.getAttacks().get("P1"), "P1's attack value should be 10.");

    }

    @Test
    @DisplayName("R-TEST-24: Resolving Stages (Failing and/or Winning)")
    public void RESP_24_test_01() {
        // Setup quest with two stages
        Quest quest = new Quest("Q1", "Adventure", "P1", 2);

        quest.getParticipants().addAll(Arrays.asList("P1", "P2"));

        // Create stage and set attack values for participants
        List<Card> stage1Cards = new ArrayList<>();
        stage1Cards.add(new Card("Sword", "W", 10, "Weapon"));
        stage1Cards.add(new Card("Horse", "H", 15, "Weapon"));
        quest.getStages().add(new Stage("Stage-1", 20, stage1Cards));

        quest.getStages().get(0).getAttacks().put("P1", 25); // P1 passes
        quest.getStages().get(0).getAttacks().put("P2", 15); // P2 fails

        quest.setCardsUsedBySponsor(stage1Cards);

        simulateInput("1\n1\n1\n1\n1\n1\n");

        // Invoke resolveStage() for stage 1
        quest.resolveStage(0, game);

        // Assert that P1 remains in the participants list
        assertTrue(quest.getParticipants().contains("P1"), "P1 should have passed the stage.");
        // Assert that P2 is removed from the participants list
        assertFalse(quest.getParticipants().contains("P2"), "P2 should have failed the stage and been removed.");

        // Assert that P1 progresses to the next stage
        assertTrue(quest.getParticipants().contains("P1"), "P1 should have advanced to the next stage.");
    }

    @Test
    @DisplayName("R-TEST-24: Resolving Stages (Failing and/or Winning) - no participant left test")
    public void RESP_24_test_02() {
        // Setup quest with two stages
        Quest quest = new Quest("Q2", "Adventure", "P3", 2);

        quest.getParticipants().addAll(Arrays.asList("P3", "P4"));

        // Create stage and set attack values for participants (both fail)
        List<Card> stage1Cards = new ArrayList<>();
        stage1Cards.add(new Card("Bow", "W", 12, "Weapon"));
        quest.getStages().add(new Stage("Stage-1", 20, stage1Cards));

        quest.getStages().get(0).getAttacks().put("P3", 10); // P3 fails
        quest.getStages().get(0).getAttacks().put("P4", 15); // P4 fails

        // Capture output stream to verify quest end message
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Invoke resolveStage()
        quest.resolveStage(0, game);

        // Verify no participants left
        assertTrue(quest.getParticipants().isEmpty(), "No participants should remain.");
        // Verify the quest ends without winners
        assertTrue(outContent.toString().contains("No participants left. Quest ends."), "The quest should end without winners.");
    }

    @Test
    @DisplayName("R-TEST-24: Resolving Stages (Failing and/or Winning) - Winners Test")
    public void RESP_24_test_03() {
        // Setup quest with two stages
        Quest quest = new Quest("Q3", "Adventure", "P1", 2);

        quest.getParticipants().addAll(Arrays.asList("P1", "P2"));
        System.out.println("get participants: "+ quest.getParticipants());
        System.out.println("get game players: "+ game.getPlayerByName("P1").getName());

        List<Card> stage1Cards = new ArrayList<>();
        stage1Cards.add(new Card("Bow", "W", 12, "Weapon"));
        stage1Cards.add(new Card("Foe", "F", 10, "Foe"));
        quest.getStages().add(new Stage("Stage-1", 2, stage1Cards));
        quest.getStages().add(new Stage("Stage-1", 2, stage1Cards));

        quest.setCardsUsedBySponsor(stage1Cards);

        simulateInput("1\n1\n1\n1\n1\n1\n");

        quest.resolveWinners(game);

        // Assert that both participants are added to the winners list
        assertTrue(quest.getWinners().contains("P1"), "P1 should be in the winners list.");
        assertTrue(quest.getWinners().contains("P2"), "P2 should be in the winners list.");

        // Assert that players receive shields equal to the number of stages (2 in this case)
        assertEquals(2, game.getPlayerByName("P1").getShields(), "P1 should have 2 shields.");
        assertEquals(2, game.getPlayerByName("P2").getShields(), "P2 should have 2 shields.");
    }

    @Test
    @DisplayName("R-TEST-24: Resolving Stages (Failing and/or Winning) - quest ends without winners")
    public void RESP_24_test_04() {
        // Setup quest
        Quest quest = new Quest("Q5", "Adventure", "P4", 3);

        // Capture output stream to verify quest end message
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Invoke endQuestWithoutWinners()
        quest.endQuestWithoutWinners();

        // Verify that the correct end message is printed
        assertTrue(outContent.toString().contains("QUEST ENDED WITH NO WINNERS"), "The end quest message should be displayed.");
    }

    @Test
    @DisplayName("R-TEST-27: Sponsor Post-Quest Card Set Up (Drawing and Trimming)")
    public void RESP_27_test_01() {
        // Setup quest with two stages
        Quest quest = new Quest("Q1", "Adventure", "P1", 2);

        quest.getParticipants().addAll(Arrays.asList("P1", "P2"));

        // Create stage and set attack values for participants
        List<Card> stage1Cards = new ArrayList<>();
        stage1Cards.add(new Card("Sword", "W", 10, "Weapon"));
        stage1Cards.add(new Card("Horse", "H", 15, "Weapon"));
        quest.getStages().add(new Stage("Stage-1", 20, stage1Cards));

        quest.getStages().get(0).getAttacks().put("P1", 25); // P1 passes
        quest.getStages().get(0).getAttacks().put("P2", 15); // P2 fails

        quest.setCardsUsedBySponsor(stage1Cards);

        simulateInput("1\n1\n1\n1\n1\n1\n");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Invoke resolveStage() for stage 1
        quest.resolveStage(0, game);

        Player sponsor = game.getCurrentPlayer();

        // Assert that P1 remains in the participants list
        assertTrue(quest.getParticipants().contains("P1"), "P1 should have passed the stage.");
        // Assert that P2 is removed from the participants list
        assertFalse(quest.getParticipants().contains("P2"), "P2 should have failed the stage and been removed.");

        // Assert that P1 progresses to the next stage
        assertTrue(quest.getParticipants().contains("P1"), "P1 should have advanced to the next stage and wins!");
        // Simulate resolving winners and post-quest actions

        // Assert that the sponsor drew the correct number of adventure cards
        int expectedCardsToDraw = stage1Cards.size() + quest.getStages().size();
        assertEquals(12, sponsor.getHand().size(),
                "Sponsor should have drawn " + expectedCardsToDraw + " adventure cards after the quest.");

        // Assert that the sponsor's hand is trimmed to 12 cards
        sponsor.getHand().add(new Card("ExtraCard", "X", 1, "Weapon")); // Simulate having more than 12 cards
        sponsor.trimHandTo12Cards(sponsor);
        assertTrue(sponsor.getHand().size() <= 12, "Sponsor's hand should have been trimmed to 12 cards.");

        assertTrue(outputStream.toString().contains("adventure cards as the sponsor!"));

    }










}

