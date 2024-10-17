package org.example;

import java.util.Scanner;

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
}

