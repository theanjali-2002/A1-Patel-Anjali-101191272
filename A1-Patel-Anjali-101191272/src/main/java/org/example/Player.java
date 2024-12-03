package org.example;

import java.util.*;
import java.util.stream.Collectors;

//import static org.example.ScannerSingleton.getScannerInstance;

public class Player {
    private String name;
    private List<Card> hand;
    private int shields;
    private List<Card> discardPileA;
    private AdventureDeck adventureDeck;
    private Game game;
    private boolean sponsor;
    private Quest quest;

    // Map to store total attack values for each stage
    private Map<String, Integer> stageAttackValues;

    private Map<Integer, List<Card>> receivedCardEvents;
    private int receptionCount = 0; // To keep track of the reception event number

    public Player(){
        this.hand = new ArrayList<>();
        this.discardPileA = new ArrayList<>();
        this.stageAttackValues = new HashMap<>();
        this.receivedCardEvents = new HashMap<>(); // Initialize the map here
        this.receptionCount = 0; // Initialize the count
    }

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.shields = 0;
        this.discardPileA = new ArrayList<>();
        this.game = new Game();
        this.sponsor = false;
        this.quest = new Quest();
        this.stageAttackValues = new HashMap<>();
        this.receivedCardEvents = new HashMap<>(); // Initialize the map here
        this.receptionCount = 0; // Initialize the count
    }

    public String getName() {
        return name;
    }

    // Add cards to the player's hand
    public void receiveCards(List<Card> cards) {
        if (cards != null && !cards.isEmpty()) {
            hand.addAll(cards); // Add the received cards to the player's hand
        }

        // Increment the reception count for the next reception event
        receptionCount++;
        // Store the cards received in this event with the incremented key
        receivedCardEvents.put(receptionCount, new ArrayList<>(cards));
    }

    public List<Card> getHand() {
        List<Card> sortedHand = new ArrayList<>(hand);
        //OutputRedirector.println("Before sorting: " + sortedHand);
        sortHand(sortedHand);
        //OutputRedirector.println("After sorting: " + sortedHand);
        return sortedHand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    // Method to clear all cards in the event deck
    public void clearHand() {
        hand.clear();  // Removes all cards from the hand
    }

    // Method to set a specific deck (for testing purposes)
    public void setClearHand(List<Card> testCards) {
        clearHand();  // Ensure the hand is empty before adding new cards
        hand.addAll(testCards);
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
            OutputRedirector.println("Adventure card discarded: " + card.getCardName());
        } else {
            OutputRedirector.println("Card not found in hand: " + card.getCardName());
        }
    }

    // Method to get the player's discard pile
    public List<Card> getDiscardPileA() {
        return discardPileA;
    }

    // Method to trim the player's hand to 12 cards
    public void trimHandTo12Cards(Player player) {
        hand = player.getHand();

        while (hand.size() > 12) {
            int numToDiscard = hand.size() - 12;
            OutputRedirector.println("*********************************************");
            OutputRedirector.println("You have drawn/received new adventure cards!");
            OutputRedirector.println("You need to discard " + numToDiscard + " card(s) to maintain the deck of 12");

            game.displayPlayerHand(player);

            // Prompt for a valid position to discard
            int position = -1;
            while (position < 1 || position > hand.size()) {
                OutputRedirector.print("Enter the position of the card to discard (1-" + hand.size() + "): \n");
                position = Integer.parseInt(ScannerSingleton.nextLine());
            }

            // Discard the card at the selected position
            Card cardToDiscard = hand.get(position - 1);
            discardACardFromHand(cardToDiscard);
            //OutputRedirector.println("Card trimmed discarding: "+ cardToDiscard.getCardName());
        }

        OutputRedirector.println("Hand trimmed to 12 cards.");
        OutputRedirector.println("*********************************************");
    }

    // Getter for shields
    public int getShields() {
        return shields;
    }

    // Method to reduce shields (ensures player doesn't go below 0 shields)
    public void loseShields(int amount) {
        shields = Math.max(shields - amount, 0);  // Player can't go below 0 shields
        OutputRedirector.println(name + " has lost " + amount + " shields. Remaining shields: " + shields);
    }

    // Method to add shields
    public void gainShields(int amount) {
        shields += amount;
        OutputRedirector.println(name + " has gained " + amount + " shields. Current shields: " + shields);
    }

    public int prepareAttackForStage(Stage stage, Player player) {

        OutputRedirector.println(player.getName() + ", it's your turn to prepare ATTACK for " + stage.getStageId());
        game.displayPlayerHand(player);
        List<Card> hand = player.getHand();

        // Check if the player has any weapon cards
        boolean hasWeaponCard = hand.stream().anyMatch(card -> card.getCategory().equals("Weapon"));
        if (!hasWeaponCard) {
            OutputRedirector.println(player.getName() + ", you have no valid (Weapon) cards to continue playing. You cannot participate in this quest.");
            quest.getParticipants().remove(player);  // Remove player from participants if they can't continue
            return 0;  // Return 0 since the player cannot attack
        }

        List<Card> selectedCards = new ArrayList<>();
        int totalAttackValue = 0;

        // Player selects cards to use for the stage
        while (true) {
            OutputRedirector.println("*********************************************");
            OutputRedirector.println("Enter the card number to select for attack or 'q' to finish:");

            String input = ScannerSingleton.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                if (selectedCards.isEmpty()) {
                    OutputRedirector.println("You must select at least one card to attack.");
                } else {
                    break;  // Finish selection when player is done
                }
            } else {
                try {
                    int cardIndex = Integer.parseInt(input) - 1;
                    if (cardIndex >= 0 && cardIndex < hand.size()) {
                        Card selectedCard = hand.get(cardIndex);
                        OutputRedirector.println("Selected card: " + selectedCard);

                        // Check if the selected card is already in selectedCards (by name)
                        boolean hasDuplicateCardName = selectedCards.stream()
                                .anyMatch(card -> card.getCardName().equals(selectedCard.getCardName()));

                        if (selectedCard.getCategory().equals("Weapon")) {
                            if (!hasDuplicateCardName) {
                                selectedCards.add(selectedCard);
                                totalAttackValue += selectedCard.getValue();
                                OutputRedirector.println("Added " + selectedCard + " to attack. Current attack value: " + totalAttackValue);
                                OutputRedirector.println("*********************************************");
                            } else {
                                OutputRedirector.println("You cannot select the same weapon card more than once.");
                            }
                        } else {
                            OutputRedirector.println("You can only select weapon cards for attack.");
                        }
                    } else {
                        OutputRedirector.println("Invalid card number.");
                    }
                } catch (NumberFormatException e) {
                    OutputRedirector.println("Invalid input. Please enter a number or 'q'.");
                }
            }
        }

        // Remove selected cards from hand and add them to active cards (cards used for attack)
        for (Card card : selectedCards) {
            player.discardACardFromHand(card);
            stage.addWeaponCard(card.getCardName());
        }

        stageAttackValues.put(stage.getStageId(), totalAttackValue);

        OutputRedirector.println(player.getName() + " prepared attack with " + totalAttackValue + " attack value.");
        OutputRedirector.println("*********************************************");
        Game.clearConsole();

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

    // Getter to retrieve attack values for each stage
    public Map<String, Integer> getStageAttackValues() {
        return stageAttackValues;
    }

    public Map<Integer, List<String>> getReceivedCardEvents() {
        Map<Integer, List<String>> receivedCardNames = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : receivedCardEvents.entrySet()) {
            List<String> cardNames = entry.getValue().stream()
                    .map(Card::getCardName)
                    .collect(Collectors.toList());
            receivedCardNames.put(entry.getKey(), cardNames);
        }
        return receivedCardNames;
    }


    public void clearReceivedCardEvents() {
        receivedCardEvents.clear();
        receptionCount = 0; // Reset the reception count as well
    }


}
