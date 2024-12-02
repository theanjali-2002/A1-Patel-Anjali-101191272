package org.example;

public class ScannerSingleton {
    private static ScannerSingleton instance;
    private static String input = null;

    private ScannerSingleton() {
        // Prevent instantiation
    }

    // Singleton instance getter
    public static synchronized ScannerSingleton getInstance() {
        if (instance == null) {
            instance = new ScannerSingleton();
        }
        return instance;
    }

    // Method for API to set input
    public synchronized void setInput(String newInput) {
        System.out.println("Setting input to: " + newInput); // Debug log
        input = newInput; // Update the input
        notifyAll(); // Notify waiting threads
    }



    // Method for game logic to get input (replaces Scanner.nextLine())
    public synchronized String getInput() {
        while (input == null) {
            try {
                System.out.println("Waiting for input..."); // Debug log
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String userInput = input;
        input = null; // Clear input after reading
        System.out.println("Fetched input: " + userInput); // Debug log
        return userInput;
    }



    // Static method to provide a Scanner-like API
    public static String nextLine() {
        return ScannerSingleton.getInstance().getInput(); // Fetch input from the Singleton
    }
}
