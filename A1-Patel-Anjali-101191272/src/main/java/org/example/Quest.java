package org.example;

import java.util.ArrayList;
import java.util.List;

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

    }

    public void addWinner(String winnerId, int shields) {

    }

    public void setCurrentStage(int stage) {

    }

    public void setStatus(String status) {

    }

    public void discardQuestCard(String card) {

    }


}
