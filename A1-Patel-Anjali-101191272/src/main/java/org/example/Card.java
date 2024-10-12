package org.example;

public class Card {
    private String name;   // "F5", "S10", "H10", etc.
    private int value;     // Card value, e.g., 5, 10, 15, 20

    public Card(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;  // Example: "F5", "D5", "S10", "H10", etc.
    }
}
