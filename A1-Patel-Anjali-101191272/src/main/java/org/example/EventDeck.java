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
        int[] questCounts = {3, 4, 3, 2};   // Number of each quest card type
        int[] questStages = {2, 3, 4, 5};   // Stages corresponding to each quest type

        for (int i = 0; i < questStages.length; i++) {
            for (int j = 0; j < questCounts[i]; j++) {
                String cardName = "Q" + questStages[i];  // Combine type and stages for card name (e.g., "Q2", "Q3")
                deck.add(new Card(cardName, "Q",questStages[i], "Quest"));  // Add quest card to the deck
            }
        }
    }

    private void addEventCards() {
        // Add 1 Plague card (-2 shields)
        deck.add(new Card("Plague", "E", -2, "Event"));

        // Add 2 Queen's Favor cards (2 adventure cards)
        for (int i = 0; i < 2; i++) {
            deck.add(new Card("Queen's Favor", "E", 2, "Event"));
        }

        // Add 2 Prosperity cards (all players draw 2 adventure cards)
        for (int i = 0; i < 2; i++) {
            deck.add(new Card("Prosperity", "E", 2, "Event"));
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
