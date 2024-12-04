package org.example;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        resetGame();
        game.initializeGameEnvironment();
        game.initializePlayers();
        System.out.println("Game initialized with players: " + game.getPlayers().size());
    }

    private void resetGame() {
        game = new Game();
        quest = new Quest();
        lastDrawnCard = null;
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

    public Game getGame() {
        return game;
    }

    public void startGame(List<Card> riggedAdventureDeck, List<Card> riggedEventDeck, Map<String, List<Card>> riggedHands) {
        // Rigging phase: Override initialized decks and hands if rigging data is provided
        if (riggedAdventureDeck != null && riggedEventDeck != null && riggedHands != null) {
            rigDecksForGame(riggedEventDeck, riggedAdventureDeck);

            for (Map.Entry<String, List<Card>> entry : riggedHands.entrySet()) {
                rigHandsForPlayers(entry.getValue(), entry.getKey());
            }

            validateRigging(riggedAdventureDeck, riggedEventDeck, riggedHands);
            System.out.println("Game rigged successfully with custom decks and hands.");
        }

        // Start the game logic
        UserInterface userInterface = new UserInterface();
        boolean ui = userInterface.displayGameStartMessage(true);
        if (!ui) {
            stopGame();
            return;
        }

        game.distributeAdventureCards();

        isRunning = true;
        gameThread = new Thread(() -> {
            while (isRunning) {
                lastDrawnCard = game.drawEventCard();
                System.out.println("drawn card should be Q4: "+ lastDrawnCard);
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

            // Add player's cards as a list of card names
            List<String> cardNames = player.getHand().stream()
                    .map(Card::getCardName)
                    .collect(Collectors.toList());
            playerData.put("cards", cardNames);

            players.add(playerData);
        }
        gameState.put("players", players);

        return gameState; // This will be converted to JSON automatically by Spring Boot
    }



    public void rigHandsForPlayers(List<Card> cards, String playerName) {
        if (game == null || game.getPlayers().isEmpty()) {
            throw new IllegalStateException("Game or players are not initialized. Cannot rig hands.");
        }
        game.getPlayerByName(playerName).setClearHand(cards);
        game.getPlayerByName(playerName).clearReceivedCardEvents();
        System.out.println("testing player hand: "+ game.getPlayerByName("P1").getHand().size());
    }

    public void rigDecksForGame(List<Card> eDeck, List<Card> aDeck) {
        if (game == null) {
            throw new IllegalStateException("Game is not initialized. Cannot rig decks.");
        }
        adventureDeck = game.getAdventureDeck();
        adventureDeck.clearDeck();
        System.out.println("A deck after clear: "+ adventureDeck.getDeck().size());
        adventureDeck.setDeck(aDeck);
        System.out.println("A deck after RIGGED: "+ adventureDeck.getDeck().size());
        System.out.println("A deck after RIGGED: "+ adventureDeck.getDeck());

        eventDeck = game.getEventDeck();
        eventDeck.clearDeck();
        System.out.println("E deck after clear: "+ eventDeck.getDeck().size());
        eventDeck.setDeck(eDeck);
        System.out.println("E deck after RIGGED: "+ eventDeck.getDeck().size());
    }

    private void validateRigging(List<Card> riggedAdventureDeck, List<Card> riggedEventDeck, Map<String, List<Card>> riggedHands) {
        if (riggedAdventureDeck == null || riggedAdventureDeck.isEmpty()) {
            throw new IllegalStateException("Rigged Adventure Deck is missing or empty.");
        }
        if (riggedEventDeck == null || riggedEventDeck.isEmpty()) {
            throw new IllegalStateException("Rigged Event Deck is missing or empty.");
        }
        if (riggedHands == null || riggedHands.isEmpty()) {
            throw new IllegalStateException("Rigged hands are missing or not provided for players.");
        }

        // Validate Adventure Deck
        List<Card> actualAdventureDeck = game.getAdventureDeck().getDeck();
        if (!new HashSet<>(actualAdventureDeck).equals(new HashSet<>(riggedAdventureDeck))) {
            throw new IllegalStateException("Mismatch in rigged Adventure Deck.");
        }

        // Validate Event Deck
        List<Card> actualEventDeck = game.getEventDeck().getDeck();
        if (!new HashSet<>(actualEventDeck).equals(new HashSet<>(riggedEventDeck))) {
            throw new IllegalStateException("Mismatch in rigged Event Deck.");
        }

        // Ensure each player in the game has rigged hands and validate each card
        for (Player player : game.getPlayers()) {
            String playerName = player.getName();
            if (!riggedHands.containsKey(playerName) || riggedHands.get(playerName).isEmpty()) {
                throw new IllegalStateException("Rigged hand for player " + playerName + " is missing or empty.");
            }

            List<Card> playerHand = player.getHand();
            List<Card> expectedHand = riggedHands.get(playerName);

            if (!new HashSet<>(playerHand).equals(new HashSet<>(expectedHand))) {
                throw new IllegalStateException("Mismatch in rigged hand for player " + playerName);
            }
        }
        System.out.println("Rigged data validated successfully.");
    }






}
