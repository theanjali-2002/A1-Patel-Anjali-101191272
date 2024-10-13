package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        // Add foe cards to the deck
        for (int i = 0; i < foeValues.length; i++) {
            for (int j = 0; j < foeCounts[i]; j++) {
                String cardName = "F" + foeValues[i]; // e.g., "F5", "F10"
                //System.out.println(cardName);
                deck.add(new Card(cardName, "F" , foeValues[i], "Foe"));
            }
        }

        // Print out the added Foe cards
        //System.out.println("Foe deck: " + deck);
    }

    private void addWeaponCards() {
        // Weapon cards and their counts (D5, H10, S10, B15, L20, E30)
        int[] weaponValues = {5, 10, 10, 15, 20, 30};
        String[] weaponTypes = {"D", "H", "S", "B", "L", "E"};
        int[] weaponCounts = {6, 12, 16, 8, 6, 2};  // How many of each Weapon card

        for (int i = 0; i < weaponValues.length; i++) {
            for (int j = 0; j < weaponCounts[i]; j++) {
                String cardName = weaponTypes[i] + weaponValues[i];
                deck.add(new Card(cardName, weaponTypes[i], weaponValues[i], "Weapon"));
            }
        }
        // Print out the added Foe cards
        //System.out.println("Weapon deck: " + deck);
    }

    // Method to draw cards
    public List<Card> drawCards(int numberOfCards) {
        List<Card> drawnCards = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfCards; i++) {
            if (!deck.isEmpty()) {
                int index = random.nextInt(deck.size());
                drawnCards.add(deck.remove(index)); // Draw a card and remove it from the deck
            }
        }
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
