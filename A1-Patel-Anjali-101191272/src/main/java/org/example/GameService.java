package org.example;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
@Scope("singleton")
public class GameService {
    @Value("${test.mode:default}")
    private String testMode;

    private Game game;
    private Quest quest;
    private Card lastDrawnCard;
    private boolean isRunning = true;
    private Thread gameThread;
    private String gameId;
    AdventureDeck adventureDeck;
    EventDeck eventDeck;
    private final Object lock = new Object();

    public void setTestMode(String mode) {
        this.testMode = mode;
        System.out.println("DEBUG: Test mode set to: " + testMode);
    }

    @PostConstruct
    private void init() {
        this.gameId = UUID.randomUUID().toString();
        System.out.println("DEBUG: GameService initialized with test mode: " + testMode);
        System.out.println("GameService singleton initialized with ID: " + gameId);
    }

    @PreDestroy  // Add this method
    public void cleanup() {
        stopGame();  // Stop the game thread
        game = null;
        quest = null;
        lastDrawnCard = null;
        System.out.println("GameService cleaned up for session: " + gameId);
    }

    private void resetGame() {
        if (this.gameId == null) {
            this.gameId = UUID.randomUUID().toString();
        }
        System.out.println("DEBUG: Test mode in resetGame is: " + testMode);
        if ("selenium".equals(testMode)) {
            System.out.println("DEBUG: Creating Game with test lambda");
            game = new Game(()->0);
        } else {
            System.out.println("DEBUG: Creating normal Game instance");
            game = new Game();
        }

        quest = new Quest();
        lastDrawnCard = null;
        System.out.println("Game initialized with ID: " + gameId);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        System.out.println("[" + timestamp + "] resetGame() gameId: " + gameId);

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
        synchronized (lock) {
            resetGame();
            game.initializeGameEnvironment();
            game.initializePlayers();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            System.out.println("[" + timestamp + "] startGame() called with gameId: " + gameId);
            System.out.println("DEBUG [GameService] Received riggedAdventureDeck: " + (riggedAdventureDeck != null ? riggedAdventureDeck.size() : "null"));
            System.out.println("DEBUG [GameService] Received riggedEventDeck: " + (riggedEventDeck != null ? riggedEventDeck.size() : "null"));
            System.out.println("DEBUG [GameService] Received riggedHands: " + (riggedHands != null ? riggedHands.size() : "null"));

            // Rigging phase: Override initialized decks and hands if rigging data is provided
            if (riggedAdventureDeck != null && riggedEventDeck != null && riggedHands != null) {
                rigDecksForGame(riggedEventDeck, riggedAdventureDeck);

                for (Map.Entry<String, List<Card>> entry : riggedHands.entrySet()) {
                    rigHandsForPlayers(entry.getValue(), entry.getKey());
                }

                validateRigging(riggedAdventureDeck, riggedEventDeck, riggedHands);
                System.out.println("Game rigged successfully with custom decks and hands.");
                System.out.println("Game rigged in startGame() with ID: " + gameId);
            } else {
                game.distributeAdventureCards();
                System.out.println("Game started without rigging with ID: " + gameId);
            }

            // Start the game logic
            UserInterface userInterface = new UserInterface();
            userInterface.displayGameStartMessage(true);

            isRunning = true;
            gameThread = new Thread(() -> {
                while (isRunning) {
                    System.out.println("DEBUG [GameThread] Drawing event card...");
                    lastDrawnCard = game.drawEventCard();

                    if ("Event".equals(lastDrawnCard.getCategory())) {
                        System.out.println("DEBUG [GameThread] Handling event card effects");
                        game.handleECardEffects(lastDrawnCard, game.getCurrentPlayer());

                    } else {
                        System.out.println("DEBUG [GameThread] Handling quest card");
                        OutputRedirector.println("It is a Quest card");

                        Player value = game.findSponsor(game.getCurrentPlayer(), game.getPlayers(), lastDrawnCard.getValue());
                        Player sponsor = new Player();
                        for (Player player : game.getPlayers()){
                            if (player.isSponsor()) {
                                sponsor = player;
                            }
                        }
                        if (value == null) {
                            game.nextHotSeatPlayer();
                        } else {
                            quest.setupQuest(game, lastDrawnCard);
                            game.setGameState("Asking players to participate in Quest!");
                            quest.promptParticipants(game.getPlayers(), game.getCurrentPlayer());

                            for (int i = 0; i < lastDrawnCard.getValue(); i++) {

                                System.out.println("\nDEBUG [GameService] ========= STAGE " + (i + 1) + " START =========");
                                System.out.println("DEBUG [GameService] Current participants before prepareForQuest: " + quest.getParticipants());
                                System.out.println("DEBUG [GameService] Calling prepareForQuest for stage " + (i + 1));

                                quest.prepareForQuest(game, i);

                                System.out.println("DEBUG [GameService] Completed prepareForQuest for stage " + (i + 1));
                                System.out.println("DEBUG [GameService] Participants after prepareForQuest: " + quest.getParticipants());
                                System.out.println("DEBUG [GameService] Calling prepareForStage for stage " + (i + 1));

                                quest.prepareForStage(i, game, quest);

                                System.out.println("DEBUG [GameService] Completed prepareForStage for stage " + (i + 1));

                                quest.resolveStage(i, game);

                                System.out.println("DEBUG [GameService] Completed resolveStage for stage " + (i + 1));
                                System.out.println("DEBUG [GameService] ========= STAGE " + (i + 1) + " END =========\n");
                            }

                            if (!(quest.getWinners()== null)) {
                                game.nextHotSeatPlayer();
                            } else {
                                OutputRedirector.println("!!!");
                            }
                        }
                    }
                }
            });
            gameThread.start();
        }
    }



    public Map<String, Object> getGameState() {
        synchronized(lock) {
            System.out.println("DEBUG [GameService] Game object exists: " + (game != null));
            System.out.println("DEBUG [GameService] Current game state: " + (game != null ? game.getGameState() : "null"));

            Map<String, Object> gameState = new HashMap<>();
            if (game == null) {
                gameState.put("progressMessage", "Game not initialized");
                gameState.put("hotSeatPlayer", "None");
                gameState.put("cardDrawn", "No card drawn yet");
                gameState.put("sponsor", "No Sponsor");
                gameState.put("players", Collections.emptyList());
                return gameState;
            }
    
            // Add general game info
            gameState.put("progressMessage", game.getGameState());
            gameState.put("hotSeatPlayer", game.getHotSeatPlayer().getName());
    
            if (lastDrawnCard != null) {
                gameState.put("cardDrawn", lastDrawnCard.getCardName());
            } else {
                gameState.put("cardDrawn", "No card drawn yet");
            }
    
            String sponsorName = "No Sponsor";
            for (Player player : game.getPlayers()) {
                if (player.isSponsor()) {
                    sponsorName = player.getName();
                    break;
                }
            }
            gameState.put("sponsor", sponsorName);
    
            // Add player stats
            List<Map<String, Object>> players = new ArrayList<>();
            for (Player player : game.getPlayers()) {
                Map<String, Object> playerData = new HashMap<>();
                playerData.put("name", player.getName());
                playerData.put("shields", player.getShields());
                playerData.put("hand", player.getHand().size());
    
                List<String> cardNames = player.getHand().stream()
                        .map(Card::getCardName)
                        .collect(Collectors.toList());
                playerData.put("cards", cardNames);
                players.add(playerData);
            }
            gameState.put("players", players);
            return gameState;
        }
    }



    public void rigHandsForPlayers(List<Card> cards, String playerName) {
        if (game == null || game.getPlayers().isEmpty()) {
            throw new IllegalStateException("Game or players are not initialized. Cannot rig hands.");
        }

        System.out.println("DEBUG [GameService.rigHandsForPlayers] Setting " + cards.size() + " cards for player " + playerName);
        System.out.println("DEBUG [GameService.rigHandsForPlayers] Player hand before rigging: " + game.getPlayerByName(playerName).getHand().size());

        game.getPlayerByName(playerName).setClearHand(cards);
        game.getPlayerByName(playerName).clearReceivedCardEvents();
    }

    public void rigDecksForGame(List<Card> eDeck, List<Card> aDeck) {
        if (game == null) {
            throw new IllegalStateException("Game is not initialized. Cannot rig decks.");
        }
        System.out.println("DEBUG [GameService.rigDecksForGame] Event Deck before rigging: " + (eventDeck != null ? eventDeck.getDeck().size() : "null"));
        System.out.println("DEBUG [GameService.rigDecksForGame] Adventure Deck before rigging: " + (adventureDeck != null ? adventureDeck.getDeck().size() : "null"));

        adventureDeck = game.getAdventureDeck();
        adventureDeck.clearDeck();
        adventureDeck.setDeck(aDeck);

        eventDeck = game.getEventDeck();
        eventDeck.clearDeck();
        eventDeck.setDeck(eDeck);
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
