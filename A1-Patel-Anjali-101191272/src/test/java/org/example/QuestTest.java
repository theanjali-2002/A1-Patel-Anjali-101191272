package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestTest {
    private Game game; // Assuming you have a Game class that contains setupQuest()
    private Quest quest;

    @BeforeEach
    public void setUp() {
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

        // Use the receiveCards method to add cards to the player's hand
        currentPlayer.receiveCards(testCards);

        // Set number of stages for the quest
        quest.setNumberOfStages(2);
    }



    @Test
    @DisplayName("R-Test-16: Implementing Quest logics")
    public void RESP_16_test_01() {
        // Creating a Quest instance
        Quest quest = new Quest("Q3", "Q3", "P1", 3);

        // Manually updating fields after construction for testing
        quest.getParticipants().addAll(Arrays.asList("P1", "P2", "P3"));
        quest.getWinners().addAll(Arrays.asList("P2"));
        quest.setTotalShieldsAwarded(3); // Assuming direct access for simplicity in test
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
        String simulatedInput = "1\nq\n2\nq\n"; // Ensure this matches the expected input sequence
        InputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(input);

        // Run the setupQuest method
        quest.setupQuest(game);

        // Validate that the stages were set up correctly
        assertEquals(2, quest.getStages().size(), "Expected 2 stages to be set up.");

        // Validate the values of the stages
        assertEquals("Stage-1", quest.getStages().get(0).getStageId(), "First stage ID should be 'Stage-1'");
        assertTrue(quest.getStages().get(0).getStageValue() > 0, "Stage value should be greater than 0");

        assertEquals("Stage-2", quest.getStages().get(1).getStageId(), "Second stage ID should be 'Stage-2'");
        assertTrue(quest.getStages().get(1).getStageValue() > quest.getStages().get(0).getStageValue(), "Stage 2 value should be greater than Stage 1 value");
    }



}

