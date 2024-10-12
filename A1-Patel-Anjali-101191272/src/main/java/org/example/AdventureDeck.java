package org.example;

import java.util.ArrayList;
import java.util.List;

public class AdventureDeck {
    private List<Card> deck; // full set of cards in the deck

    // constructor initializes the deck
    public AdventureDeck() {
        deck = new ArrayList<>();
    }

    public void setupDeck() {
        addFoeCards();
        addWeaponCards();
    }

    private void addFoeCards() {
        // Foe cards and their counts (values: 5, 10, 15, ..., 70)
        int[] foeValues = {5, 10, 15, 20, 25, 30, 35, 40, 50, 70};
        int[] foeCounts = {8, 7, 8, 7, 7, 4, 4, 2, 2, 1};  // How many of each Foe card

        for (int i = 0; i < foeValues.length; i++) {
            for (int j = 0; j < foeCounts[i]; j++) {
                deck.add(new Card("F", foeValues[i], "Foe"));
            }
        }
    }

    private void addWeaponCards() {
        // Weapon cards and their counts (D5, H10, S10, B15, L20, E30)
        int[] weaponValues = {5, 10, 10, 15, 20, 30};
        String[] weaponTypes = {"D", "H", "S", "B", "L", "E"};
        int[] weaponCounts = {6, 12, 16, 8, 6, 2};  // How many of each Weapon card

        for (int i = 0; i < weaponValues.length; i++) {
            for (int j = 0; j < weaponCounts[i]; j++) {
                deck.add(new Card(weaponTypes[i], weaponValues[i], "Weapon"));
            }
        }
    }

    // Method to draw cards
    public List<Card> drawCards(int numberOfCards) {
        List<Card> drawnCards = new ArrayList<>();
        //needs code later
        return drawnCards;
    }

    public int getTotalCards() {
        return deck.size();
    }

    public int countFoeCards() {
        int count = 0;
        for (Card card : deck) {
            if ("Foe".equals(card.getCategory())) {
                count++;
            }
        }
        return count;
    }

    public int countWeaponCards() {
        int count = 0;
        for (Card card : deck) {
            if ("Weapon".equals(card.getCategory())) {  // Anything not a Foe is a Weapon
                count++;
            }
        }
        return count;
    }

}
