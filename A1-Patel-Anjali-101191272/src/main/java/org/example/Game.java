//The Game class file manages the game logic,
//including initializing the game environment,
//setting up decks, and controlling the flow of gameplay.
package org.example;

import java.util.*;

public class Game {
    private AdventureDeck adventureDeck;
    private EventDeck eventDeck;
    private List<Player> players;
    private int currentPlayerIndex; // To track the current player

    // Constructor
    public Game() {
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
    }

    // Methods to initialize the game environment
    public void initializeGameEnvironment() {
        adventureDeck.setupDeck(); // RESP-02 Set up the adventure deck
        eventDeck.setupDeck(); // RESP-03 Set up the event deck
    }

    public void initializePlayers() {
        players.add(new Player("P1"));
        players.add(new Player("P2"));
        players.add(new Player("P3"));
        players.add(new Player("P4"));
        //System.out.println("PLayers are initialized now!");
    }

    public void distributeAdventureCards() {
        final int CARDS_PER_PLAYER = 12;

        // Distributing cards to each player
        for (Player player : players) {
            List<Card> cardsToDistribute = adventureDeck.drawCards(CARDS_PER_PLAYER); // Draw cards from the deck
            player.receiveCards(cardsToDistribute); // Distribute the drawn cards to the player
        }
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void displayCurrentPlayerHand() {
        Player currentPlayer = getCurrentPlayer();

        List<Card> hand = currentPlayer.getHand();

        // Check if the hand is empty
        if (hand.isEmpty()) {
            System.out.println("Current player's hand is empty - something went wrong");
            return;
        }

        // Separate foes and weapons into different lists
        List<Card> foes = new ArrayList<>();
        List<Card> weapons = new ArrayList<>();

        for (Card card : hand) {
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
            // Prioritize Sword before Horse
            if (card.getType().equals("S")) {
                return 0; // Swords come first
            } else if (card.getType().equals("H")) {
                return 1; // Horses come last
            } else {
                return 2; // Other weapon types
            }
        }).thenComparingInt(Card::getValue)); // Sort by value

        // Display foes
        System.out.println("Hot Seat: " + currentPlayer.getName());
        System.out.println("Current Player " + currentPlayer.getName() + "'s Hand: ");
        int placeValue = 1; // Starting place value from 1

        // Display sorted foes
        for (Card card : foes) {
            System.out.println("[" + placeValue + "] " + card.toString());
            placeValue++;
        }

        // Display sorted weapons
        for (Card card : weapons) {
            System.out.println("[" + placeValue + "] " + card.toString());
            placeValue++;
        }
    }

    public Player getCurrentPlayer() {
        // Check if the players list is not empty before accessing it
        if (players.isEmpty()) {
            throw new IllegalStateException("No players available in the game.");
        }
        return players.get(currentPlayerIndex);
    }

    // Getters for testing
    public AdventureDeck getAdventureDeck() {
        return adventureDeck;
    }

    public EventDeck getEventDeck() {
        return eventDeck;
    }

    public List<Player> getPlayers() {return players;}


    // Function to prompt player to draw a card
    public String drawEventCard() {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        if (eventDeck.countEventCards() == 0) {
            System.out.println("No event cards left in the deck!");
            return null;
        }

        System.out.println("Press 'e' to draw an event card OR Press 'q' to Quit Game...");

        // Keep asking until the player presses 'e'
        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("e")) {
                // Player pressed 'e', draw a random event card
                List<Card> deck = eventDeck.getDeck();  // Get the deck list
                Card drawnCard = null;

                // Find and remove a random event card from the deck
                while (drawnCard == null) {
                    int index = random.nextInt(deck.size());
                    Card card = deck.get(index);
                    if ("Event".equals(card.getCategory()) ||  "Quest".equals(card.getCategory())) {
                        drawnCard = card;
                        deck.remove(index);  // Remove the drawn card from the deck
                    }
                }

                // Display the drawn card
                System.out.println("Drawn Card: " + drawnCard.getCardName());
                return drawnCard.getCategory();

            } else if (input.equalsIgnoreCase("q")) {
                System.out.println("Game Exiting...");
                return null;
            }
            else {
                // Invalid input, prompt the player again
                System.out.println("Invalid input! Please press 'e' to draw a card OR Press 'q' to Quit Game...");
            }
        }
    }



    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface(); // Initialize user interface
        userInterface.displayGameStartMessage(true); // Display the game start message

        Game game = new Game(); // Create a new instance of the Game class
        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();
        game.displayCurrentPlayerHand();
        //userInterface.displayPlayerTurn(game.getCurrentPlayer().getName());
        game.drawEventCard();

        // Other game logic code later
    }

}