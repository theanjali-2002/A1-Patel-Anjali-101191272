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

        return hand;
    }


    public void sortHand(List<Card> handToSort) {
        //code later
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
