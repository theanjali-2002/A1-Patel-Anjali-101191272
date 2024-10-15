package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StageTest {
    @Test
    @DisplayName("R-TEST-17: Implementing Stage logics")
    public void RESP_17_test_01() {

        List<Card> hand = new ArrayList<>();

        // Add Foes with different values
        hand.add(new Card("F5", "F", 5, "Foe"));   // Foe with value 5
        hand.add(new Card("F3", "F", 3, "Foe"));   // Foe with value 3
        // Creating a Stage instance
        Stage stage = new Stage("S1", 5, hand);

        // Manually updating fields after construction for testing
        stage.addWeaponCard("Sword");
        stage.addWeaponCard("Bow");
        stage.addParticipant("P1");
        stage.addParticipant("P2");
        stage.recordAttack("P1", 6); // Participant P1 makes a successful attack
        stage.recordAttack("P2", 3); // Participant P2 fails to attack
        stage.setStatus("completed"); // Setting status directly

        // Testing Stage properties
        assertEquals("S1", stage.getStageId(), "Stage ID should be S1");
        assertEquals(5, stage.getStageValue(), "Stage Value should be 5");
        assertEquals(Arrays.asList("Sword", "Bow"), stage.getWeaponCards(), "Weapon cards should match");
        assertEquals(Arrays.asList("P1", "P2"), stage.getParticipants(), "Participants should match");
        assertEquals(1, stage.getSuccessfulParticipants().size(), "Should have 1 successful participant");
        assertTrue(stage.getSuccessfulParticipants().contains("P1"), "Successful participant should be P1");
        assertEquals("completed", stage.getStatus(), "Stage status should be completed");
    }
}
