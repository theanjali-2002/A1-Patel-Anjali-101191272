package org.example;

public class Card {
    private String type;   // Just "F", "D", "S", etc. (for card type, Foe, or Weapon)
    private int value;     // Card value, e.g., 5, 10, 15, 20
    private String category;  // New field to differentiate between Quest and Event cards

    public Card(String type, int value, String category) {
        this.type = type;
        this.value = value;
        this.category = category;
    }

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {return category;}

    @Override
    public String toString() {return type;}  // Example: "F5", "D5", "S10", "H10", etc.

}
