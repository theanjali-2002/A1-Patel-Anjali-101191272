package org.example;

public class Main {
    public static void main(String[] args) {

        // Step 1: Start game, decks are created, hands of the 4 players are set up with random cards
        UserInterface userInterface = new UserInterface(); // Initialize user interface
        userInterface.displayGameStartMessage(true); // Display the game start message

        Game game = new Game();
        Quest quest = new Quest();

        game.initializeGameEnvironment();
        game.initializePlayers();
        game.distributeAdventureCards();

        Card drewCard;
        while (true) {
            drewCard = game.drawEventCard();
            if (drewCard.getCategory() == "Event"){
                game.handleECardEffects(drewCard, game.getCurrentPlayer());
                //next hot seat player here - DONE
            } else {
                System.out.println("it is a Quest card");
                Player value = game.findSponsor(game.getCurrentPlayer(), game.getPlayers());
                Player sponsor = new Player();
                for (Player player : game.getPlayers()){
                    if (player.isSponsor()) {
                        sponsor = player;
                    }
                }
                if (value == null) {
                    //game.nextPlayer();
                    //next hot seat player here since no sponsor so no quest - DONE
                    game.nextHotSeatPlayer();
                } else {
                    //System.out.println("current player in main: "+ game.getCurrentPlayer().getName());

                    //anywhere where game can end - get next hot seat player
                    quest.setupQuest(game, drewCard);
                    Game.clearConsole(); //here
                    quest.promptParticipants(game.getPlayers(), game.getCurrentPlayer());
                    for (int i=0; i<drewCard.getValue(); i++){
                        quest.prepareForQuest(game, i);

                        quest.prepareForStage(i, game, quest);
                        Game.clearConsole(); //here
                        quest.resolveStage(i, game);
                    }
                    if (!(quest.getWinners()== null)) {
                        //game.nextPlayer();
                        // quest game ends here to Next Hot Seat Player - DONE
                        game.nextHotSeatPlayer();
                    } else {
                        System.out.println("finish");
                    }
                }
            }
        }
    }
}