package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private int shields;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.shields = 0;
    }

    public String getName() {
       return name;
    }

    // Add cards to the player's hand
    public void receiveCards(List<Card> cards) {
        if (cards != null && !cards.isEmpty()) {
            hand.addAll(cards); // Add the received cards to the player's hand
        }
    }

    public List<Card> getHand() {
        List<Card> sortedHand = new ArrayList<>(hand);
        //System.out.println("Before sorting: " + sortedHand);
        sortHand(sortedHand);
        //System.out.println("After sorting: " + sortedHand);
        return sortedHand;
    }


    public void sortHand(List<Card> handToSort) {
        // Separate foes and weapons into different lists
        List<Card> foes = new ArrayList<>();
        List<Card> weapons = new ArrayList<>();

        for (Card card : handToSort) {
            if (card.getCategory().equals("Foe")) {
                foes.add(card);
            } else if (card.getCategory().equals("Weapon")) {
                weapons.add(card);
            }
        }

        // Sort foes by value
        foes.sort(Comparator.comparingInt(Card::getValue));

        // Sort weapons: prioritize swords before horses, then by value
        weapons.sort(Comparator.comparing((Card card) -> {
            if (card.getType().equals("S")) {
                return 0; // Swords come first
            } else if (card.getType().equals("H")) {
                return 1; // Horses come last
            } else {
                return 2; // Other weapon types
            }
        }).thenComparingInt(Card::getValue)); // Sort by value

        // Clear the original hand and add sorted cards back
        handToSort.clear();
        handToSort.addAll(foes);
        handToSort.addAll(weapons);
    }

    // Getter for shields
    public int getShields() {
        return shields;
    }

    // Method to reduce shields (ensures player doesn't go below 0 shields)
    public void loseShields(int amount) {
        shields = Math.max(shields - amount, 0);  // Player can't go below 0 shields
        System.out.println(name + " has lost " + amount + " shields. Remaining shields: " + shields);
    }

    // Method to add shields
    public void gainShields(int amount) {
        shields += amount;
        System.out.println(name + " has gained " + amount + " shields. Current shields: " + shields);
    }

}
