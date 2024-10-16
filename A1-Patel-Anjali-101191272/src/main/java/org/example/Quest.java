package org.example;

import java.util.ArrayList;
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

    public Quest() {
        this.participants = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.discardedCards = new ArrayList<>();
        this.cardDeck = new ArrayList<>();
        this.stages = new ArrayList<>();
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
                            validStageSetup = true; // Stage is valid
                        }
                    } else {
                        // Validate the card position
                        try {
                            int cardPosition = Integer.parseInt(input); // Convert to int
                            Card card = getCard(cardPosition - 1, game); // Assuming getCard takes position from hand

                            if (card != null && isValidCard(card, cardsInStage)) {
                                cardsInStage.add(card);
                                System.out.println("Added card: " + card);

                                // Print the cards currently in the stage
                                System.out.println("Current cards in Stage " + (i + 1) + ": " + cardsInStage);

                                stageValue = calculateStageValue(cardsInStage); // Update stage value

                                // Discard the card from player's hand
                                game.getCurrentPlayer().discardAdventureCard(card); // Assuming discardCard method exists

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
            List<Card> drawnCards = adventureDeck.drawCards(1);
            player.receiveCards(drawnCards); // Each participant draws one adventure card
            player.trimHandTo12Cards(); // Ensure they don’t exceed card limit
        }
    }

    public void prepareForStage(int stageIndex, Game game) {
        Stage stage = stages.get(stageIndex);
        for (String participant : participants) {
            System.out.println("Participant in method: " + participant);
            Player player = game.getPlayerByName(participant);
            if (player == null) {
                System.out.println("Error: Player '" + participant + "' could not be found in the game.");
                continue;
            }
            // Prepare the player's attack for the stage
            try {
                int attackValue = player.prepareAttackForStage(stage, player);
                stage.recordAttack(participant, attackValue);
            } catch (Exception e) {
                System.out.println("Error while preparing attack for " + participant + ": " + e.getMessage());
            }
        }
    }



    public void resolveStage(int stageIndex, Game game) {
        //code later
    }

    public void resolveWinners(Game game) {
        //code later
    }


    public void endQuestWithoutWinners() {
        //code later
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


}
