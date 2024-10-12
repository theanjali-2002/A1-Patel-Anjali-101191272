//The Game class file manages the game logic,
//including initializing the game environment,
//setting up decks, and controlling the flow of gameplay.
package org.example;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private AdventureDeck adventureDeck;
    private EventDeck eventDeck;
    private List<Player> players;

    // Constructor
    public Game() {
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
        players = new ArrayList<>();
    }

    // Methods to initialize the game environment
    public void initializeGameEnvironment() {
        adventureDeck.setupDeck(); // RESP-02 Set up the adventure deck
        eventDeck.setupDeck(); // RESP-03 Set up the event deck
    }

    public void initializePlayers() {
        //code later
    }

    // Getters for testing
    public AdventureDeck getAdventureDeck() {
        return adventureDeck;
    }

    public EventDeck getEventDeck() {
        return eventDeck;
    }

    public List<Player> getPlayers() {return players;}

}
