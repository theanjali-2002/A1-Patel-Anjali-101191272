package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage {
    private String stageId;
    private int stageValue;
    private String foeCard;
    private List<String> weaponCards;
    private List<String> participants;
    private Map<String, Integer> attacks;
    private List<String> successfulParticipants;
    private String status;

    public Stage(String stageId, int stageValue, String foeCard) {
        this.stageId = stageId;
        this.stageValue = stageValue;
        this.foeCard = foeCard;
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

    public String getFoeCard() {
        return foeCard;
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
        attacks.put(participantId, attackValue);
        if (attackValue >= stageValue) {
            successfulParticipants.add(participantId);
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

