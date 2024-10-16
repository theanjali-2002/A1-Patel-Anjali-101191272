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
    private Player player;

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
            List<Card> cardsToDistribute = adventureDeck.drawACards(CARDS_PER_PLAYER); // Draw cards from the deck
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
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        if (eventDeck.countEventCards() == 0) {
            eventDeck.refillDeckFromDiscard();
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
                        deck.remove(index);
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
                System.out.println("Card Drawn: Plague card.\n Current player loses 2 shields.");
                currentPlayer.loseShields(2);
                break;

            case "Queen's favor":
                System.out.println("Card Drawn: Queen's favor card. \n Current player will draw 2 adventure cards.");
                currentPlayer.receiveCards(adventureDeck.drawACards(2));
                currentPlayer.trimHandTo12Cards();
                break;

            case "Prosperity":
                System.out.println("Card Drawn: Prosperity Card. \n All players draw 2 adventure cards.");
                for (Player player : players) {
                    System.out.println(player.getName() + " has drawn 2 adventure cards.");
                    player.receiveCards(adventureDeck.drawACards(2));
                    player.trimHandTo12Cards();
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

    public Player findSponsor(Player currentPlayer, List<Player> players) {
        // Get the index of the current player
        int currentPlayerIndex = players.indexOf(currentPlayer);

        // Start with the current player and loop through all players in order
        for (int i = 0; i < players.size(); i++) {
            // Calculate the current player in the loop (wrap around the list if needed)
            Player playerToAsk = players.get((currentPlayerIndex + i) % players.size());

            // Prompt the player to sponsor the quest
            System.out.println("Asking " + playerToAsk.getName() + " to sponsor the quest...");
            boolean sponsor = promptToSponsor(playerToAsk);

            // If the player agrees to sponsor the quest, return this player
            if (sponsor) {
                //System.out.println(playerToAsk.getName() + " has agreed to sponsor the quest.");
                return playerToAsk;
            } else {
                //System.out.println(playerToAsk.getName() + " has declined to sponsor the quest.");
            }
        }

        // If no player agrees to sponsor the quest, return null
        System.out.println("All players have declined to sponsor the quest. The quest ends.");
        return null;
    }



    public static void main(String[] args) {
        //Scanner scanner = new Scanner(System.in);
        UserInterface userInterface = new UserInterface(); // Initialize user interface
        userInterface.displayGameStartMessage(true); // Display the game start message

        Game game = new Game(); // Create a new instance of the Game class
        Quest quest = new Quest();
        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();
        game.displayCurrentPlayerHand();
        //userInterface.displayPlayerTurn(game.getCurrentPlayer().getName());
        Card drewCard = game.drawEventCard();
        if (drewCard.getCategory() == "Event"){
            game.handleECardEffects(drewCard, game.getCurrentPlayer());
        } else {
            System.out.println("Quest");
            Player value = game.findSponsor(game.getCurrentPlayer(), game.getPlayers());
            if (value == null) {
                game.nextPlayer();
            } else {
                quest.setupQuest(game, drewCard);
                quest.promptParticipants(game.getPlayers(), game.getCurrentPlayer());
                quest.prepareForQuest(game);
                for (int i=0; i<quest.getNumberOfStages(); i++){
                    quest.prepareForStage(i, game);
                    quest.resolveStage(i, game);
                }

            }
        }


        // Other game logic code later
    }

}