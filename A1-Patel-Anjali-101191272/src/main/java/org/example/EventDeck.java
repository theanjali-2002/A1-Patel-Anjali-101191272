package org.example;

import java.util.ArrayList;
import java.util.List;

public class EventDeck {
    private List<Card> deck;

    public EventDeck() {
        deck = new ArrayList<>();
    }

    public void setupDeck() {
        addQuestCards();
        addEventCards();
    }

    private void addQuestCards() {
        // Quest cards: 3 Q2, 4 Q3, 3 Q4, 2 Q5
        String[] questTypes = {"Q2", "Q3", "Q4", "Q5"};
        int[] questCounts = {3, 4, 3, 2};  // number of each type of quest card
        int[] questStages = {2, 3, 4, 5};  // stages corresponding to each quest type

        for (int i = 0; i < questTypes.length; i++) {
            for (int j = 0; j < questCounts[i]; j++) {
                deck.add(new Card(questTypes[i], questStages[i], "Quest"));
            }
        }
    }

    private void addEventCards() {
        // Add 1 Plague card (-2 shields)
        deck.add(new Card("Plague", -2, "Event"));

        // Add 2 Queen's favor cards (2 adventure cards)
        for (int i = 0; i < 2; i++) {
            deck.add(new Card("Queen's favor", 2, "Event"));
        }

        // Add 2 Prosperity cards (all players draw 2 adventure cards)
        for (int i = 0; i < 2; i++) {
            deck.add(new Card("Prosperity", 2, "Event"));
        }
    }

    public int getTotalCards() {
        return deck.size();
    }

    public int countQuestCards() {
        int count = 0;
        for (Card card : deck) {
            if ("Quest".equals(card.getCategory())) {
                count++;
            }
        }
        return count;
    }

    public int countEventCards() {
        int count = 0;
        for (Card card : deck) {
            if ("Event".equals(card.getCategory())) {
                count++;
            }
        }
        return count;
    }

}
