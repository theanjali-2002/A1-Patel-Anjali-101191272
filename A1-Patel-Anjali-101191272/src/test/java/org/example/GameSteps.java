package org.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.AfterStep;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import io.cucumber.java.After;
import io.cucumber.java.Before;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.in;
import static org.junit.jupiter.api.Assertions.*;

public class GameSteps {

    private UserInterface userInterface;
    private final InputStream originalIn = in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;
    private boolean resetInputStreamAfterStep = true;

    private Game game;
    private Quest quest;
    Card drawnCard;
    AdventureDeck adventureDeck;
    Player player;

    @Before // Cucumber setup
    public void cucumberSetup() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterStep // Cucumber setup
    public void cucumberSetupRestore() {
        if (resetInputStreamAfterStep) {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        resetInputStreamAfterStep = true;
    }

    @After // Cucumber setup
    public void cucumberSetupRestoreScenario() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void simulateInput(String input) {
        input = input.replace("\\n", "\n");
        ScannerSingleton.resetScanner(new ByteArrayInputStream(input.getBytes()));
        System.setIn(in); // Set System.in to new ByteArrayInputStream
    }

    public List<Card> getSortedHand(List<Card> handToSort) {
        // Separate foes and weapons into different lists
        List<Card> foes = new ArrayList<>();
        List<Card> weapons = new ArrayList<>();

        for (Card card : handToSort) {
            if (card.getCategory().equals("Foe")) {
                foes.add(card);
            } else if (card.getCategory().equals("Weapon")) {
                weapons.add(card);
            }
        }

        // Sort foes by value
        foes.sort(Comparator.comparingInt(Card::getValue));

        // Sort weapons: prioritize swords before horses, then by value
        weapons.sort(Comparator.comparing((Card card) -> {
            if (card.getType().equals("S")) {
                return 0; // Swords come first
            } else if (card.getType().equals("H")) {
                return 1; // Horses come last
            } else {
                return 2; // Other weapon types
            }
        }).thenComparingInt(Card::getValue)); // Sort by value

        // Combine sorted foes and weapons into a new sorted hand
        List<Card> sortedHand = new ArrayList<>(foes);
        sortedHand.addAll(weapons);

        return sortedHand; // Return the sorted hand
    }


    @Given("the user interface is initialized")
    public void the_user_interface_is_initialized() {
        resetInputStreamAfterStep = false;
        userInterface = new UserInterface();
        System.setOut(new PrintStream(outputStream)); // Capture System.out output
    }

    @When("the game start message is displayed with input {string}")
    public void the_game_start_message_is_displayed(String inputSequence) {
        resetInputStreamAfterStep = false;
        simulateInput(inputSequence);
        userInterface.displayGameStartMessage(true);
    }

    @Then("the game start {string} is shown")
    public void the_welcome_message_is_shown(String expectedMessage) {
        resetInputStreamAfterStep = false;
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

    @And("hands for all players are rigged with specified cards")
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
                new Card("L20", "L", 20, "Weapon"),
                new Card("E30", "E", 30, "Weapon")
        ));

        game.getPlayerByName("P1").setClearHand(testCardsP1);
        game.getPlayerByName("P2").setClearHand(testCardsP2);
        game.getPlayerByName("P3").setClearHand(testCardsP3);
        game.getPlayerByName("P4").setClearHand(testCardsP4);

        //Clear the received cards list for testing purposes:
        game.getPlayerByName("P1").clearReceivedCardEvents();
        game.getPlayerByName("P2").clearReceivedCardEvents();
        game.getPlayerByName("P3").clearReceivedCardEvents();
        game.getPlayerByName("P4").clearReceivedCardEvents();
    }

    @And("event and Adventure decks are rigged")
    public void rigDecksForGame() {
        EventDeck eventDeck = game.getEventDeck();
        eventDeck.setDeck(Arrays.asList(new Card("Q4", "Q", 4, "Quest")));

        adventureDeck = game.getAdventureDeck();
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



    // SCENARIO 2 ===============================================================>
    @And("quest event cards and Adventure decks are rigged")
    public void rigQuestDeckForGame() {
        EventDeck eventDeck = game.getEventDeck();
        List<Card> deck = new ArrayList<>();
        deck.add(new Card("Q3", "Q", 3, "Quest"));
        deck.add(new Card("Q4", "Q", 4, "Quest"));
        Collections.reverse(deck);
        eventDeck.setDeck(deck);

        adventureDeck = game.getAdventureDeck();
        adventureDeck.clearDeck();
        adventureDeck.setDeck(Arrays.asList(
                new Card("D5", "D", 5, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                //above are required for playing the given quest Q4
                new Card("F10", "F", 10, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                // P1 gets above for Q4
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


















    @When("player draws the rigged event card")
    public void p1DrawsQCard() {
        String inputSequence = "e\n";
        simulateInput(inputSequence);
        quest = new Quest();
        drawnCard = game.drawEventCard();
        System.out.println("debug quest drawn: " + drawnCard);
        resetInputStreamAfterStep = true;
    }

    @Then("player {string} becomes the sponsor")
    public void playerBecomesSponsor(String expectedSponsor) {
        List<Player> players = game.getPlayers();
        int startingIndex = -1;

        // Find the index of the starting player
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(game.getCurrentPlayer().getName())) {
                startingIndex = i;
                break;
            }
        }

        StringBuilder inputSequence = new StringBuilder();
        int playerCount = players.size();

        // Loop through players starting from the current player and wrapping around
        for (int i = 0; i < playerCount; i++) {
            Player player = players.get((startingIndex + i) % playerCount);
            if (player.getName().equals(expectedSponsor)) {
                inputSequence.append("y\n"); // Sponsor agrees
                break;
            } else {
                inputSequence.append("n\n"); // Other players decline
            }
        }

        simulateInput(inputSequence.toString());
        game.findSponsor(game.getCurrentPlayer(), game.getPlayers());
        assertEquals(expectedSponsor, game.getCurrentPlayer().getName());
    }


    @And("sponsor sets up the {int} stages of quest with input {string}")
    public void p2SetsUpQuest(int stageNumber, String inputSequence) {
        String[] parts = inputSequence.split("\\\\n");
        int count = 0;
        for (String part : parts) {
            System.out.println("part debug: "+ part);
            if (!part.isEmpty() && !part.equals("q")) {
                count++;
            }
        }
        System.out.println("count debug: "+ count);
        int initialHand = game.getCurrentPlayer().getHand().size();
        int finalCount = initialHand - count;

        simulateInput(inputSequence);
        quest.setupQuest(game, drawnCard);
        assertEquals(game.getCurrentPlayer().getHand().size(), finalCount);
        assertEquals(quest.getStages().size(), stageNumber); //total stages set up
    }

    @And("players are asked to participate in the Quest and declines are from {string}")
    public void playersJoinQuest(String decliningPlayers) {
        List<String> decliners = decliningPlayers.isEmpty() ? new ArrayList<>() : Arrays.asList(decliningPlayers.split(","));
        StringBuilder inputSequence = new StringBuilder();

        Player currentPlayer = game.getCurrentPlayer();

        for (Player player : game.getPlayers()) {
            // Skip the current player (sponsor)
            if (player.equals(currentPlayer)) {
                continue;
            }

            if (decliners.contains(player.getName())) {
                inputSequence.append("n\n"); // Player declines
            } else {
                inputSequence.append("y\n"); // Player joins
            }
        }

        simulateInput(inputSequence.toString());
        quest.promptParticipants(game.getPlayers(), currentPlayer);
    }


    @And("stage {int} proceeds with eligible players {string} where {string} declines, each discarding {string}")
    public void stageProceeds(int stageNumber, String eligiblePlayers, String decliningPlayers, String discardCards) {
        List<String> eligiblePlayerList = Arrays.asList(eligiblePlayers.split(","));
        List<String> decliningPlayerList = decliningPlayers.isEmpty() ? new ArrayList<>() : Arrays.asList(decliningPlayers.split(","));
        List<String> cardsToDiscard = discardCards.isEmpty() ? new ArrayList<>() : Arrays.asList(discardCards.split(","));

        StringBuilder inputSequence = new StringBuilder();

        // Simulate joining input for eligible players and declining input for others
        for (String playerName : eligiblePlayerList) {
            if (eligiblePlayerList.contains(playerName)) {
                inputSequence.append("y\n");
            } else if (decliningPlayerList.contains(playerName)) {
                inputSequence.append("n\n");
            }
        }

        // If there are cards to discard, find the index for each eligible player's discard card
        if (!cardsToDiscard.isEmpty()) {
            for (int i = 0; i < eligiblePlayerList.size(); i++) {
                String playerName = eligiblePlayerList.get(i);
                String cardName = cardsToDiscard.get(i);
                Player player = game.getPlayerByName(playerName);

                // Find the card in the player's hand and get its index
                int cardIndex = -1;
                for (int j = 0; j < player.getHand().size(); j++) {
                    if (player.getHand().get(j).getCardName().equals(cardName)) {
                        cardIndex = j + 1; // Convert to 1-based index
                        break;
                    }
                }

                if (cardIndex != -1) {
                    inputSequence.append(cardIndex).append("\n");
                }
            }
        }

        simulateInput(inputSequence.toString());
        quest.prepareForQuest(game, stageNumber - 1);
        assertEquals(eligiblePlayerList, quest.getParticipants());
    }


    @And("all players make attacks for Stage {int} as given:")
    public void playersMakeAttacksForStage(int stageNumber, DataTable dataTable) {
        StringBuilder inputSequence = new StringBuilder();

        // Convert DataTable to a list of maps where each row represents a player and their cards
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String playerName = row.get("Player").trim();
            String cardList = row.get("Cards").trim();

            List<String> cardNames = cardList.equalsIgnoreCase("none") ? new ArrayList<>() : Arrays.asList(cardList.split(","));
            Player player = game.getPlayerByName(playerName);

            // Find the index of each card in the player's hand and add to input sequence
            for (String cardName : cardNames) {
                int cardIndex = -1;
                for (int i = 0; i < player.getHand().size(); i++) {
                    if (player.getHand().get(i).getCardName().equals(cardName.trim())) {
                        cardIndex = i + 1; // Convert to 1-based index
                        break;
                    }
                }
                if (cardIndex != -1) {
                    inputSequence.append(cardIndex).append("\n");
                }
            }

            // Append "q\n" after each player's attack sequence
            inputSequence.append("q\n");
        }

        simulateInput(inputSequence.toString());
        quest.prepareForStage(stageNumber-1, game, quest);
        Game.clearConsole();
    }









    @And("each player draws card {string} for Stage {int}")
    public void eachPlayerDrawsCard(String cardNames, int stage) {
        List<String> cardList = Arrays.asList(cardNames.split(","));
        List<String> participants = quest.getParticipants();
        System.out.println("debug participants: " + participants);
        for (int i = 0; i < participants.size(); i++) {
            String playerName = participants.get(i);
            System.out.println("Debug player name: "+ playerName + "... Also, i=" + i);
            assertEquals(List.of(cardList.get(i)), game.getPlayerByName(playerName).getReceivedCardEvents().get(stage));
        }
    }

    @And("each player prepares attack of {string} for Stage {int}")
    public void eachPlayerPreparesAttack(String attackValues, int stage) {
        List<Integer> attackPoints = Arrays.stream(attackValues.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        int i = 0;
        for (String playerName : quest.getParticipants()) {
            assertEquals(attackPoints.get(i), game.getPlayerByName(playerName).getStageAttackValues().get("Stage-"+stage));
            i++;
        }
    }

    @And("resolve stage {int} to check each player is left with {string} cards")
    public void eachPlayerIsLeftWithCardsOnTheirHand(int stageNumber, String remainingCards) {
        List<Integer> cardsLeft = Arrays.stream(remainingCards.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        int i = 0;
        for (String playerName : quest.getParticipants()) {
            assertEquals(cardsLeft.get(i), game.getPlayerByName(playerName).getHand().size());
            i++;
        }
        quest.resolveStage(stageNumber - 1, game);
        System.out.println("debug sponsor hand resolve: "+ game.getCurrentPlayer().getHand());
    }


    @And("for stage {int} sponsor trims their hand by discarding {string}")
    public void sponsorTrimsHand(int stageNumber, String cardsToDiscard) {
        List<String> cards = Arrays.asList(cardsToDiscard.split(","));
        StringBuilder inputSequence = new StringBuilder();

        Player sponsor = game.getCurrentPlayer();  // Assuming the sponsor is the current player
        int handsDrawn = stageNumber+(12-sponsor.getHand().size());
        List<Card> hand = new ArrayList<>(sponsor.getHand());  // Create a mutable copy of the hand
        List<Card> aDeck = new ArrayList<>(adventureDeck.getCards().subList(0, handsDrawn));
        hand.addAll(aDeck.subList(0, handsDrawn));
        hand = getSortedHand(hand);

        System.out.println("debug card name: "+ sponsor.getHand());
        System.out.println("my dummy hand: "+ hand);

        for (String cardName : cards) {
            int indexToDiscard = -1;

            // Find the index of the current card in the hand
            for (int i = 0; i < hand.size(); i++) {
                //System.out.println("debug trim hand card name: "+ hand.get(i).getClass().getSimpleName());
                if (hand.get(i).getCardName().equals(cardName)) {
                    indexToDiscard = i;
                    break;
                }
            }

            // If card is found, add its index to the input sequence and simulate removal
            if (indexToDiscard != -1) {
                inputSequence.append(indexToDiscard + 1).append("\n");  // Convert to 1-based index
                hand.remove(indexToDiscard);  // Remove the card to adjust future indices
            }
        }

        System.out.println("debug sequence: "+ inputSequence.toString());
        simulateInput(inputSequence.toString());
        System.out.println("debug sponsor hand trim: "+ game.getCurrentPlayer().getHand());

    }



    @And("the final game state should verify sponsor with trimmed hand with {int} cards")
    public void verifyFinalGameState(int handSize) {
        System.out.println("debug sponsor: "+ game.getCurrentPlayer().getName());
        System.out.println("debug sponsor hand: "+ game.getCurrentPlayer().getHand());
        Assertions.assertEquals(handSize, game.getCurrentPlayer().getHand().size(), "Sponsor should have exactly 12 cards.");
    }

    @And("player {string} has {int} shields with hand {string}")
    public void verifyPlayerShieldsAndHand(String playerName, int shieldCount, String expectedHand) {
        Player player = game.getPlayerByName(playerName);
        Assertions.assertEquals(shieldCount, player.getShields(),
                playerName + " should have " + shieldCount + " shields.");

        if (!expectedHand.isEmpty()) {
            List<String> expectedHandCards = Arrays.asList(expectedHand.split(","));
            List<String> actualHandCards = player.getHand()
                    .stream()
                    .map(Card::getCardName)
                    .collect(Collectors.toList());

            Assertions.assertEquals(expectedHandCards, actualHandCards,
                    "The cards in " + playerName + "'s hand do not match the expected hand.");
        }

    }


    // SCENARIO 4 =================================================================>
    @And("event Q2 and Adventure decks are rigged")
    public void rigEDeckForGame() {
        EventDeck eventDeck = game.getEventDeck();
        eventDeck.setDeck(Arrays.asList(new Card("Q2", "Q", 2, "Quest")));

        adventureDeck = game.getAdventureDeck();
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


    // SCENARIO 3 ===============================================================>
    @And("multiple event cards and Adventure decks are rigged")
    public void rigEVENTDeckForGame() {
        EventDeck eventDeck = game.getEventDeck();
        List<Card> deck = new ArrayList<>();
        deck.add(new Card("Q3", "Q", 3, "Quest"));
        deck.add(new Card("Queen's Favor", "E", 2, "Event"));
        deck.add(new Card("Prosperity", "E", 2, "Event"));
        deck.add(new Card("Plague", "E", -2, "Event"));
        deck.add(new Card("Q4", "Q", 4, "Quest"));
        Collections.reverse(deck);
        eventDeck.setDeck(deck);

        adventureDeck = game.getAdventureDeck();
        adventureDeck.clearDeck();
        adventureDeck.setDeck(Arrays.asList(
                new Card("D5", "D", 5, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("F10", "F", 10, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("L20", "L", 20, "Weapon"),
                new Card("H10", "H", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("F30", "F", 30, "Foe"),
                new Card("L20", "L", 20, "Weapon"),
                //above are required for playing the given quest Q4
                new Card("F10", "F", 10, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F5", "F", 5, "Foe"),
                new Card("F15", "F", 15, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F20", "F", 20, "Foe"),
                new Card("F30", "F", 30, "Foe"),
                new Card("D5", "D", 5, "Weapon"),
                new Card("S10", "S", 10, "Weapon"),
                new Card("B15", "B", 15, "Weapon"),
                // P1 gets above for Q4
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

    @And("quest winners found OR no one sponsored the quest returning to next hot seat player {string}")
    public void nextHotSeat(String input) {
        simulateInput(input);
        game.nextHotSeatPlayer();
        System.out.println("Debug current hot seat: "+ game.getCurrentPlayer().getName());
    }

    @And("event card Plague is drawn by player {string}")
    public void plagueCardDrawn(String currentPlayer) {
        String returnSign = "r\n";
        simulateInput(returnSign);
            int initialShields = game.getPlayerByName(currentPlayer).getShields();

            // Apply Plague effect
            game.handleECardEffects(drawnCard, game.getPlayerByName(currentPlayer));

            // Assert shields decrease by 2
            assertEquals(initialShields - 2, game.getPlayerByName(currentPlayer).getShields());
    }

    @And("event card Prosperity is drawn by player {string} and each player discards:")
    public void prosperityCardDrawn(String currentPlayer, DataTable dataTable) {
        StringBuilder inputSequence = new StringBuilder();
        Map<String, Integer> initialHandSizes = new HashMap<>();

        // Store each player's initial hand size
        for (Player player : game.getPlayers()) {
            initialHandSizes.put(player.getName(), player.getHand().size());
        }

        // Parse the DataTable and process discards for each player
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String playerName = row.get("Player").trim();
            String cardList = row.get("Cards").trim();

            List<String> cardNames = cardList.equalsIgnoreCase("none") ? new ArrayList<>() : Arrays.asList(cardList.split(","));
            Player player = game.getPlayerByName(playerName);

            // For each card, find the index in the player's hand and add to input sequence
            for (String cardName : cardNames) {
                int cardIndex = -1;
                for (int i = 0; i < player.getHand().size(); i++) {
                    if (player.getHand().get(i).getCardName().equals(cardName.trim())) {
                        cardIndex = i + 1; // Convert to 1-based index
                        break;
                    }
                }
                if (cardIndex != -1) {
                    inputSequence.append(cardIndex).append("\n");
                }
            }
        }

        // Append "r\n" for hot seat player
        inputSequence.append("r\n");
        // Simulate the generated input sequence for discarding and returning
        System.out.println("debug prosper input: "+ inputSequence.toString());
        simulateInput(inputSequence.toString());

        // Apply Prosperity effect
        game.handleECardEffects(drawnCard, game.getPlayerByName(currentPlayer));

        // Assert each player's hand size increased by up to 2 cards, capped at 12
        for (Player player : game.getPlayers()) {
            int initialHand = initialHandSizes.get(player.getName());
            int expectedHandSize = Math.min(initialHand + 2, 12);
            assertEquals(expectedHandSize, player.getHand().size());
        }
    }










    @And("event card Queen's Favor is drawn by player {string} who discards {string}")
    public void queensFavorCardDrawn(String currentPlayer, String cardsToDiscard) {
        List<String> cards = cardsToDiscard.isEmpty() ? new ArrayList<>() : Arrays.asList(cardsToDiscard.split(","));
        StringBuilder inputSequence = new StringBuilder();

        Player player = game.getPlayerByName(currentPlayer);
        List<Card> hand = new ArrayList<>(player.getHand());  // Create a mutable copy of the player's hand
        List<Card> aDeck = new ArrayList<>(adventureDeck.getCards().subList(0, 2));
        hand.addAll(aDeck.subList(0, 2));
        hand = getSortedHand(hand);

        // Step 1: Find and simulate discards for specified cards, if any
        if (!cards.isEmpty()) {
            for (String cardName : cards) {
                int indexToDiscard = -1;

                // Find the index of the current card in the hand
                for (int i = 0; i < hand.size(); i++) {
                    if (hand.get(i).getCardName().equals(cardName)) {
                        indexToDiscard = i;
                        break;
                    }
                }

                // If card is found, add its index to the input sequence and simulate removal
                if (indexToDiscard != -1) {
                    inputSequence.append(indexToDiscard + 1).append("\n");  // Convert to 1-based index
                    hand.remove(indexToDiscard);  // Remove the card to adjust future indices
                }
            }
        }

        inputSequence.append("r\n");
        // Step 2: Simulate the input sequence for discarding, if any
        if (!inputSequence.isEmpty()) {
            simulateInput(inputSequence.toString());
        }

        // Step 3: Apply Queen's Favor effect (draw up to 2 cards with a maximum hand size of 12)
        int currentHandSize = player.getHand().size();
        game.handleECardEffects(drawnCard, player);

        // Step 4: Assert the player's hand size increased correctly
        int expectedHandSize = Math.min(currentHandSize + 2, 12);
        assertEquals(expectedHandSize, player.getHand().size());
    }


    @And("player {string} declared as game winner")
    public void playersDeclaredAsWinners(String players) {
        // Split the string by commas to handle multiple players
        List<String> winners = Arrays.asList(players.split(","));

        // Loop through each player to assert their winner status
        for (String playerName : winners) {
            playerName = playerName.trim();  // Ensure no extra whitespace

            // Retrieve the player and check if they are declared as a winner
            Player player = game.getPlayerByName(playerName);
            assertTrue(player.getShields() >= 7, "Player's hand size should be 7 or more");

            // Optional: Print out confirmation for debugging
            System.out.println(playerName + " is declared as a winner with " + player.getShields() + " shields!");
        }
    }



}