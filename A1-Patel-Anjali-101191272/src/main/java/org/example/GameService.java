package org.example;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GameService {
    private Game game;
    private Quest quest;
    private Card lastDrawnCard;
    private boolean isRunning = true;
    private Thread gameThread;
    AdventureDeck adventureDeck;
    EventDeck eventDeck;

    public GameService() {
        //resetGame();
    }

    private boolean resetGame() {
        game = new Game();
        quest = new Quest();
        lastDrawnCard = null;
        UserInterface userInterface = new UserInterface();
        return userInterface.displayGameStartMessage(true);
    }

    public void stopGame() {
        isRunning = false;
        if (gameThread != null && gameThread.isAlive()) {
            try {
                gameThread.join(); // Wait for the thread to terminate
            } catch (InterruptedException e) {
                OutputRedirector.println("Error while stopping the game thread: " + e.getMessage());
            }
        }
    }

    public void startGame() {
        if (!resetGame()) {
            OutputRedirector.println("Game has been terminated by the user.");
            stopGame();
            return; // Exit if the user chooses to quit
        }
        isRunning = true;
        gameThread = new Thread(() -> {

            game.initializeGameEnvironment();
            game.initializePlayers();
            game.distributeAdventureCards();

            while (isRunning) {
                lastDrawnCard = game.drawEventCard();
                if ("Event".equals(lastDrawnCard.getCategory())) {
                    game.handleECardEffects(lastDrawnCard, game.getCurrentPlayer());
                } else {
                    OutputRedirector.println("It is a Quest card");
                    Player sponsor = game.findSponsor(game.getCurrentPlayer(), game.getPlayers());
                    if (sponsor == null) {
                        game.nextHotSeatPlayer();
                    } else {
                        quest.setupQuest(game, lastDrawnCard);
                        game.setGameState("Asking players to participate in Quest!");
                        quest.promptParticipants(game.getPlayers(), game.getCurrentPlayer());

                        for (int i = 0; i < lastDrawnCard.getValue(); i++) {
                            quest.prepareForQuest(game, i);
                            quest.prepareForStage(i, game, quest);
                            quest.resolveStage(i, game);
                        }

                        if (quest.getWinners() == null) {
                            game.nextHotSeatPlayer();
                        } else {
                            OutputRedirector.println("Quest finished!");
                        }
                    }
                }
            }
        });
        gameThread.start();
    }


    public Map<String, Object> getGameState() {
        Map<String, Object> gameState = new HashMap<>();

        // Add general game info
        gameState.put("progressMessage", game.getGameState());
        //gameState.put("currentPlayer", game.getCurrentPlayer().getName());
        gameState.put("hotSeatPlayer", game.getHotSeatPlayer().getName());

        if (lastDrawnCard != null) {
            gameState.put("cardDrawn", lastDrawnCard.getCardName()); // Use the last drawn card
        } else {
            gameState.put("cardDrawn", "No card drawn yet");
        }

        String sponsorName = "No Sponsor"; // Default value
        for (Player player : game.getPlayers()) {
            if (player.isSponsor()) {
                sponsorName = player.getName();
                break; // Exit the loop once the sponsor is found
            }
        }
        gameState.put("sponsor", sponsorName);

        // Add player stats
        List<Map<String, Object>> players = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            Map<String, Object> playerData = new HashMap<>();
            playerData.put("name", player.getName());
            playerData.put("shields", player.getShields());
            playerData.put("hand", player.getHand().size()); // Number of cards in hand
            players.add(playerData);
        }
        gameState.put("players", players);

        return gameState; // This will be converted to JSON automatically by Spring Boot
    }



    public void rigHandsForPlayers(List<Card> cards, String playerName) {
        game.getPlayerByName(playerName).setClearHand(cards);
        game.getPlayerByName(playerName).clearReceivedCardEvents();
    }

    public void rigDecksForGame(List<Card> eDeck, List<Card> aDeck) {
        adventureDeck = game.getAdventureDeck();
        adventureDeck.clearDeck();
        System.out.println("A deck after clear: "+ adventureDeck.getDeck().size());
        adventureDeck.setDeck(aDeck);

        eventDeck = game.getEventDeck();
        eventDeck.clearDeck();
        System.out.println("E deck after clear: "+ eventDeck.getDeck().size());
        eventDeck.setDeck(eDeck);
    }





}
