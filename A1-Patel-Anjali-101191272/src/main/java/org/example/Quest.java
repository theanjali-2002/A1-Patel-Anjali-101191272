package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Quest {
    private String questId;
    private String questType;
    private String sponsorId;
    private int numberOfStages;
    private List<String> participants;
    private List<String> winners;
    private int totalShieldsAwarded;
    private int currentStage;
    private String status;
    private List<String> discardedCards;
    private List<Card> cardDeck;
    private List<Stage> stages;
    private Game game;
    private AdventureDeck adventureDeck;
    private EventDeck eventDeck;
    private Player player;
    private Stage stage;
    private Quest quest;
    private List<Card> cardsUsedBySponsor; // Track all cards used by the sponsor


    public Quest() {
        this.participants = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.discardedCards = new ArrayList<>();
        this.cardDeck = new ArrayList<>();
        this.stages = new ArrayList<>();
        this.cardsUsedBySponsor = new ArrayList<>();
        game = new Game();
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
        player = new Player();
        stage = new Stage();
    }

    public Quest(String questId, String questType, String sponsorId, int numberOfStages) {
        this.questId = questId;
        this.questType = questType;
        this.sponsorId = sponsorId;
        this.numberOfStages = numberOfStages;
        this.participants = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.discardedCards = new ArrayList<>();
        this.currentStage = 0; // Starting at the first stage
        this.status = "active"; // Default status
        this.totalShieldsAwarded = 0;
        this.cardDeck = new ArrayList<>();
        this.stages = new ArrayList<>();
        game = new Game();
    }

    public void setupQuest(Game game, Card drawnQcard) {
        setNumberOfStages(drawnQcard.getValue());
        System.out.println("num of stages: "+ numberOfStages);
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < numberOfStages; i++) {
            System.out.println("Setting up Stage " + (i + 1));
            String stageId = "Stage-" + (i + 1);
            List<Card> cardsInStage = new ArrayList<>();
            int stageValue = 0;

            // Display the current player's hand before adding to the stage
            game.displayCurrentPlayerHand();

            boolean validStageSetup = false;
            boolean hasFoeCard = false;
            while (!validStageSetup) {
                System.out.println("Enter the position of the next card to include or 'q' to finish this stage:");

                // Check for available input
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();

                    if (input.equalsIgnoreCase("q")) {
                        if (cardsInStage.isEmpty()) {
                            System.out.println("A stage cannot be empty");
                        } else if (stageValue <= (i > 0 ? stages.get(i - 1).getStageValue() : 0)) {
                            System.out.println("Insufficient value for this stage");
                        } else {
                            for (Card card : cardsInStage) {
                                if (card.getCategory().equals("Foe")) {
                                    hasFoeCard = true;
                                    break;
                                }
                            }
                            if (!hasFoeCard) {
                                System.out.println("You cannot quit; at least one card with category 'Foe' is required in this stage.");
                            } else {
                                validStageSetup = true; // Stage is valid
                            }
                        }
                    } else {
                        // Validate the card position
                        try {
                            int cardPosition = Integer.parseInt(input); // Convert to int
                            Card card = getCard(cardPosition - 1, game); // Assuming getCard takes position from hand

                            if (card != null && isValidCard(card, cardsInStage)) {
                                cardsInStage.add(card);
                                cardsUsedBySponsor.add(card);
                                System.out.println("Added card: " + card);

                                // Print the cards currently in the stage
                                System.out.println("Current cards in Stage " + (i + 1) + ": " + cardsInStage);

                                stageValue = calculateStageValue(cardsInStage); // Update stage value

                                // Discard the card from player's hand
                                game.getCurrentPlayer().discardACardFromHand(card);

                                // Print updated player's hand
                                game.displayCurrentPlayerHand();
                            } else {
                                System.out.println("Invalid card selection. Must be a sole foe or non-repeated weapon card.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid position. Please enter a valid card position.");
                        }
                    }
                } else {
                    System.out.println("No input available. Exiting stage setup.");
                    return; // Handle the situation where there's no input
                }
            }

            // After building the stage, set its value based on the cards
            Stage stage = new Stage(stageId, stageValue, cardsInStage);
            stages.add(stage);

            // Print details of the created stage
            System.out.println("Stage created: " + stage.getStageId());
        }
        System.out.println("Quest setup complete with " + stages.size() + " stages.");
    }


    private Card getCard(int position, Game game) {
        cardDeck = game.getCurrentPlayer().getHand();
        // Check if the position is within the valid range
        if (position < 0 || position >= cardDeck.size()) {
            System.out.println("Invalid position: " + position + ". It must be between 0 and " + (cardDeck.size() - 1) + ".");
            return null; // Return null if the position is invalid
        }

        System.out.println(cardDeck.get(position).getCardName());
        // Return the card from the deck based on the position
        return cardDeck.get(position);

    }


    // Method to check if the selected card is valid
    private boolean isValidCard(Card card, List<Card> existingCards) {
        // Check if the card is non-repeated and if it's a valid type
        return !existingCards.contains(card) && (card.getCategory().equals("Weapon") || card.getCategory().equals("Foe"));
    }

    // Method to calculate the stage value based on cards
    private int calculateStageValue(List<Card> cards) {
        int totalValue = 0;
        for (Card card : cards) {
            totalValue += card.getValue();
        }
        return totalValue; // Return the total value of the stage
    }

    public void promptParticipants(List<Player> players, Player sponsor) {
        Scanner scanner = new Scanner(System.in);
        for (Player player : players) {
            if (!player.equals(sponsor)) {
                System.out.println(player.getName() + ", do you want to participate in the quest? (y/n)");
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("y")) {
                    participants.add(player.getName());
                    System.out.println(player.getName() + " joined the quest.");
                } else {
                    System.out.println(player.getName() + " declined to join the quest.");
                }
            }
        }
    }

    public void prepareForQuest(Game game) {
        for (String participant : participants) {
            Player player = game.getPlayerByName(participant); // Retrieve the player from game
            List<Card> drawnCards = game.getAdventureDeck().drawACards(1);
            player.receiveCards(drawnCards); // Each participant draws one adventure card
            player.trimHandTo12Cards(); // Ensure they don’t exceed card limit
        }
    }

    public void prepareForStage(int stageIndex, Game game, Quest quest) {
        Stage stage = stages.get(stageIndex);
        Iterator<String> iterator = participants.iterator(); // Create an iterator for the participants list

        while (iterator.hasNext()) {
            String participant = iterator.next(); // Get the next participant
            System.out.println("Participant in method: " + participant);
            Player player = game.getPlayerByName(participant);

            if (player == null) {
                System.out.println("Error: Player '" + participant + "' could not be found in the game.");
                iterator.remove(); // Remove participant if player not found
                continue; // Skip to the next participant
            }

            // Prepare the player's attack for the stage
            try {
                int attackValue = player.prepareAttackForStage(stage, player);
                if (attackValue == 0) { // Check if the attack value is 0 (indicating player couldn't attack)
                    System.out.println(participant + " cannot attack and will be removed from participants.");
                    iterator.remove(); // Remove participant if they cannot attack
                } else {
                    stage.recordAttack(participant, attackValue);
                }
            } catch (Exception e) {
                System.out.println("\n");
            }
        }
    }



    public void resolveStage(int stageIndex, Game game) {
        Stage stage = stages.get(stageIndex);

        // First check if there are participants
        if (participants.isEmpty()) {
            System.out.println("No participants left. Quest ends.");
            endQuestWithoutWinners();  // End the quest if no participants remain
            //game.nextPlayer();
            return;  // Exit early since there are no participants to process
        }

        // Iterate over a copy of the participants list to safely remove participants
        for (String participant : new ArrayList<>(participants)) {
            int attackValue = stage.getAttacks().get(participant);
            if (attackValue < stage.getStageValue()) {
                System.out.println(participant + " failed to pass stage " + (stageIndex + 1));
                participants.remove(participant);  // Remove the participant if attack value is less than stage value
            }
        }

        // Check if participants remain after processing
        if (participants.isEmpty()) {
            System.out.println("No participants left. Quest ends.");
            endQuestWithoutWinners();  // End the quest if no participants remain after attack resolution
            //game.nextPlayer();
        } else if (stageIndex + 1 < stages.size()) {
            //prepareForStage(stageIndex + 1, game, quest);  // Move to the next stage if there are more stages
            System.out.println("\n");
        } else {
            resolveWinners(game);  // If final stage is completed, resolve the winners
        }

    }

    public void resolveWinners(Game game) {
        // The participants who have completed all stages are the winners of the quest.
        System.out.println("Resolving winners...");
        int shieldReward = stages.size(); // Each winner gets shields equal to the number of stages
        System.out.println("total shields: "+ shieldReward);

        if (participants.isEmpty()) {
            System.out.println("No participants left to resolve winners.");
            endQuestWithoutWinners(); // No participants left means no winners
            //game.nextPlayer();
            return;
        }

        // Iterate over remaining participants and award them shields
        for (String participant : participants) {
            Player winner = game.getPlayerByName(participant); // Fetch the player from the game
            if (winner != null) {
                winner.gainShields(shieldReward); // Add shields to the winner's score
                System.out.println(participant + " has won the quest and earned " + shieldReward + " shields!");

                // Add the winner to the Quest's winners list
                winners.add(participant);

                // Optionally, check if this player has won the game (>= 7 shields)
                if (winner.getShields() >= 7) {
                    System.out.println(participant + " has accumulated 7 or more shields and is one of the game's winners!");
                }
            } else {
                System.out.println("Error: Participant " + participant + " could not be found in the game.");
            }
        }
        // Print all winners of the quest
        System.out.println("Quest Winners: " + winners);

        // Check if the game has a winner
        boolean gameWon = winners.stream()
                .anyMatch(winner -> game.getPlayerByName(winner).getShields() >= 7);

        if (!gameWon) {
            // Sponsor discards all cards used to build the quest
            Player sponsor = game.getCurrentPlayer(); // Assuming there's a method to get the sponsor
            int questCards = cardsUsedBySponsor.size();

            // Sponsor draws the same number of adventure cards + additional cards for each stage
            int stagesCount = stages.size();
            int totalCardsToDraw = questCards + stagesCount;
            List<Card> drawnCards = game.getAdventureDeck().drawACards(totalCardsToDraw);
            sponsor.receiveCards(drawnCards);
            System.out.println(sponsor.getName() + " draws " + totalCardsToDraw + " adventure cards as the sponsor!\n");

            // If necessary, the sponsor trims their hand to 12 cards
            System.out.println(sponsor.getName() + "'s hand has to be reduced to 12 cards!\n");
            sponsor.trimHandTo12Cards();
        }

        // The quest ends after resolving the winners
        System.out.println("The quest has ended. The game will continue.");
        //game.nextPlayer();
    }

    public void endQuestWithoutWinners() {
        // Fancy ASCII art to enhance the UI
        System.out.println("*****************************************************");
        System.out.println("*                                                   *");
        System.out.println("*        ⚔️ QUEST ENDED WITH NO WINNERS ⚔️          *");
        System.out.println("*                                                   *");
        System.out.println("*****************************************************");

        System.out.println("\n\n      ────── ✦ ────── ");
        System.out.println("      Darkness falls upon the land...");
        System.out.println("      The brave heroes have failed to complete the quest.");
        System.out.println("      All hope is lost... until the next adventure!");
        System.out.println("      ────── ✦ ────── ");

        System.out.println("\nReturning to the main game flow...\n");
    }



    // Getters and Setters
    public String getQuestId() {
        return questId;
    }

    public String getQuestType() {
        return questType;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public int getNumberOfStages() {
        return numberOfStages;
    }

    public void setNumberOfStages(int num) {
        this.numberOfStages = num;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public List<String> getWinners() {
        return winners;
    }

    public int getTotalShieldsAwarded() {
        return totalShieldsAwarded;
    }

    public void setTotalShieldsAwarded(int totalShieldsAwarded) {
        this.totalShieldsAwarded = totalShieldsAwarded;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getDiscardedCards() {
        return discardedCards;
    }

    public void addParticipant(String participantId) {
        participants.add(participantId);
    }

    public void addWinner(String winnerId, int shields) {
        winners.add(winnerId);
        totalShieldsAwarded += shields;
    }

    public void setCurrentStage(int stage) {
        if (stage >= 0 && stage < numberOfStages) {
            this.currentStage = stage;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void discardQuestCard(String card) {
        discardedCards.add(card);
    }

    public List<Stage> getStages() { // Getter for stages
        return stages;
    }

    // Getter for cardsUsedBySponsor
    public List<Card> getCardsUsedBySponsor() {
        return cardsUsedBySponsor;
    }

    // Setter for cardsUsedBySponsor
    public void setCardsUsedBySponsor(List<Card> cardsUsedBySponsor) {
        this.cardsUsedBySponsor = cardsUsedBySponsor;
    }



}
