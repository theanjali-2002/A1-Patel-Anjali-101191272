package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumTests {

    private WebDriver driver;

    //helper method to test number of shields and hand of a player
    private void validatePlayerState(int playerNumber, String expectedShields, String expectedHand) {
        WebElement playerShields = driver.findElement(By.id("player" + playerNumber + "-shields"));
        WebElement playerHand = driver.findElement(By.id("player" + playerNumber + "-hand"));

        assertEquals(expectedShields, playerShields.getText(), "Player " + playerNumber + " shields mismatch.");
        assertEquals(expectedHand, playerHand.getText(), "Player " + playerNumber + " hand mismatch.");
    }

    //helper method for input commands for longer games
    private void executeCommands(WebElement commandInput, String commands) throws InterruptedException {
        for (String command : commands.split("\n")) {
            commandInput.sendKeys(command);
            commandInput.sendKeys(Keys.RETURN);
            Thread.sleep(2000); // Add delay between commands to let the UI process
        }
    }


    @BeforeEach
    public void setUp() throws IOException {
        // Set the ChromeDriver path (skip this if added to PATH)
        System.setProperty("webdriver.chrome.driver", "C:/Users/thean/OneDrive/Desktop/Anjali/Fall24/comp4004/A1-Patel-Anjali-101191272/chromedriver-win64/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080");
    }

    @Test
    public void testGameTitle() throws InterruptedException {
        driver.get("http://localhost:8080");
        Thread.sleep(5000);

        // Verify the game title
        WebElement title = driver.findElement(By.className("game-title"));
        assertEquals("4004 Quest Game", title.getText());

        WebElement commandInput = driver.findElement(By.id("commandInput"));
        commandInput.sendKeys("s");
        commandInput.sendKeys(Keys.RETURN);
        Thread.sleep(5000);

        // Player 1
        WebElement player1Shields = driver.findElement(By.id("player1-shields"));
        WebElement player1Hand = driver.findElement(By.id("player1-hand"));
        assertEquals("0", player1Shields.getText());
        assertEquals("12", player1Hand.getText());

        // Validate initial state
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");
    }

    @Test
    public  void A1_scenario() throws MalformedURLException, ProtocolException, IOException, InterruptedException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/scenario1").openConnection();
        connection.setRequestMethod("POST");
        connection.getResponseCode();

        driver.get("http://localhost:8080");
        Thread.sleep(5000);

        WebElement commandInput = driver.findElement(By.id("commandInput"));
        commandInput.sendKeys("s");
        commandInput.sendKeys(Keys.RETURN);
        Thread.sleep(5000);

        // Validate initial state
        validatePlayerState(1, "0", "12");
        validatePlayerState(2, "0", "12");
        validatePlayerState(3, "0", "12");
        validatePlayerState(4, "0", "12");

        String phase1Commands = "e\nn\ny\n"; //p1 draws a card and declines to sponsor, p2 sponsors it
        executeCommands(commandInput, phase1Commands);

        String phase2Commands = "1\n6\nq\n"; //p2 sets up the Quest Stage 1
        executeCommands(commandInput, phase2Commands);
        validatePlayerState(2, "0", "10");

        String phase3Commands = "2\n5\nq\n"; //p2 sets up the Quest Stage 2
        executeCommands(commandInput, phase3Commands);
        validatePlayerState(2, "0", "8");

        String phase4Commands = "2\n2\nq\n"; //p2 sets up the Quest Stage 3
        executeCommands(commandInput, phase4Commands);
        validatePlayerState(2, "0", "6");

        String phase5Commands = "1\n1\n4\nq\n"; //p2 sets up the Quest Stage 3
        executeCommands(commandInput, phase5Commands);
        validatePlayerState(2, "0", "3");

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
