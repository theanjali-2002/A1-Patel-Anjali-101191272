package org.example;

import java.util.Scanner;

public class UserInterface {
    private Scanner scanner;
    private Game game;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
        this.game = new Game();
    }

    // Constructor with Scanner
    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
        this.game = new Game();
    }

    // Method to display game start message and instructions
    public void displayGameStartMessage(boolean waitForInput) {
        // Display the game start message
        System.out.println("***********************************************************");
        System.out.println("          Welcome to the 4004 Assignment 1 Game!          ");
        System.out.println("***********************************************************");
        System.out.println();
        System.out.println("Instructions:");
        System.out.println("1. 🧙‍♂️ Accumulate 7 shields to become a knight!");
        System.out.println("2. 🃏 Draw adventure cards to complete quests.");
        System.out.println("3. 🎯 Successfully complete quests to earn shields.");
        System.out.println("4. 🏆 Players with 7 or more shields at the end of a quest win!");
        System.out.println();
        System.out.println("✨ Good luck, and may the best knight prevail! ✨");
        System.out.println("***********************************************************");
        System.out.println("Press 's' to Start Game");
        System.out.println("Press 'q' to Quit Game");

        // Handle user input if specified
        if (waitForInput) {
            while (true) {
                String userInput = scanner.nextLine().trim().toLowerCase();

                if ("s".equals(userInput)) {
                    System.out.println("Game Starting...\n");
                    break; // Exit the loop if input is valid
                } else if ("q".equals(userInput)) {
                    System.out.println("Game Exiting...\n");
                    break; // Exit the loop if input is valid
                } else {
                    System.out.println("Invalid input! Please enter 's' to start or 'q' to quit.\n");
                }
            }
        }
    }

    // Method to display the current player's turn, their hand, and ask to draw an event card
    public void displayPlayerTurn(String CurrentplayerName) {
        System.out.println("It is " + CurrentplayerName + "'s turn on the hotseat!");
        //game.displayCurrentPlayerHand(); // Call the method from Game class to show the player's hand
        System.out.println("Press 'e' to draw a card from the event deck...");

        // Wait for player to press the space bar or 'e' key to draw a card
        while (true) {
            String input = scanner.nextLine().trim();
            if ("e".equalsIgnoreCase(input)) {
                System.out.println(CurrentplayerName + " has drawn a card from the event deck!");
                break; // Exit the loop once valid input is received
            } else {
                System.out.println("Invalid input! Please press 'e' to draw a card.");
            }
        }
    }

    public void gameStatus(Game game) {
        System.out.println("*********************************************");
        System.out.println("**************** Game Status ****************");
        System.out.println("*********************************************");
        System.out.println("Current Player: " + game.getCurrentPlayer().getName());

        System.out.println("Player Shields:");
        for (Player player : game.getPlayers()) {
            System.out.println(player.getName() + " - Shields: " + player.getShields());
        }
        System.out.println("*********************************************");
        System.out.println("*********************************************");
    }
}
