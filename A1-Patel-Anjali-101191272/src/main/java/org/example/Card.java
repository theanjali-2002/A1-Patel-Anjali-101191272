package org.example;

import java.util.Objects;

public class Card {
    private String cardName;
    private String type;   // Just "F", "D", "S", etc. (for card type, Foe, or Weapon)
    private int value;     // Card value, e.g., 5, 10, 15, 20
    private String category;  // New field to differentiate between Quest and Event cards

    public Card(String cardName, String type, int value, String category) {
        this.cardName = cardName;
        this.type = type;
        this.value = value;
        this.category = category;
    }

    public String getCardName() {return cardName;}

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {return category;}

    @Override
    public String toString() {return cardName;}  // Example: "F5", "D5", "S10", "H10", etc.

}
