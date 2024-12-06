package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import static org.example.ScannerSingleton.getScannerInstance;

public class Quest {
    private String questId;
    private String questType;
    private int numberOfStages;
    private List<String> participants;
    private List<String> winners;
    private int totalShieldsAwarded;
    private int currentStage;
    private String status;
    private List<String> discardedCards;
    private List<Card> cardDeck;
    private List<Stage> stages;
    private Game game;
    private AdventureDeck adventureDeck;
    private EventDeck eventDeck;
    private Player player;
    private Stage stage;
    private List<Card> cardsUsedBySponsor; // Track all cards used by the sponsor


    public Quest() {
        this.participants = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.discardedCards = new ArrayList<>();
        this.cardDeck = new ArrayList<>();
        this.stages = new ArrayList<>();
        this.cardsUsedBySponsor = new ArrayList<>();
        game = new Game();
        adventureDeck = new AdventureDeck();
        eventDeck = new EventDeck();
        player = new Player();
        stage = new Stage();
    }

    public Quest(String questId, String questType, String sponsorId, int numberOfStages) {
        this.questId = questId;
        this.questType = questType;
        this.numberOfStages = numberOfStages;
        this.participants = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.discardedCards = new ArrayList<>();
        this.currentStage = 0;
        this.status = "active";
        this.totalShieldsAwarded = 0;
        this.cardDeck = new ArrayList<>();
        this.stages = new ArrayList<>();
        this.game = new Game();
    }

    public void setupQuest(Game game, Card drawnQcard) {
        // Clear previous quest data
        System.out.println("DEBUG [Quest.setupQuest] Before clearing - Participants: " + participants);
        System.out.println("DEBUG [Quest.setupQuest] Before clearing - Stages: " + stages.size());

        this.participants.clear();
        this.stages.clear();
        this.winners.clear();
        this.cardsUsedBySponsor.clear();

        System.out.println("DEBUG [Quest.setupQuest] After clearing - Participants: " + participants);
        System.out.println("DEBUG [Quest.setupQuest] After clearing - Stages: " + stages.size());
        setNumberOfStages(drawnQcard.getValue());

        for (int i = 0; i < numberOfStages; i++) {
            OutputRedirector.println("*********************************************");
            OutputRedirector.println("Sponsor is setting up Stage " + (i + 1));
            String stageId = "Stage-" + (i + 1);
            List<Card> cardsInStage = new ArrayList<>();
            int stageValue = 0;

            game.displayPlayerHand(game.getCurrentPlayer());
            game.setGameState("Sponsor is setting up the Quest's Stages!");


            boolean validStageSetup = false;
            boolean hasFoeCard = false;
            while (!validStageSetup) {
                OutputRedirector.println("Enter the position of the next card to include or 'q' to finish this stage:");

                    String input = ScannerSingleton.nextLine();

                    if (input.equalsIgnoreCase("q")) {
                        if (cardsInStage.isEmpty()) {
                            OutputRedirector.println("A stage cannot be empty");
                        } else if (stageValue <= (i > 0 ? stages.get(i - 1).getStageValue() : 0)) {
                            OutputRedirector.println("Insufficient value for this stage");
                        } else {
                            for (Card card : cardsInStage) {
                                if (card.getCategory().equals("Foe")) {
                                    hasFoeCard = true;
                                    break;
                                }
                            }
                            if (!hasFoeCard) {
                                OutputRedirector.println("You cannot quit; at least one card with category 'Foe' is required in this stage.");
                            } else {
                                validStageSetup = true; // Stage is valid
                            }
                        }
                    } else {
                        // Validate the card position
                        try {
                            int cardPosition = Integer.parseInt(input); // Convert to int
                            Card card = getCard(cardPosition - 1, game);

                            if (card != null && isValidCard(card, cardsInStage)) {
                                cardsInStage.add(card);
                                cardsUsedBySponsor.add(card);
                                OutputRedirector.println("*********************************************");
                                OutputRedirector.println("Added card: " + card);

                                // Print the cards currently in the stage
                                OutputRedirector.println("Current cards in Stage " + (i + 1) + ": " + cardsInStage);

                                stageValue = calculateStageValue(cardsInStage); // Update stage value

                                // Discard the card from player's hand
                                game.getCurrentPlayer().discardACardFromHand(card);

                                // Print updated player's hand
                                game.displayCurrentPlayerHand();
                            } else {
                                OutputRedirector.println("Invalid card selection. Must be a sole foe or non-repeated weapon card.");
                            }
                        } catch (NumberFormatException e) {
                            OutputRedirector.println("Invalid position. Please enter a valid card position.");
                        }
                    }

            }

            // After building the stage, set its value based on the cards
            Stage stage = new Stage(stageId, stageValue, cardsInStage);
            stages.add(stage);

            // Print details of the created stage
            OutputRedirector.println("Stage created: " + stage.getStageId());
            OutputRedirector.println("*********************************************");
        }
        OutputRedirector.println("Quest setup completed!");
        OutputRedirector.println("*********************************************");
        OutputRedirector.println("*********************************************");
    }


    private Card getCard(int position, Game game) {
        cardDeck = game.getCurrentPlayer().getHand();
        // Check if the position is within the valid range
        if (position < 0 || position >= cardDeck.size()) {
            OutputRedirector.println("Invalid position: " + position + ". It must be between 0 and " + (cardDeck.size() - 1) + ".");
            return null; // Return null if the position is invalid
        }

        OutputRedirector.println(cardDeck.get(position).getCardName());
        // Return the card from the deck based on the position
        return cardDeck.get(position);

    }


    // Method to check if the selected card is valid
    private boolean isValidCard(Card card, List<Card> existingCards) {
        // Check if the card is non-repeated and if it's a valid type
        return !existingCards.contains(card) && (card.getCategory().equals("Weapon") || card.getCategory().equals("Foe"));
    }

    // Method to calculate the stage value based on cards
    private int calculateStageValue(List<Card> cards) {
        int totalValue = 0;
        for (Card card : cards) {
            totalValue += card.getValue();
        }
        return totalValue; // Return the total value of the stage
    }

    // In general at first if they want to play the quest or not
    public void promptParticipants(List<Player> players, Player sponsor) {
        //game.setGameState("Asking players to participate in Quest!");
        for (Player player : players) {
            if (!player.equals(sponsor)) {
                OutputRedirector.println(player.getName() + ", do you want to participate in the quest? (y/n)");
                String response = ScannerSingleton.nextLine().trim();
                if (response.equalsIgnoreCase("y")) {
                    participants.add(player.getName());
                    OutputRedirector.println(player.getName() + " joined the quest.");
                    OutputRedirector.println("*********************************************");
                } else {
                    OutputRedirector.println(player.getName() + " declined to join the quest.");
                    OutputRedirector.println("*********************************************");
                }
                Game.clearConsole();
            }
        }
    }

    //asking for each individual stage of quest if they want to continue or not
    public void promptEachStage(List<String> participants, Player sponsor, int stageNumber) {
        game.setGameState("Asking players to participate in Stage-"+ stageNumber);

        Iterator<String> iterator = participants.iterator();

        while (iterator.hasNext()) {
            String participant = iterator.next();
            if (!participant.equals(sponsor.getName())) {
                OutputRedirector.println(participant + ", do you want to participate in Stage - " + stageNumber + "? (y/n)");
                String response = ScannerSingleton.nextLine();
                if (response.equalsIgnoreCase("y")) {
                    OutputRedirector.println(participant + " joined to participate.");
                    OutputRedirector.println("*********************************************");
                } else {
                    iterator.remove();  // Safely remove using iterator
                    OutputRedirector.println(participant + " declined to participate.");
                    OutputRedirector.println("*********************************************");
                }
                Game.clearConsole();
            }
        }
    }

    //for participants now. they will be asked if they want to play particular stage in the quest.
    // for every participant who agreed to play, will draw cards and trim
    public void prepareForQuest(Game game, int stageNumber) {
        Player sponsor = new Player();
        for (Player player : game.getPlayers()){
            if (player.isSponsor()) {
                sponsor = player;
            }
        }
        promptEachStage(participants, sponsor, stageNumber + 1);
        for (String participant : participants) {
            Player player = game.getPlayerByName(participant); // Retrieve the player from game
            List<Card> drawnCards = game.getAdventureDeck().drawACards(1);
            player.receiveCards(drawnCards); // Each participant draws one adventure card
            player.sortHand(player.getHand());
            player.trimHandTo12Cards(player); // Ensure they don’t exceed card limit
            Game.clearConsole();
        }
    }

    public void prepareForStage(int stageIndex, Game game, Quest quest) {
        Stage stage = stages.get(stageIndex);
        Iterator<String> iterator = participants.iterator(); // Create an iterator for the participants list

        while (iterator.hasNext()) {
            String participant = iterator.next(); // Get the next participant
            //OutputRedirector.println("Participant in method: " + participant);
            Player player = game.getPlayerByName(participant);

            if (player == null) {
                OutputRedirector.println("Error: Player '" + participant + "' could not be found in the game.");
                iterator.remove(); // Remove participant if player not found
                continue; // Skip to the next participant
            }

            // Prepare the player's attack for the stage
            try {
                int attackValue = player.prepareAttackForStage(stage, player);
                //System.out.flush();
                if (attackValue == 0) { // Check if the attack value is 0 (indicating player couldn't attack)
                    OutputRedirector.println(participant + " cannot attack and will be removed from participants.");
                    iterator.remove(); // Remove participant if they cannot attack
                } else {
                    stage.recordAttack(participant, attackValue);
                }
            } catch (Exception e) {
                OutputRedirector.println("\n");
            }
        }
    }



    public void resolveStage(int stageIndex, Game game) {
        game.setGameState("Resolving Stage!");
        Stage stage = stages.get(stageIndex);

        // First check if there are participants
        if (participants.isEmpty()) {
            OutputRedirector.println("No participants left. Quest ends!!!");
            endQuestWithoutWinners();  // End the quest if no participants remain
            return;  // Exit early since there are no participants to process
        }

        // Iterate over a copy of the participants list to safely remove participants
        OutputRedirector.println("*********************************************");
        for (String participant : new ArrayList<>(participants)) {
            int attackValue = stage.getAttacks().get(participant);
            if (attackValue < stage.getStageValue()) {
                OutputRedirector.println(participant + " failed to pass stage " + (stageIndex + 1));
                participants.remove(participant);  // Remove the participant if attack value is less than stage value
            }
        }
        OutputRedirector.println("*********************************************");

        // Check if participants remain after processing
        System.out.println("DEBUG: Participants when resolving stage: " + participants);
        OutputRedirector.println("DEBUG: Participants when resolving stage: " + participants);

        if (participants.isEmpty()) {
            OutputRedirector.println("No participants left. Quest ends.");
            endQuestWithoutWinners();  // End the quest if no participants remain after attack resolution
            Player sponsor = game.getCurrentPlayer();

            // Sponsor draws the same number of adventure cards + additional cards for each stage
            int stagesCount = stages.size();
            int questCards = cardsUsedBySponsor.size();
            int totalCardsToDraw = questCards + stagesCount;
            List<Card> drawnCards = game.getAdventureDeck().drawACards(totalCardsToDraw);
            sponsor.receiveCards(drawnCards);
            OutputRedirector.println("Calling Sponsor ... Loading ... ...");
            OutputRedirector.println("*********************************************");
            sponsor.trimHandTo12Cards(sponsor);
        } else if (stageIndex + 1 < stages.size()) {
            //prepareForStage(stageIndex + 1, game, quest);  // Move to the next stage if there are more stages
            OutputRedirector.println("\n");
            //OutputRedirector.println("DEBUG: Participants when resolving stage lastly???????: " + stageIndex+1 + " ...stage size: " + stages.size());
        } else {
            //OutputRedirector.println("DEBUG: resolving winners called======================>" + participants);
            System.out.println("DEBUG: resolving winners called======================>");
            resolveWinners(game);  // If final stage is completed, resolve the winners
        }

    }

    public void resolveWinners(Game game) {
        game.setGameState("Resolving Winners!");
        // The participants who have completed all stages are the winners of the quest.
        OutputRedirector.println("Resolving winners...");
        int shieldReward = stages.size(); // Each winner gets shields equal to the number of stages
        //OutputRedirector.println("total shields: "+ shieldReward);

        if (participants.isEmpty()) {
            OutputRedirector.println("No participants left to resolve winners.");
            endQuestWithoutWinners(); // No participants left means no winners
            //game.nextPlayer();
            return;
        }

        boolean hasGameWinner = false;
        List<String> gameWinners = new ArrayList<>();

        // Iterate over remaining participants and award them shields
        OutputRedirector.println("*********************************************");
        for (String participant : participants) {
            Player winner = game.getPlayerByName(participant); // Fetch the player from the game
            if (winner != null) {
                winner.gainShields(shieldReward); // Add shields to the winner's score
                OutputRedirector.println(participant + " has won the quest and earned " + shieldReward + " shields!");

                // Add the winner to the Quest's winners list
                winners.add(participant);

                // Check if this player has won the game (>= 7 shields)
                if (winner.getShields() >= 7) {
                    gameWinners.add(participant); // Collect participant's name if they are a game winner
                }
            } else {
                OutputRedirector.println("Error: Participant " + participant + " could not be found in the game.");
            }
        }

        if (!gameWinners.isEmpty()) {
            game.setGameState(gameWinners + " is/are the winner(s) of the Game!");
        } else {
            game.setGameState(winners + " is/are the winner(s) of the Quest!");
        }

        OutputRedirector.println("*********************************************");
        // Print all winners of the quest
        OutputRedirector.println("Quest Winners: " + winners);
        OutputRedirector.println("*********************************************");

        // Sponsor discards all cards used to build the quest
        Player sponsor = game.getCurrentPlayer();
        int questCards = cardsUsedBySponsor.size();

        // Sponsor draws the same number of adventure cards + additional cards for each stage
        int stagesCount = stages.size();
        int totalCardsToDraw = questCards + stagesCount;
        List<Card> drawnCards = game.getAdventureDeck().drawACards(totalCardsToDraw);
        sponsor.receiveCards(drawnCards);
        OutputRedirector.println("Calling Sponsor ... Loading ... ...");

        OutputRedirector.println("*********************************************");
        OutputRedirector.println(sponsor.getName() + " draws " + totalCardsToDraw + " adventure cards as the sponsor!\n");

        // If necessary, the sponsor trims their hand to 12 cards
        OutputRedirector.println(sponsor.getName() + ", your hand has to be reduced to 12 cards!");
        sponsor.trimHandTo12Cards(sponsor);
        Game.clearConsole();


        if (!gameWinners.isEmpty()) {
            // Print a single message for all game winners
            OutputRedirector.println("╔═════════════════════════════════════════════════════════════╗");
            OutputRedirector.println("║                                                             ");
            OutputRedirector.println("║      CONGRATULATIONS, CHAMPION(s)!                          ");
            OutputRedirector.println("║                                                             ");
            OutputRedirector.println("╠═════════════════════════════════════════════════════════════╣");
            OutputRedirector.println("║                                                             ");
            OutputRedirector.printf("   ~~ %s have reached a legendary milestone! ~~                    \n", String.join(", ", gameWinners));
            OutputRedirector.println("║                                                             ");
            OutputRedirector.println("║   With 7 or more shields, these champions are crowned       ");
            OutputRedirector.println("║   WINNERS in this epic quest!                               ");
            OutputRedirector.println("║                                                             ");
            OutputRedirector.println("╚═════════════════════════════════════════════════════════════╝");

            // End the game since winners have been found
            OutputRedirector.println("Game Over. Thank you for playing!");
            return;
        }

        // The quest ends after resolving the winners
        OutputRedirector.println("The quest has ended. The game will continue.");
        //game.nextPlayer();
    }

    public void endQuestWithoutWinners() {
        // Fancy ASCII art to enhance the UI
        OutputRedirector.println("*****************************************************");
        OutputRedirector.println("*                                                   *");
        OutputRedirector.println("*        ⚔️ QUEST ENDED WITH NO WINNERS ⚔️          *");
        OutputRedirector.println("*                                                   *");
        OutputRedirector.println("*****************************************************");

        OutputRedirector.println("\n\n      ────── ✦ ────── ");
        OutputRedirector.println("      Darkness falls upon the land...");
        OutputRedirector.println("      The brave heroes have failed to complete the quest.");
        OutputRedirector.println("      All hope is lost... until the next adventure!");
        OutputRedirector.println("      ────── ✦ ────── ");

        OutputRedirector.println("\nReturning to the main game flow...\n");
    }



    // Getters and Setters
    public String getQuestId() {
        return questId;
    }

    public String getQuestType() {
        return questType;
    }

    public void setNumberOfStages(int num) {
        this.numberOfStages = num;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public List<String> getWinners() {
        return winners;
    }

    public int getTotalShieldsAwarded() {
        return totalShieldsAwarded;
    }

    public void setTotalShieldsAwarded(int totalShieldsAwarded) {
        this.totalShieldsAwarded = totalShieldsAwarded;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getDiscardedCards() {
        return discardedCards;
    }

    public void setCurrentStage(int stage) {
        if (stage >= 0 && stage < numberOfStages) {
            this.currentStage = stage;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Stage> getStages() { // Getter for stages
        return stages;
    }

    public void setCardsUsedBySponsor(List<Card> cardsUsedBySponsor) {
        this.cardsUsedBySponsor = cardsUsedBySponsor;
    }



}
