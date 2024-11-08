Feature: Quest Game

  Scenario: Displaying the game start message
    Given the user interface is initialized
    When the game start message is displayed with input "s"
    Then the welcome message "Game Starting..." is shown


  Scenario: A1_scenario
    Given the game is initialized with 4 players and decks are set up
    And hands for P1, P2, P3, and P4 are rigged with specified cards
    And event and Adventure decks are rigged

    When P1 draws a Quest Q4 card
    And P1 declines to sponsor the quest with input "n\n"
    And P2 sponsors and sets up the quest with 4 stages
    And stage 1 proceeds with players drawing and discarding cards as described
    And players P1, P3, and P4 each make attacks and go to the next stage
    And stage 2 proceeds with players drawing, discarding, and making attacks
    And players P1, P3, and P4 each make attacks and P1 loses in stage 2 and others go to the next stage
    And stage 3 proceeds with players P3 and P4 drawing, discarding, and making attacks
    And stage 4 proceeds with players P3 and P4 drawing, discarding, and making attacks

    Then the final game state should verify:
    And P1 has no shields and a specific hand
    And P3 has no shields and a specific hand
    And P4 has 4 shields and a specific hand
    And P2 discards quest cards and trims down to 12 cards after redrawing

