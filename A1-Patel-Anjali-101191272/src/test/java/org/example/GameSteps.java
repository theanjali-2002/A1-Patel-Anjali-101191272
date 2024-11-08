package org.example;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;


import static java.lang.System.in;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameSteps {

    private UserInterface userInterface;
    private final InputStream originalIn = in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private Game game;
    private Quest quest;

    @BeforeEach // JUnit setup
    public void junitSetup() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @io.cucumber.java.Before // Cucumber setup
    public void cucumberSetup() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Given("the user interface is initialized")
    public void the_user_interface_is_initialized() {
        userInterface = new UserInterface();
        System.setOut(new PrintStream(outputStream)); // Capture System.out output
    }

    @When("the game start message is displayed with input {string}")
    public void the_game_start_message_is_displayed(String inputSequence) {
        InputStream inputStream = new ByteArrayInputStream(inputSequence.getBytes());
        System.setIn(inputStream);
        userInterface.displayGameStartMessage(true);
    }

    @Then("the welcome message {string} is shown")
    public void the_welcome_message_is_shown(String expectedMessage) {
        String output = outputStream.toString().trim();
        Assert.assertTrue(output.contains(expectedMessage));
    }





    // next scenario 1 =======================================================================>
    @Given("the game is initialized with 4 players and decks are set up")
    public void initializeGameAndDecks() {
        game = new Game(()->0);
        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();
    }

    @And("hands for P1, P2, P3, and P4 are rigged with specified cards")
    public void rigHandsForPlayers() {
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
    }

    @And("event and Adventure decks are rigged")
    public void rigDecksForGame() {
        EventDeck eventDeck = game.getEventDeck();
        eventDeck.setDeck(Arrays.asList(new Card("Q4", "Q", 4, "Quest")));

        AdventureDeck adventureDeck = game.getAdventureDeck();
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
    }

    @When("P1 draws a Quest Q4 card")
    public void p1DrawsQCard() {
        quest = new Quest();
        Card questCard = game.drawEventCard();
        quest.setupQuest(game, questCard);
    }

    @And("P1 declines to sponsor the quest with input {string}")
    public void p1DeclinesToSponsor() {
        game.findSponsor(game.getCurrentPlayer(), game.getPlayers());
        Assertions.assertEquals("P2", game.getCurrentPlayer().getName());
    }

    @And("P2 sponsors and sets up the quest with 4 stages")
    public void p2SponsorsAndSetsUpQuest() {
        // Here we would use methods to simulate P2 setting up the quest stages
    }

    @And("stage {int} proceeds with players drawing and discarding cards as described")
    public void stageProceedsWithDrawingAndDiscarding(int stageNumber) {
        // Implement drawing and discarding as per the requirements for each stage
        // Example for Stage 1:
        if (stageNumber == 1) {
            // P1, P3, and P4 draw cards and trim down
        }
    }

    @And("players P1, P3, and P4 each make attacks and go to the next stage")
    public void playersMakeAttacksStage1() {
        // Example attacks for stage 1, repeated similarly for other stages

    }

    @And("stage {int} proceeds with players drawing, discarding, and making attacks")
    public void stageProceedsWithDrawingAndAttacks(int stageNumber) {
        // Implement the drawing and attacking specifics for stages 2, 3, and 4
    }

    @Then("the final game state should verify")
    public void verifyFinalGameState() {
        // Assertions for the final game state per the scenario
        Assertions.assertEquals(0, game.getPlayerByName("P1").getShields(), "P1 should have no shields.");
        //Assertions.assertEquals(/* specific hand for P1 */, game.getPlayerByName("P1").getHand());

        Assertions.assertEquals(0, game.getPlayerByName("P3").getShields(), "P3 should have no shields.");
        //Assertions.assertEquals(/* specific hand for P3 */, game.getPlayerByName("P3").getHand());

        Assertions.assertEquals(4, game.getPlayerByName("P4").getShields(), "P4 should have 4 shields.");
        //Assertions.assertEquals(/* specific hand for P4 */, game.getPlayerByName("P4").getHand());

        Assertions.assertEquals(12, game.getPlayerByName("P2").getHand().size(), "P2 should have exactly 12 cards.");
    }
}