package org.example;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class OutputRedirector {
    private static final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private static final PrintStream customPrintStream = new PrintStream(outputStream);

    // Redirect System.out to the custom output stream
    public static void redirectOutput() {
        System.setOut(customPrintStream);
    }

    // Capture print calls
    public static void print(String message) {
        customPrintStream.print(message + "\n");
    }

    // Capture println calls
    public static void println(String message) {
        customPrintStream.println(message + "\n");
    }

    // Capture printf calls
    public static void printf(String format, Object... args) {
        customPrintStream.printf(format, args);
    }

    // Fetch the captured output
    public static String getOutput() {
        String output = outputStream.toString(); // Get the output as a string
        outputStream.reset(); // Clear the output stream
        return output.trim(); // Trim extra spaces or newlines
    }

}
