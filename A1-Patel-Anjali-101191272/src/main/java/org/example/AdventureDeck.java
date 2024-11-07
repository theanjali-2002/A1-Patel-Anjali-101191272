package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class AdventureDeck {
    private List<Card> deck; // full set of cards in the deck
    private List<Card> discardPile;
    private Player player;
    private Supplier<Integer> randomSupplier;

    // constructor initializes the deck
    public AdventureDeck(Supplier<Integer> randomSupplier) {
        this.randomSupplier = randomSupplier != null ? randomSupplier : () -> new Random().nextInt();
        deck = new ArrayList<>();
    }

    public AdventureDeck() {
        this(null);
        deck = new ArrayList<>();
    }

    public void setupDeck() {
        addFoeCards();
        addWeaponCards();
    }

    public List<Card> getDeck() {
        return deck;
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
    public List<Card> drawACards(int numberOfCards) {
        List<Card> drawnCards = new ArrayList<>();

        // Check if the number of cards requested exceeds the available cards
        if (numberOfCards > deck.size()) {
            System.out.println("Requested number of cards (" + numberOfCards + ") exceeds the deck size (" + deck.size() + "). Drawing only available cards (" + deck.size() + ").");
            numberOfCards = deck.size();  // Adjust the number of cards to draw
        }

        for (int i = 0; i < numberOfCards; i++) {
            if (deck.isEmpty()) {
                refillDeckFromDiscard();
            }
            if (!deck.isEmpty()) {
                int index = Math.floorMod(randomSupplier.get(), deck.size());
                drawnCards.add(deck.remove(index));
            }
        }
        return drawnCards;
    }

    // Method to add a list of cards to the deck - for testing purposes
    public void addCards(List<Card> newCards) {
        if (newCards != null) {
            this.deck.addAll(newCards); // Add all new cards to the existing list
        }
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

    // Method to refill the deck from the discard pile
    private void refillDeckFromDiscard() {
        if (!player.getDiscardPileA().isEmpty()) {
            // Shuffle the discard pile
            Collections.shuffle(player.getDiscardPileA());
            // Add all cards from the discard pile back to the deck
            deck.addAll(player.getDiscardPileA());
            // Clear the discard pile after refilling the deck
            player.getDiscardPileA().clear();
            System.out.println("The Adventure deck has been refilled from the discard pile.");
        } else {
            System.out.println("The Adventure discard pile is also empty. Cannot refill the deck.");
        }
    }

    // Method to clear all cards in the event deck
    public void clearDeck() {
        deck.clear();  // Removes all cards from the deck
    }

    // Method to set a specific deck (for testing purposes)
    public void setDeck(List<Card> testCards) {
        clearDeck();  // Ensure the deck is empty before adding new cards
        deck.addAll(testCards);
    }

}
