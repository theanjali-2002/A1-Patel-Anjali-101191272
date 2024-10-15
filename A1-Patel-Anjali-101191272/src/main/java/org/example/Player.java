package org.example;

import java.util.ArrayList;
import java.util.Collection;
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
    public int getShields() {
        return shields;
    }

    public void loseShields(int amount) {
        //code later
    }

    // Method to add shields
    public void gainShields(int amount) {
        //code later
    }

}
