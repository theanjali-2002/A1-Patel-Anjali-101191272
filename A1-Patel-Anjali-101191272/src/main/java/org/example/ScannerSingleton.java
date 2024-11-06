package org.example;

import java.util.Scanner;
import java.io.InputStream;

public class ScannerSingleton {
    private static Scanner scannerInstance;

    private ScannerSingleton() {
        // Prevent instantiation
    }

    public static Scanner getScannerInstance() {
        if (scannerInstance == null) {
            scannerInstance = new Scanner(System.in);
        }
        return scannerInstance;
    }

    // Method to reset Scanner with a new InputStream
    public static void resetScanner(InputStream newInputStream) {
        if (scannerInstance != null) {
            scannerInstance.close();
        }
        scannerInstance = new Scanner(newInputStream);
    }
}

