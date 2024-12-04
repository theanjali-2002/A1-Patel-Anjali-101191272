package org.example;
//import static org.example.ScannerSingleton.getScannerInstance;

import java.util.Scanner;

public class UserInterface {
    private Game game;

    public UserInterface() {
        this.game = new Game();
    }

    // Method to display game start message and instructions
    public boolean displayGameStartMessage(boolean waitForInput) {
        // Display the game start message
        OutputRedirector.println("***********************************************************");
        OutputRedirector.println("          Welcome to the 4004 Assignment 1 Game!          ");
        OutputRedirector.println("***********************************************************");
        OutputRedirector.println("");
        OutputRedirector.println("Instructions:");
        OutputRedirector.println("1. 🧙‍♂️ Accumulate 7 shields to become a knight!");
        OutputRedirector.println("2. 🃏 Draw adventure cards to complete quests.");
        OutputRedirector.println("3. 🎯 Successfully complete quests to earn shields.");
        OutputRedirector.println("4. 🏆 Players with 7 or more shields at the end of a quest win!");
        OutputRedirector.println("");
        OutputRedirector.println("✨ Good luck, and may the best knight prevail! ✨");
        OutputRedirector.println("***********************************************************");
        OutputRedirector.println("Press 's' to Start Game");
        OutputRedirector.println("Press 'q' to Quit Game");

        // Handle user input if specified
        if (waitForInput) {
            while (true) {
                String userInput = ScannerSingleton.nextLine().trim().toLowerCase();

                if ("s".equals(userInput)) {
                    OutputRedirector.println("Game Starting...\n");
                    return true;
                } else if ("q".equals(userInput)) {
                    OutputRedirector.println("Game Exiting...\n");
                    return false;
                } else {
                    OutputRedirector.println("Invalid input! Please enter 's' to start or 'q' to quit.\n");
                }
            }
        }
        return true;
    }

    // Method to display the current player's turn, their hand, and ask to draw an event card
    public void displayPlayerTurn(String CurrentplayerName) {
        OutputRedirector.println("It is " + CurrentplayerName + "'s turn on the hotseat!");
        //game.displayCurrentPlayerHand(); // Call the method from Game class to show the player's hand
        OutputRedirector.println("Press 'e' to draw a card from the event deck...");

        // Wait for player to press the space bar or 'e' key to draw a card
        while (true) {
            String input = ScannerSingleton.nextLine().trim();
            if ("e".equalsIgnoreCase(input)) {
                OutputRedirector.println(CurrentplayerName + " has drawn a card from the event deck!");
                break; // Exit the loop once valid input is received
            } else {
                OutputRedirector.println("Invalid input! Please press 'e' to draw a card.");
            }
        }
    }

}
