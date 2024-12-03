package org.example;
import java.util.*;

public class GameService {
    private final Game game;
    private final Quest quest;
    private Card lastDrawnCard;

    public GameService() {
        game = new Game();
        quest = new Quest();
        lastDrawnCard = null;
    }

    public void startGame() {
        UserInterface userInterface = new UserInterface();
        userInterface.displayGameStartMessage(true);

        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();

        while (true) {
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




}
