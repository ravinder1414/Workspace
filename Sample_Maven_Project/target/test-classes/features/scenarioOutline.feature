@wip
Feature: scenario outline feature file

  Scenario Outline: Third new user
    Given I am in users page
    Then I click on new user link
    And enter user first name as <First name>
    And enter user last name as <Last name>
    And enter user email id as <Email address>
    And enter user username as <Username>
    And enter user password as <Password>
    And enter user phone as <Phone>
    And enter user address as <Address>
    And enter user postcode as <Postal code>
    And enter user year of dob as <DobYear>
    And enter user month of dob as <DobMonth>
    And enter user day of dob as <DobDay>
    And enter user sex as <Sex>
    And enter user comments as <Comments>
    And click create user button
    Then verify user is on confirm page

  Examples:
    | First name | Last name | Email address        | Username   | Password   | Phone      | Address     | Postal code | DobYear | DobMonth | DobDay | Sex    | Comments    |
    | SecondUser | LastName  | SecondUser@gmail.com | SecondUser | password12 | 1234567890 | Sushant Lok | 122002      | 1991    | jan      | 21     | Female | Test User 2 |
