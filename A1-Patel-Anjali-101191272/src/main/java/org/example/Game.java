//The Game class file manages the game logic,
//including initializing the game environment,
//setting up decks, and controlling the flow of gameplay.
package org.example;
import static org.example.ScannerSingleton.getScannerInstance;
import java.util.*;
import java.util.function.Supplier;

public class Game {
    private AdventureDeck adventureDeck;
    private EventDeck eventDeck;
    private List<Player> players;
    private int currentPlayerIndex;
    private int hotSeatIndex;
    private Supplier<Integer> randomSupplier;

    public Game(Supplier<Integer> randomSupplier) {
        this.randomSupplier = randomSupplier != null ? randomSupplier : () -> new Random().nextInt();
        adventureDeck = new AdventureDeck(() -> 0);
        eventDeck = new EventDeck();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        hotSeatIndex = 0;
    }

    // Constructor
    public Game() {
        this(null);
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        hotSeatIndex = 0;
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
    }

    public void distributeAdventureCards() {
        final int CARDS_PER_PLAYER = 12;

        // Distributing cards to each player
        for (Player player : players) {
            List<Card> cardsToDistribute = adventureDeck.drawACards(CARDS_PER_PLAYER); // Draw cards from the deck
            player.receiveCards(cardsToDistribute); // Distribute the drawn cards to the player
        }
    }

    public void nextPlayer() {
        // End the current player's turn
        Player currentPlayer = players.get(currentPlayerIndex);
        System.out.println(currentPlayer.getName() + ", your turn has ended. Please enter 'r' to return from the Hot Seat.");

        // Check for user input (e.g., 'r') to return, and loop until the input is valid
        while (true) {
            String input = getScannerInstance().nextLine().trim();

            if ("r".equalsIgnoreCase(input)) {
                System.out.println("Leaving the Hot Seat...");
                break; // Exit the loop when input is valid
            } else {
                System.out.println("Invalid input. Please enter 'r' to return from the Hot Seat.");
            }
        }

        clearConsole();
        // Move to the next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Player nextPlayer = players.get(currentPlayerIndex);
        setCurrentPlayer(currentPlayerIndex);
        System.out.println("*********************************************");
        System.out.println("Hot Seat (current player): " + nextPlayer.getName());
        System.out.println("*********************************************");
    }

    // Method to advance to the next hot seat player
    public void nextHotSeatPlayer() {
        // End the current player's turn
        Player currentPlayer = players.get(hotSeatIndex);
        System.out.println(currentPlayer.getName() + ", your turn has ended. Please enter 'r' to return from the Hot Seat.");

        // Check for user input (e.g., 'r') to return, and loop until the input is valid
        while (true) {
            String input = getScannerInstance().nextLine().trim();

            if ("r".equalsIgnoreCase(input)) {
                System.out.println("Leaving the Hot Seat...");
                break; // Exit the loop when input is valid
            } else {
                System.out.println("Invalid input. Please enter 'r' to return from the Hot Seat.");
            }
        }

        clearConsole();
        // Move to the next Hot Seat player
        hotSeatIndex = (hotSeatIndex + 1) % players.size();
        Player nextPlayer = players.get(hotSeatIndex);
        setCurrentPlayer(hotSeatIndex);
        System.out.println("*********************************************");
        System.out.println("Hot Seat (current player): " + nextPlayer.getName());
        System.out.println("*********************************************");

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

        System.out.println("Current Player " + currentPlayer.getName() + "'s Hand: ");
        int placeValue = 1;

        for (Card card : hand) {
            System.out.println("[" + placeValue + "] " + card.toString());
            placeValue++;
        }
    }

    public void displayPlayerHand(Player player) {
        // Check if the player's hand is empty
        if (player.getHand().isEmpty()) {
            System.out.println(player.getName() + "'s hand is empty - something went wrong");
            return;
        }

        List<Card> hand = player.getHand();
        System.out.println(player.getName() + "'s Hand: ");
        int placeValue = 1;

        for (Card card : hand) {
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

    public void setCurrentPlayer(int playerIndex) {
        // Check if the players list is not empty
        if (players.isEmpty()) {
            throw new IllegalStateException("No players available in the game.");
        }

        // Check if the playerIndex is within the valid range
        if (playerIndex < 0 || playerIndex >= players.size()) {
            throw new IndexOutOfBoundsException("Player index is out of bounds: " + playerIndex);
        }

        // Set the current player index
        currentPlayerIndex = playerIndex;
    }

    public AdventureDeck getAdventureDeck() {
        return adventureDeck;
    }

    public EventDeck getEventDeck() {
        return eventDeck;
    }

    public List<Player> getPlayers() {return players;}

    public Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    // Function to prompt player to draw a card
    public Card drawEventCard() {

        if (eventDeck.countEventCards() == 0) {
            eventDeck.refillDeckFromDiscard();
        }

        System.out.println("Press 'e' to draw an event card OR Press 'q' to Quit Game...");

        // Keep asking until the player presses 'e'
        while (true) {
            String input = getScannerInstance().nextLine().trim();

            if (input.equalsIgnoreCase("e")) {
                // Player pressed 'e', draw a random event card
                List<Card> deck = eventDeck.getDeck();  // Get the deck list
                System.out.println("debug event deck: "+ deck);
                Card drawnCard = null;

                // Find and remove a random event card from the deck
                while (drawnCard == null) {
                    //int index = random.nextInt(deck.size());
                    //Card card = deck.get(index);
                    int index = Math.floorMod(randomSupplier.get(), deck.size());  // Ensures index is within bounds
                    Card card = deck.get(index);
                    if ("Event".equals(card.getCategory()) ||  "Quest".equals(card.getCategory())) {
                        drawnCard = card;
                        deck.remove(card);
                        System.out.println("debug discarding drawn card: " + drawnCard.getCardName());
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
                System.out.println("*********************************************");
                System.out.println("Card Drawn: Plague card.");
                System.out.println("Current player " + currentPlayer.getName() + " loses 2 shields.");
                System.out.println("*********************************************");
                currentPlayer.loseShields(2);
                break;

            case "Queen's Favor":
                System.out.println("*********************************************");
                System.out.println("Card Drawn: Queen's favor card.");
                System.out.println("Current player will draw 2 adventure cards.");
                System.out.println("*********************************************");
                currentPlayer.receiveCards(adventureDeck.drawACards(2));
                currentPlayer.trimHandTo12Cards(currentPlayer);
                break;

            case "Prosperity":
                System.out.println("*********************************************");
                System.out.println("Card Drawn: Prosperity Card.");
                System.out.println("All players draw 2 adventure cards.");
                System.out.println("*********************************************");
                System.out.println("Loading ... ...");
                for (Player player : players) {
                    System.out.println(player.getName() + ", you have drawn 2 adventure cards.");
                    player.receiveCards(adventureDeck.drawACards(2));
                    player.trimHandTo12Cards(player);
                    System.out.println("Next player ... Loading ... ...");
                    clearConsole();
                }
                break;

            default:
                System.out.println("\n");
        }

        // Discard the drawn event card
        eventDeck.discardEventCard(drawnCard);

        // End the current player's turn after drawing an event card
        // Next Hot Seat Player
        nextHotSeatPlayer();
    }

    public boolean promptToSponsor(Player currentPlayer) {
        System.out.println(currentPlayer.getName() + ", a new quest has been drawn.");
        System.out.print("Do you want to sponsor this quest? (y/n): ");

        String response = getScannerInstance().nextLine().trim().toLowerCase();

        // Validate the input and handle the response
        while (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n")) {
            System.out.print("Invalid input. Please enter 'y' or 'n': ");
            response = getScannerInstance().nextLine().trim().toLowerCase();
        }

        if (response.equalsIgnoreCase("y")) {
            int foeCardCount = (int) currentPlayer.getHand().stream()
                    .filter(card -> card.getCategory().equals("Foe")) // Assuming card has a getCategory() method
                    .count();

            if (foeCardCount >= 3) {
                System.out.println(currentPlayer.getName() + " has chosen to sponsor the quest.");
                System.out.println("*********************************************");
                currentPlayer.setSponsor(true);
                setCurrentPlayer(currentPlayerIndex);
                //System.out.println("curent player: "+ currentPlayer.getName());
                for (Player player : players) {
                    if (player.getName() != currentPlayer.getName()) {
                        player.setSponsor(false);
                    }
                }
                return true; // Player has chosen to sponsor the quest
            } else {
                System.out.println("*********************************************");
                System.out.println("You do not have enough Foe cards to sponsor the quest.");
                System.out.print("Please respond with 'n' to decline sponsorship: ");
                String newResponse = getScannerInstance().nextLine().trim().toLowerCase();

                // Handle the new response
                while (!newResponse.equalsIgnoreCase("n")) {
                    System.out.print("Invalid input.");
                    System.out.println("You do not have enough Foe cards to sponsor the quest.");
                    System.out.print("Please respond with 'n' to decline sponsorship: ");
                    newResponse = getScannerInstance().nextLine().trim().toLowerCase();
                }
                System.out.println(currentPlayer.getName() + " has declined to sponsor the quest. (Ineligibility)");
                return false; // Player has declined the sponsorship
            }
        } else {
            System.out.println(currentPlayer.getName() + " has declined to sponsor the quest.");
            return false; // Player has declined the sponsorship
        }
    }

    public Player findSponsor(Player currentPlayer, List<Player> players) {
        // Get the index of the current player
        int currentPlayerIndex = players.indexOf(currentPlayer);

        // Start with the current player and loop through all players in order
        for (int i = 0; i < players.size(); i++) {
            // Calculate the current player in the loop (wrap around the list if needed)
            Player playerToAsk = players.get((currentPlayerIndex + i) % players.size());

            // Prompt the player to sponsor the quest
            System.out.println("*********************************************");
            System.out.println("Asking " + playerToAsk.getName() + " to sponsor the quest...");
            boolean sponsor = promptToSponsor(playerToAsk);

            //Fixing Bug - setting sponsor as current player:
            setCurrentPlayer((currentPlayerIndex + i) % players.size());


            // If the player agrees to sponsor the quest, return this player
            if (sponsor) {
                //System.out.println(playerToAsk.getName() + " has agreed to sponsor the quest.");
                return playerToAsk;
            } else {
                //System.out.println(playerToAsk.getName() + " has declined to sponsor the quest.");
            }
            Game.clearConsole();
        }

        // If no player agrees to sponsor the quest, return null
        System.out.println("All players have declined to sponsor the quest. The quest ends.");
        return null;
    }

    public static void clearConsole() {
        System.out.print("\n".repeat(100)); // Prints 100 newlines to simulate a cleared screen
        System.out.flush();
    }

}