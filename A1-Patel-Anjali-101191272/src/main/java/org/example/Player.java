package org.example;

import java.util.*;

public class Player {
    private String name;
    private List<Card> hand;
    private int shields;
    private List<Card> discardPileA;
    private AdventureDeck adventureDeck;
    private Game game;
    private boolean sponsor;
    private Quest quest;

    public Player(){
        this.hand = new ArrayList<>();
        this.discardPileA = new ArrayList<>();
    }

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.shields = 0;
        this.discardPileA = new ArrayList<>();
        this.game = new Game();
        this.sponsor = false;
        this.quest = new Quest();
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

    public void setHand(List<Card> hand) {
        this.hand = hand;
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
    public void discardACardFromHand(Card card) {
        if (hand.remove(card)) {
            discardPileA.add(card); // Add the card to the discard pile
            System.out.println("Adventure card discarded: " + card.getCardName());
        } else {
            System.out.println("Card not found in hand: " + card.getCardName());
        }
    }

    // Method to get the player's discard pile
    public List<Card> getDiscardPileA() {
        return discardPileA;
    }

    // Method to trim the player's hand to 12 cards
    public void trimHandTo12Cards(Player player) {
        Scanner scanner = new Scanner(System.in);

        while (hand.size() > 12) {
            int numToDiscard = hand.size() - 12;
            System.out.println("*********************************************");
            System.out.println("You need to discard " + numToDiscard + " card(s).");

            game.displayPlayerHand(player);

            // Prompt for a valid position to discard
            int position = -1;
            while (position < 1 || position > hand.size()) {
                System.out.print("Enter the position of the card to discard (1-" + hand.size() + "): \n");
                position = scanner.nextInt();
            }

            // Discard the card at the selected position
            Card cardToDiscard = hand.get(position - 1);
            discardACardFromHand(cardToDiscard);

            // Display the updated hand
            System.out.println("*********************************************");
            System.out.println("Updated hand:");
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + ": " + hand.get(i).getCardName());
            }
            System.out.println("*********************************************");
        }

        System.out.println("Hand trimmed to 12 cards.");
        System.out.println("*********************************************");
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
        Scanner scanner = new Scanner(System.in);

        System.out.println(player.getName() + ", it's your turn to prepare ATTACK for " + stage.getStageId());
        game.displayPlayerHand(player);
        List<Card> hand = player.getHand();

        // Check if the player has any weapon cards
        boolean hasWeaponCard = hand.stream().anyMatch(card -> card.getCategory().equals("Weapon"));
        if (!hasWeaponCard) {
            System.out.println(player.getName() + ", you have no valid (Weapon) cards to continue playing. You cannot participate in this quest.");
            quest.getParticipants().remove(player);  // Remove player from participants if they can't continue
            return 0;  // Return 0 since the player cannot attack
        }

        List<Card> selectedCards = new ArrayList<>();
        int totalAttackValue = 0;

        // Player selects cards to use for the stage
        while (true) {
            System.out.println("*********************************************");
            System.out.println("Enter the card number to select for attack or 'q' to finish:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("q")) {
                if (selectedCards.isEmpty()) {
                    System.out.println("You must select at least one card to attack.");
                } else {
                    break;  // Finish selection when player is done
                }
            } else {
                try {
                    int cardIndex = Integer.parseInt(input) - 1;
                    if (cardIndex >= 0 && cardIndex < hand.size()) {
                        Card selectedCard = hand.get(cardIndex);
                        System.out.println("Selected card: " + selectedCard);

                        // Check if the selected card is already in selectedCards (by name)
                        boolean hasDuplicateCardName = selectedCards.stream()
                                .anyMatch(card -> card.getCardName().equals(selectedCard.getCardName()));

                        if (selectedCard.getCategory().equals("Weapon")) {
                            if (!hasDuplicateCardName) {
                                selectedCards.add(selectedCard);
                                totalAttackValue += selectedCard.getValue();
                                System.out.println("Added " + selectedCard + " to attack. Current attack value: " + totalAttackValue);
                                System.out.println("*********************************************");
                            } else {
                                System.out.println("You cannot select the same weapon card more than once.");
                            }
                        } else {
                            System.out.println("You can only select weapon cards for attack.");
                        }
                    } else {
                        System.out.println("Invalid card number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number or 'q'.");
                }
            }
        }

        // Remove selected cards from hand and add them to active cards (cards used for attack)
        for (Card card : selectedCards) {
            player.discardACardFromHand(card);
            stage.addWeaponCard(card.getCardName());
        }

        System.out.println(player.getName() + " prepared attack with " + totalAttackValue + " attack value.");
        System.out.println("*********************************************");

        return totalAttackValue;  // Return the total attack value to be recorded in the stage
    }

    // Getter method for sponsor
    public boolean isSponsor() {
        return sponsor;
    }

    // Setter method for sponsor
    public void setSponsor(boolean sponsor) {
        this.sponsor = sponsor;
    }

}
