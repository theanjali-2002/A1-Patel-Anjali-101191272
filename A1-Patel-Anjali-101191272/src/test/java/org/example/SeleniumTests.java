package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SeleniumTests {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Set the ChromeDriver path (skip this if added to PATH)
        System.setProperty("webdriver.chrome.driver", "C:/Users/thean/OneDrive/Desktop/Anjali/Fall24/comp4004/A1-Patel-Anjali-101191272/chromedriver-win64/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080");
    }

    @Test
    public void testGameTitle() {
        driver.get("http://localhost:8080"); // Replace with your frontend URL

        // Verify the game title
        WebElement title = driver.findElement(By.className("game-title"));
        assertEquals("4004 Quest Game", title.getText());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
