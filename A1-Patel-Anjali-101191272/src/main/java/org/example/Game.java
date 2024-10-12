//The Game class file manages the game logic,
//including initializing the game environment,
//setting up decks, and controlling the flow of gameplay.
package org.example;

public class Game {
    private AdventureDeck adventureDeck;
    private EventDeck eventDeck;
    private int playerCount = 0;

    // Constructor
    public Game() {
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
    }

    // Methods to initialize the game environment
    public void initializeGameEnvironment() {
        // nothing for now
    }

    // Getters for testing
    public AdventureDeck getAdventureDeck() {
        return adventureDeck;
    }

    public EventDeck getEventDeck() {
        return eventDeck;
    }

    public int getPlayerCount() {
        return playerCount;
    }

}
