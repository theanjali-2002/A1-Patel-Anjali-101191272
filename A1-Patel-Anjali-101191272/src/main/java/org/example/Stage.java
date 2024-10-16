package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage {
    private String stageId;
    private int stageValue;
    private List<String> weaponCards;
    private List<String> participants;
    private Map<String, Integer> attacks;
    private List<String> successfulParticipants;
    private String status;

    public Stage() {
        this.weaponCards = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.attacks = new HashMap<>();
        this.successfulParticipants = new ArrayList<>();
    }

    public Stage(String stageId, int stageValue, List<Card> cardsInStage) {
        this.stageId = stageId;
        this.stageValue = stageValue;
        this.weaponCards = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.attacks = new HashMap<>();
        this.successfulParticipants = new ArrayList<>();
        this.status = "pending"; // Default status
    }

    // Getters and Setters
    public String getStageId() {
        return stageId;
    }

    public int getStageValue() {
        return stageValue;
    }

    public List<String> getWeaponCards() {
        return weaponCards;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public Map<String, Integer> getAttacks() {
        return attacks;
    }

    public List<String> getSuccessfulParticipants() {
        return successfulParticipants;
    }

    public String getStatus() {
        return status;
    }

    public void addWeaponCard(String weaponCard) {
        weaponCards.add(weaponCard);
    }

    public void addParticipant(String participantId) {
        participants.add(participantId);
    }

    public void recordAttack(String participantId, int attackValue) {
        if (attacks == null) {
            attacks = new HashMap<>();
        }
        attacks.put(participantId, attackValue);
        if (attackValue >= stageValue) {
            successfulParticipants.add(participantId);
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

