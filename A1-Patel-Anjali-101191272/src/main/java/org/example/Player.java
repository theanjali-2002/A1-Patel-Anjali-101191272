package org.example;

import java.util.*;

public class Player {
    private String name;
    private List<Card> hand;
    private int shields;
    private List<Card> discardPile;

    public Player(){
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
    }

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.shields = 0;
        this.discardPile = new ArrayList<>();
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

    // Method to discard a card from the player's hand
    public void discardAdventureCard(Card card) {
        if (hand.remove(card)) { // Remove the card from the player's hand
            discardPile.add(card); // Add the card to the discard pile
            System.out.println("Card discarded: " + card.getCardName());
        } else {
            System.out.println("Card not found in hand: " + card.getCardName());
        }
    }

    // Method to get the player's discard pile
    public List<Card> getDiscardPile() {
        return discardPile;
    }

    // Method to trim the player's hand to 12 cards
    public void trimHandTo12Cards() {
        Scanner scanner = new Scanner(System.in);

        while (hand.size() > 12) {
            int numToDiscard = hand.size() - 12;
            System.out.println("You need to discard " + numToDiscard + " card(s).");

            // Display the current hand with indices for selection
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + ": " + hand.get(i).getCardName());
            }

            // Prompt for a valid position to discard
            int position = -1;
            while (position < 1 || position > hand.size()) {
                System.out.print("Enter the position of the card to discard (1-" + hand.size() + "): \n");
                position = scanner.nextInt();
            }

            // Discard the card at the selected position
            Card cardToDiscard = hand.get(position - 1);
            discardAdventureCard(cardToDiscard);

            // Display the updated hand
            System.out.println("Updated hand:");
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + ": " + hand.get(i).getCardName());
            }
        }

        System.out.println("Hand trimmed to 12 cards.");
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

    public int prepareAttackForStage(Stage stage, Player player) {
        //code later
        return 0;
    }

}
