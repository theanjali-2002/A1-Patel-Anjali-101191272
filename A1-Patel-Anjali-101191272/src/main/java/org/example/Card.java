package org.example;

public class Card {
    private String type;   // Just "F", "D", "S", etc. (for card type, Foe, or Weapon)
    private int value;     // Card value, e.g., 5, 10, 15, 20

    public Card(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;  // Example: "F5", "D5", "S10", "H10", etc.
    }
}
