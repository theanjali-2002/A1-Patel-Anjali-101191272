package org.example;

import java.util.Scanner;

public class UserInterface {
    private Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    // Constructor with Scanner
    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
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
    public void displayPlayerTurn(String playerName) {}
}
