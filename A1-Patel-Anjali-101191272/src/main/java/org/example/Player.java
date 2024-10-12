package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public String getName() {
       return name;
    }

    // Add cards to the player's hand
    public void receiveCards(List<Card> cards) {
        //code later
    }

    public List<Card> getHand() {
        return hand;
    }
}
