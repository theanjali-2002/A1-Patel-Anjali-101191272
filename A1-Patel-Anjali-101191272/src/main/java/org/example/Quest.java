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

    public Quest() {
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
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
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

    public void setupQuest(Game game) {

    }


    private Card getCard(int position, Game game) {

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
