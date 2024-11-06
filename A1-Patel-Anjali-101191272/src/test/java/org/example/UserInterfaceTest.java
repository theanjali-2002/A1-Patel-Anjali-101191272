package org.example;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UserInterfaceTest {
    private UserInterface userInterface;
    private Game game;
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        userInterface = new UserInterface();
        // Set up to capture output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void simulateInput(String input) {
        ScannerSingleton.resetScanner(new ByteArrayInputStream(input.getBytes()));
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
        simulateInput("s\n");

        userInterface = new UserInterface(); // Use the injected scanner
        userInterface.displayGameStartMessage(true); // Call the method

        // Expected output after starting the game
        String expectedOutput = "Game Starting...\n"; // Replace with expected output for starting the game

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The game start message was not printed correctly.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test user input to quit the game")
    public void RESP_05_test_03() {
        simulateInput("q\n");
        userInterface.displayGameStartMessage(true); // Call the method
        String expectedOutput = "Game Exiting...\n"; // Replace with expected output for quitting the game
        assertTrue(outputStream.toString().contains(expectedOutput), "The game quit message was not printed correctly.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test invalid user input")
    public void RESP_05_test_04() {
        simulateInput("x\ns\n"); // Simulate pressing 'x' (invalid) then 's' (valid)
        userInterface.displayGameStartMessage(true); // Call the method
        String expectedOutput = "Invalid input! Please enter 's' to start or 'q' to quit.\n"; // Expectation for invalid input

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The invalid input message was not printed correctly.");
    }

    @Test
    @DisplayName("R-TEST-10: UI - Option to draw a card from the event deck")
    public void RESP_10_test_01() {
        String playerName = "P1";
        simulateInput("e\n");
        userInterface.displayPlayerTurn(playerName);
        String expectedOutputValid = "It is P1's turn on the hotseat!\r\n" +
                "Press 'e' to draw a card from the event deck...\r\n" +
                "P1 has drawn a card from the event deck!\r\n";
        assertEquals(expectedOutputValid, outputStream.toString(), "The player's turn message is incorrect for valid input.");
    }







}

