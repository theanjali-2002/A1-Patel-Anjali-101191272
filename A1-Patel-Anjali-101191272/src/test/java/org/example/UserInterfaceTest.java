package org.example;

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
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        userInterface = new UserInterface();
        System.setOut(new PrintStream(outputStream)); // Redirecting System.out to capture the output
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions")
    public void RESP_05_test_01() {
        userInterface.displayGameStartMessage(); // Call the method to display the message

        // Prepare the expected output
        String expectedOutput = "**************************************************\n" +
                "          Welcome to the 4004 Assignment 1 Game!          \n" +
                "**************************************************\n" +
                "\nInstructions:\n" +
                "1. 🧙‍♂️ Accumulate 7 shields to become a knight!\n" +
                "2. 🃏 Draw adventure cards to complete quests.\n" +
                "3. 🎯 Successfully complete quests to earn shields.\n" +
                "4. 🏆 Players with 7 or more shields at the end of a quest win!\n" +
                "\n" +
                "✨ Good luck, and may the best knight prevail! ✨\n" +
                "**************************************************\n" +
                "Press 'S' to Start Game\n" +
                "Press 'Q' to Quit Game\n"; // Include button instructions in the expected output

        // Validate the output
        assertEquals(expectedOutput, outputStream.toString(), "The displayed game start message is incorrect.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test user input to start the game")
    public void RESP_05_test_02() {
        // Simulate user input for starting the game
        String simulatedInput = "S\n"; // Simulate pressing 'S' and then Enter
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in); // Set the input stream to simulate user input

        userInterface = new UserInterface(new Scanner(System.in)); // Use the injected scanner
        userInterface.displayGameStartMessage(); // Call the method

        // Expected output after starting the game
        String expectedOutput = "Game Starting...\n"; // Replace with expected output for starting the game

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The game start message was not printed correctly.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test user input to quit the game")
    public void RESP_05_test_03() {
        // Simulate user input for quitting the game
        String simulatedInput = "Q\n"; // Simulate pressing 'Q' and then Enter
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in); // Set the input stream to simulate user input

        userInterface = new UserInterface(new Scanner(System.in)); // Use the injected scanner
        userInterface.displayGameStartMessage(); // Call the method

        // Expected output after quitting the game
        String expectedOutput = "Game Exiting...\n"; // Replace with expected output for quitting the game

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The game quit message was not printed correctly.");
    }

    @Test
    @DisplayName("R-Test-05: Display game start message and instructions; Test invalid user input")
    public void RESP_05_test_04() {
        // Simulate invalid user input
        String simulatedInput = "X\nS\n"; // Simulate pressing 'X' (invalid) then 'S' (valid)
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in); // Set the input stream to simulate user input

        userInterface = new UserInterface(new Scanner(System.in)); // Use the injected scanner
        userInterface.displayGameStartMessage(); // Call the method

        // Expected output after invalid input
        String expectedOutput = "Invalid input! Please enter 'S' to start or 'Q' to quit.\n"; // Expectation for invalid input

        // Validate the output
        assertTrue(outputStream.toString().contains(expectedOutput), "The invalid input message was not printed correctly.");
    }



}

