//The Game class file manages the game logic,
//including initializing the game environment,
//setting up decks, and controlling the flow of gameplay.
package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void displayCurrentPlayerHand() {}

    public Player getCurrentPlayer() {
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


    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface(); // Initialize user interface
        userInterface.displayGameStartMessage(true); // Display the game start message

        Game game = new Game(); // Create a new instance of the Game class
        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();

        // Other game logic code later
    }

}
