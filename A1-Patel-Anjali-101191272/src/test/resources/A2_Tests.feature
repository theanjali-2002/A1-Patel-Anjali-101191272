Feature: Quest Game

  Scenario: Displaying the game start message
    Given the user interface is initialized
    When the game start message is displayed with input "s\n"
    Then the game start "Game Starting..." is shown


  Scenario: A1_scenario
    Given the game is initialized with 4 players and decks are set up
    And hands for all players are rigged with specified cards
    And event and Adventure decks are rigged

    When player draws the rigged event card

    Then player "P2" becomes the sponsor
    And sponsor sets up the 4 stages of quest with input "1\n6\nq\n2\n5\nq\n2\n2\nq\n1\n1\n4\nq\n"
    And players are asked to participate in the Quest and declines are from ""

    And stage 1 proceeds with eligible players "P1,P3,P4" where "" declines, each discarding "F5,F5,F5"
    #And all players make attacks for Stage 1 with "5\n10\nq\n4\n10\nq\n6\n7\nq\n"
    And all players make attacks for Stage 1 as given:
      | Player | Cards    |
      | P1     | D5, S10  |
      | P3     | S10, D5  |
      | P4     | D5, H10  |


    And each player draws card "F30,S10,B15" for Stage 1
      And each player prepares attack of "15,15,15" for Stage 1
      And resolve stage 1 to check each player is left with "10,10,10" cards

    And stage 2 proceeds with eligible players "P1,P3,P4" where "" declines, each discarding ""
    #And all players make attacks for Stage 2 with "6\n7\nq\n4\n9\nq\n5\n7\nq\n"
    And all players make attacks for Stage 2 as given:
      | Player | Cards    |
      | P1     | H10, S10 |
      | P3     | B15, S10 |
      | P4     | H10, B15 |

    And each player draws card "F10,L20,L20" for Stage 2
      And each player prepares attack of "20,25,25" for Stage 2
      And resolve stage 2 to check each player is left with "9,9,9" cards

    And stage 3 proceeds with eligible players "P3,P4" where "" declines, each discarding ""
    #And all players make attacks for Stage 3 with "5\n6\n9\nq\n4\n7\n8\nq\n"
    And all players make attacks for Stage 3 as given:
      | Player | Cards         |
      | P3     | L20, H10, S10 |
      | P4     | B15, S10, L20 |

      And each player draws card "B15,S10" for Stage 3
      And each player prepares attack of "40,45" for Stage 3
      And resolve stage 3 to check each player is left with "7,7" cards

    And stage 4 proceeds with eligible players "P3,P4" where "" declines, each discarding ""
    #And all players make attacks for Stage 4 with "6\n7\n8\nq\n4\n5\n6\n8\nq\n"
    And all players make attacks for Stage 4 as given:
      | Player | Cards             |
      | P3     | B15, H10, L20     |
      | P4     | D5, S10, L20, E30 |

      And each player draws card "F30,L20" for Stage 4
      And each player prepares attack of "45,65" for Stage 4
      And for stage 4 sponsor trims their hand by discarding "F10,S10,L20,S10"
      And resolve stage 4 to check each player is left with "5,4" cards

    And the final game state should verify sponsor with trimmed hand with 12 cards
      And player "P1" has 0 shields with hand "F5,F10,F15,F15,F30,H10,B15,B15,L20"
      And player "P3" has 0 shields with hand "F5,F5,F15,F30,S10"
      And player "P4" has 4 shields with hand "F15,F15,F40,L20"




  Scenario: 2winner_game_2winner_quest
    Given the game is initialized with 4 players and decks are set up
    And hands for all players are rigged with specified cards
    And quest event cards and Adventure decks are rigged

    When player draws the rigged event card

    Then player "P1" becomes the sponsor
    And sponsor sets up the 4 stages of quest with input "1\n9\nq\n2\nq\n1\n6\nq\n2\n1\nq\n"
    And players are asked to participate in the Quest and declines are from ""
    And stage 1 proceeds with eligible players "P2,P3,P4" where "" declines, each discarding "F5,F5,F5"
      And all players make attacks for Stage 1 with "6\nq\n10\nq\n6\nq\n"
      And resolve stage 1 to check each player is left with "11,11,11" cards
    And stage 2 proceeds with eligible players "P2,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 2 with "10\nq\n9\nq\n"
      And resolve stage 2 to check each player is left with "11,11" cards
    And stage 3 proceeds with eligible players "P2,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 3 with "10\nq\n10\nq\n"
      And resolve stage 3 to check each player is left with "11,11" cards
    And stage 4 proceeds with eligible players "P2,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 4 with "12\nq\n12\nq\n"
      And for stage 4 sponsor trims their hand by discarding "H10,D5,D5,B15"
      And resolve stage 4 to check each player is left with "11,11" cards

    And the final game state should verify sponsor with trimmed hand with 12 cards
      And player "P2" has 4 shields with hand ""
      And player "P3" has 0 shields with hand ""
      And player "P4" has 4 shields with hand ""
      And quest winners found OR no one sponsored the quest returning to next hot seat player "r\n"

    And player draws the rigged event card
    And player "P3" becomes the sponsor
    And sponsor sets up the 3 stages of quest with input "1\nq\n1\n3\nq\n1\n2\nq\n"
    And players are asked to participate in the Quest and declines are from "P1"
    And stage 1 proceeds with eligible players "P2,P4" where "" declines, each discarding "F5,F5"
      And all players make attacks for Stage 1 with "10\nq\n8\nq\n"
      And resolve stage 1 to check each player is left with "11,11" cards
    And stage 2 proceeds with eligible players "P2,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 2 with "11\nq\n12\nq\n"
      And resolve stage 2 to check each player is left with "11,11" cards
    And stage 3 proceeds with eligible players "P2,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 3 with "12\n10\nq\n11\n12\nq\n"
      And resolve stage 3 to check each player is left with "10,10" cards

    And player "P2" has 7 shields with hand ""
    And player "P3" has 0 shields with hand ""
    And player "P4" has 7 shields with hand ""
    And player "P2,P4" declared as game winner




  Scenario: 1winner_game_with_events
    Given the game is initialized with 4 players and decks are set up
    And hands for all players are rigged with specified cards
    And multiple event cards and Adventure decks are rigged

    When player draws the rigged event card

    Then player "P1" becomes the sponsor
    And sponsor sets up the 4 stages of quest with input "1\nq\n1\n8\nq\n1\nq\n1\n1\nq\n"
    And players are asked to participate in the Quest and declines are from ""
    And stage 1 proceeds with eligible players "P2,P3,P4" where "" declines, each discarding "F5,F5,F5"
      And all players make attacks for Stage 1 with "9\nq\n10\nq\n7\nq\n"
      And resolve stage 1 to check each player is left with "11,11,11" cards
    And stage 2 proceeds with eligible players "P2,P3,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 2 with "7\nq\n4\nq\n5\nq\n"
      And resolve stage 2 to check each player is left with "11,11,11" cards
    And stage 3 proceeds with eligible players "P2,P3,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 3 with "10\nq\n9\nq\n8\nq\n"
      And resolve stage 3 to check each player is left with "11,11,11" cards
    And stage 4 proceeds with eligible players "P2,P3,P4" where "" declines, each discarding ""
      And all players make attacks for Stage 4 with "12\nq\n11\n10\nq\n12\nq\n"
      And for stage 4 sponsor trims their hand by discarding "H10,D5,B15,B15"
      And resolve stage 4 to check each player is left with "11,10,11" cards

    And the final game state should verify sponsor with trimmed hand with 12 cards
    And player "P2" has 4 shields with hand ""
    And player "P3" has 4 shields with hand ""
    And player "P4" has 4 shields with hand ""
    And quest winners found OR no one sponsored the quest returning to next hot seat player "r\n"

    # Event Cards Now
    And player draws the rigged event card
      And event card Plague is drawn by player "P2"
    And player draws the rigged event card
    And event card Prosperity is drawn by player "P3" and each player discards:
      | Player | Cards        |
      | P1     | F5, L20      |
      | P2     | F10          |
      | P3     | none         |
      | P4     | F15          |

    And player draws the rigged event card
      And event card Queen's Favor is drawn by player "P4" who discards "F10,F40"

    # Back to Quest Card Q3 in this test scenario
    And player draws the rigged event card
    And player "P1" becomes the sponsor
    And sponsor sets up the 3 stages of quest with input "2\nq\n1\n1\nq\n2\nq\n"
    And players are asked to participate in the Quest and declines are from ""
    And stage 1 proceeds with eligible players "P2,P3,P4" where "" declines, each discarding "F10,F5,F15"
      And all players make attacks for Stage 1 with "9\nq\n10\nq\n4\nq\n"
      And resolve stage 1 to check each player is left with "11,11,11" cards
    And stage 2 proceeds with eligible players "P2,P3" where "" declines, each discarding ""
      And all players make attacks for Stage 2 with "10\nq\n12\nq\n"
      And resolve stage 2 to check each player is left with "11,11" cards
    And stage 3 proceeds with eligible players "P2,P3" where "" declines, each discarding ""
      And all players make attacks for Stage 3 with "12\nq\n11\nq\n"
      And resolve stage 3 to check each player is left with "11,10" cards

    And player "P2" has 5 shields with hand ""
    And player "P3" has 7 shields with hand ""
    And player "P4" has 4 shields with hand ""
    And player "P3" declared as game winner





  Scenario: 0_winner_quest
    Given the game is initialized with 4 players and decks are set up
    And hands for all players are rigged with specified cards
    And event Q2 and Adventure decks are rigged

    When player draws the rigged event card

    Then player "P1" becomes the sponsor
    And sponsor sets up the 2 stages of quest with input "3\nq\n2\n2\nq\n"
    And players are asked to participate in the Quest and declines are from ""

    And stage 1 proceeds with eligible players "P2,P3,P4" where "" declines, each discarding "F5,F5,F5"
    And all players make attacks for Stage 1 with "7\nq\n10\nq\n7\nq\n"
    And each player draws card "F30,S10,B15" for Stage 1
    And each player prepares attack of "10,5,5" for Stage 1
    And for stage 1 sponsor trims their hand by discarding "F5,S10"
    And resolve stage 1 to check each player is left with "11,11,11" cards

    And the final game state should verify sponsor with trimmed hand with 12 cards
    And player "P2" has 0 shields with hand ""
    And player "P3" has 0 shields with hand ""
    And player "P4" has 0 shields with hand ""