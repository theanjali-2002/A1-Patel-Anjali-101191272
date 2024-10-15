package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class QuestTest {
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
}

