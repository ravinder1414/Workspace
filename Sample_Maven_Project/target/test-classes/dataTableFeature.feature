@wip
Feature: DataTable based feature file

  Scenario: Second User creation
    Given I am in users page
    When I click on new user link
    And I enter user details as:
      | First name | Last name | Email address        | Username   | Password   | Phone      | Address     | Postal Code | DobYear | DobMonth | DobDay | Sex    | Comments    |
      | SecondUser | LastName  | SecondUser@gmail.com | SecondUser | password12 | 1234567890 | Sushant Lok | 122002      | 1991    | jan      | 21     | Female | Test User 2 |
    And click create user button
    Then verify user is on confirm page
