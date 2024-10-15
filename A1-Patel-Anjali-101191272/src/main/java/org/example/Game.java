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
    private Scanner scanner;

    // Constructor
    public Game() {
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        this.scanner = new Scanner(System.in);
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

    public AdventureDeck getAdventureDeck() {
        return adventureDeck;
    }

    public EventDeck getEventDeck() {
        return eventDeck;
    }

    public List<Player> getPlayers() {return players;}

    // Function to prompt player to draw a card
    public Card drawEventCard() {
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
                return drawnCard;

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

    public void handleECardEffects(Card drawnCard, Player currentPlayer) {
        switch (drawnCard.getCardName()) {
            case "Plague":
                System.out.println("Plague card is drawn and current player loses 2 shields.");
                currentPlayer.loseShields(2); // Implement loseShields to set shields to 0 if less than 2
                break;

            case "Queen's favor":
                System.out.println("Queen's favor card is drawn and current player will draw 2 adventure cards.");
                currentPlayer.receiveCards(adventureDeck.drawCards(2));// Implement this method to let the player draw adventure cards
                break;

            case "Prosperity":
                System.out.println("All players draw 2 adventure cards due to the Prosperity event.");
                for (Player player : players) {
                    System.out.println(player.getName() + " draws 2 adventure cards.");
                    player.receiveCards(adventureDeck.drawCards(2));
                }
                break;

            default:
                System.out.println("No specific action for this event card.");
        }

        // Discard the drawn event card
        eventDeck.discardEventCard(drawnCard);

        // End the current player's turn after drawing an event card
        nextPlayer();
    }

    public boolean promptToSponsor(Player currentPlayer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(currentPlayer.getName() + ", a new quest has been drawn.");
        System.out.print("Do you want to sponsor this quest? (y/n): ");

        String response = scanner.nextLine().trim().toLowerCase();

        // Validate the input and handle the response
        while (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n")) {
            System.out.print("Invalid input. Please enter 'y' or 'n': ");
            response = scanner.nextLine().trim().toLowerCase();
        }

        if (response.equalsIgnoreCase("y")) {
            System.out.println(currentPlayer.getName() + " has chosen to sponsor the quest.");
            return true; // Player has chosen to sponsor the quest
        } else {
            System.out.println(currentPlayer.getName() + " has declined to sponsor the quest.");
            return false; // Player has declined the sponsorship
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
        Card drewCard = game.drawEventCard();
        if (drewCard.getCategory() == "Event"){
            game.handleECardEffects(drewCard, game.getCurrentPlayer());
        } else {
            System.out.println("not event card");
            game.promptToSponsor(game.getCurrentPlayer());
        }


        // Other game logic code later
    }

}