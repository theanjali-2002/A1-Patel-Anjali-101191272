package org.example;

public class GameService {
    private final Game game;
    private final Quest quest;

    public GameService() {
        game = new Game();
        quest = new Quest();
    }

    public void startGame() {
        UserInterface userInterface = new UserInterface();
        userInterface.displayGameStartMessage(true);

        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();

        while (true) {
            Card drewCard = game.drawEventCard();
            if ("Event".equals(drewCard.getCategory())) {
                game.handleECardEffects(drewCard, game.getCurrentPlayer());
            } else {
                OutputRedirector.println("It is a Quest card");
                Player sponsor = game.findSponsor(game.getCurrentPlayer(), game.getPlayers());
                if (sponsor == null) {
                    game.nextHotSeatPlayer();
                } else {
                    quest.setupQuest(game, drewCard);
                    quest.promptParticipants(game.getPlayers(), game.getCurrentPlayer());

                    for (int i = 0; i < drewCard.getValue(); i++) {
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
}
