package org.example;

public class GameInputManager {
    private static String input = null;

    // This is called by the API to set input
    public static synchronized void setInput(String newInput) {
        input = newInput;
    }

    // This is called by the game to get input
    public static synchronized String getInput() {
        while (input == null) { // Wait until input is provided
            try {
                Thread.sleep(100); // Small delay to avoid overloading CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String userInput = input; // Get the input
        input = null; // Clear the input after reading
        return userInput;
    }
}
