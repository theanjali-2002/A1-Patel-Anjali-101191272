package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UserInterfaceTest {
    private UserInterface userInterface;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private Game game;

    @BeforeEach
    public void setUp() {
        //game = new Game();
        //game.initializeGameEnvironment();
        //game.initializePlayers();
        //game.distributeAdventureCards();

        userInterface = new UserInterface();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream)); // Redirecting System.out to capture the output
    }

    @AfterEach
    public void tearDown() {
        // Restore the original System.out
        System.setOut(originalOut); // Restore original System.out after tests
        outputStream.reset(); // Reset the output stream for the next test
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions")
    public void RESP_05_test_01() {
        userInterface.displayGameStartMessage(false); // Call the method to display the message

        // Prepare the expected output
        String expectedOutput = "***********************************************************\r\n" +
                "          Welcome to the 4004 Assignment 1 Game!          \r\n" +
                "***********************************************************\r\n" +
                "\r\nInstructions:\r\n" +
                "1. 🧙‍♂️ Accumulate 7 shields to become a knight!\r\n" +
                "2. 🃏 Draw adventure cards to complete quests.\r\n" +
                "3. 🎯 Successfully complete quests to earn shields.\r\n" +
                "4. 🏆 Players with 7 or more shields at the end of a quest win!\r\n" +
                "\r\n" +
                "✨ Good luck, and may the best knight prevail! ✨\r\n" +
                "***********************************************************\r\n" +
                "Press 's' to Start Game\r\n" +
                "Press 'q' to Quit Game\r\n"; // Include button instructions in the expected output

        // Validate the output
        assertEquals(expectedOutput, outputStream.toString(), "The displayed game start message is incorrect.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test user input to start the game")
    public void RESP_05_test_02() {
        // Simulate user input for starting the game
        String simulatedInput = "s\n"; // Simulate pressing 's' and then Enter
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in); // Set the input stream to simulate user input

        userInterface = new UserInterface(new Scanner(System.in)); // Use the injected scanner
        userInterface.displayGameStartMessage(true); // Call the method

        // Expected output after starting the game
        String expectedOutput = "Game Starting...\n"; // Replace with expected output for starting the game

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The game start message was not printed correctly.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test user input to quit the game")
    public void RESP_05_test_03() {
        // Simulate user input for quitting the game
        String simulatedInput = "q\n"; // Simulate pressing 'q' and then Enter
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in); // Set the input stream to simulate user input

        userInterface = new UserInterface(new Scanner(System.in)); // Use the injected scanner
        userInterface.displayGameStartMessage(true); // Call the method

        // Expected output after quitting the game
        String expectedOutput = "Game Exiting...\n"; // Replace with expected output for quitting the game

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The game quit message was not printed correctly.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test invalid user input")
    public void RESP_05_test_04() {
        // Simulate invalid user input
        String simulatedInput = "x\ns\n"; // Simulate pressing 'x' (invalid) then 's' (valid)
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in); // Set the input stream to simulate user input

        userInterface = new UserInterface(new Scanner(System.in)); // Use the injected scanner
        userInterface.displayGameStartMessage(true); // Call the method

        // Expected output after invalid input
        String expectedOutput = "Invalid input! Please enter 's' to start or 'q' to quit.\n"; // Expectation for invalid input

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The invalid input message was not printed correctly.");
    }

    @Test
    @DisplayName("R-TEST-10: UI - Option to draw a card from the event deck")
    public void RESP_10_test_01() {
        String playerName = "P1";

        // Simulate valid input (pressing space bar)
        String simulatedInputValid = "e"; // Simulate pressing space bar
        InputStream inValid = new ByteArrayInputStream(simulatedInputValid.getBytes());
        System.setIn(inValid); // Simulate input for the space key

        userInterface = new UserInterface(new Scanner(System.in)); // Use the injected scanner
        userInterface.displayPlayerTurn(playerName); // Call the method with the playerName

        // Expected output: Check for the correct turn message for the valid input
        String expectedOutputValid = "It is P1's turn on the hotseat!\r\n" +
                "Press 'e' to draw a card from the event deck...\r\n" +
                "P1 has drawn a card from the event deck!\r\n";

        // Validate the output for the valid case
        assertEquals(expectedOutputValid, outputStream.toString(), "The player's turn message is incorrect for valid input.");
    }







}

