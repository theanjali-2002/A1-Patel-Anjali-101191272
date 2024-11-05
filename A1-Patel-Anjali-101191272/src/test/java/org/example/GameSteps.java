package org.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class GameSteps {

    private UserInterface userInterface;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Given("the user interface is initialized")
    public void the_user_interface_is_initialized() {
        userInterface = new UserInterface();
        System.setOut(new PrintStream(outputStreamCaptor)); // Capture System.out output
    }

    @When("the game start message is displayed")
    public void the_game_start_message_is_displayed() {
        userInterface.displayGameStartMessage(false); // Call without waiting for input
    }

    @Then("the welcome message {string} is shown")
    public void the_welcome_message_is_shown(String expectedMessage) {
        String output = outputStreamCaptor.toString().trim();
        Assert.assertTrue(output.contains(expectedMessage));
    }
}
