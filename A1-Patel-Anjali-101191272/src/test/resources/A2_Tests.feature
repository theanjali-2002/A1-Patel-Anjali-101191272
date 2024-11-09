Feature: Quest Game

  Scenario: Displaying the game start message
    Given the user interface is initialized
    When the game start message is displayed with input "s"
    Then the game start "Game Starting..." is shown


  Scenario: A1_scenario
    Given the game is initialized with 4 players and decks are set up
    And hands for all players are rigged with specified cards
    And event and Adventure decks are rigged

    When player draws the rigged event card with input "e"

    Then player "P2" becomes the sponsor with input "n\ny\n"
    And sponsor sets up the 4 stages of quest with input "1\n6\nq\n2\n5\nq\n2\n2\nq\n1\n1\n4\nq\n"

    And players are asked to participate in the Quest and everyone joins saying "y\ny\ny\n"

    And stage 1 proceeds, asking eligible players "P1,P3,P4" to join and draw and discard cards as given "y\ny\ny\n1\n1\n1\n"
    And all players make attacks for Stage 1 with "5\n10\nq\n4\n10\nq\n6\n7\nq\n"
      And each player draws card "F30,S10,B15" for Stage 1
      And each player prepares attack of "15,15,15" for Stage 1
      And resolve stage 1 to check each player is left with "10,10,10" cards on their hand

    And stage 2 proceeds, asking eligible players "P1,P3,P4" to join and draw and discard cards as given "y\ny\ny\n"
    And all players make attacks for Stage 2 with "6\n7\nq\n4\n9\nq\n5\n7\nq\n"
      And each player draws card "F10,L20,L20" for Stage 2
      And each player prepares attack of "20,25,25" for Stage 2
      And resolve stage 2 to check each player is left with "9,9,9" cards on their hand

    And stage 3 proceeds, asking eligible players "P3,P4" to join and draw and discard cards as given "y\ny\n"
    And all players make attacks for Stage 3 with "5\n6\n9\nq\n4\n7\n8\nq\n"
      And each player draws card "B15,S10" for Stage 3
      And each player prepares attack of "40,45" for Stage 3
      And resolve stage 3 to check each player is left with "7,7" cards on their hand

    And stage 4 proceeds, asking eligible players "P3,P4" to join and draw and discard cards as given "y\ny\n"
    And all players make attacks for Stage 4 with "6\n7\n8\nq\n4\n5\n6\n8\nq\n"
      And each player draws card "F30,L20" for Stage 4
      And each player prepares attack of "45,65" for Stage 4
      And sponsor trims their hand with "1\n2\n3\n4\n"
      And resolve stage 4 to check each player is left with "5,4" cards on their hand

    And the final game state should verify sponsor with trimmed hand with 12 cards
      And player "P1" has 0 shields with hand "F5,F10,F15,F15,F30,H10,B15,B15,L20"
      And player "P3" has 0 shields with hand "F5,F5,F15,F30,S10"
      And player "P4" has 4 shields with hand "F15,F15,F40,L20"


  Scenario: 0_winner_quest
    Given the game is initialized with 4 players and decks are set up
    And hands for all players are rigged with specified cards
    And event Q2 and Adventure decks are rigged

    When player draws the rigged event card with input "e"

    Then player "P1" becomes the sponsor with input "y\n"
    And sponsor sets up the 2 stages of quest with input "3\nq\n2\n2\nq\n"
    And players are asked to participate in the Quest and everyone joins saying "y\ny\ny\n"

    And stage 1 proceeds, asking eligible players "P2,P3,P4" to join and draw and discard cards as given "y\ny\ny\n1\n1\n1\n"
    And all players make attacks for Stage 1 with "7\nq\n10\nq\n7\nq\n1\n2\n3\n4\n1\n2\n3\n4\n"
      And each player draws card "F30,S10,B15" for Stage 1
      And each player prepares attack of "10,5,5" for Stage 1
      And sponsor trims their hand with "1\n2\n"
      And resolve stage 1 to check each player is left with "11,11,11" cards on their hand

    And the final game state should verify sponsor with trimmed hand with 12 cards
    And player "P2" has 0 shields with hand ""
    And player "P3" has 0 shields with hand ""
    And player "P4" has 0 shields with hand ""



#  Scenario: 2winner_game_2winner_quest
#    Given the game is initialized with 4 players and decks are set up
#    And hands for P1, P2, P3, and P4 are rigged with specified cards
#    And event and Adventure decks are rigged
#
#    When P1 draws the rigged Quest Q4 card and decides to sponsor with input "e\ny\n"
#    And P1 sets up the stages of quest with input ""
#    And players are asked to participate in the Quest and everyone joins saying "y\ny\ny\n"
#    And all players play stage 1 and win with attacks ""
#    And all players play stage 2 and win with attacks ""
#    And all players play stage 3 and win with attacks ""
#    And all players play stage 4 and win with attacks ""
#
#    Then the final game state should verify
#    And
