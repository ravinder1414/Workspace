Feature: Simpletest user feature file

  Scenario: creating new api user
    Given I am a new users page
    When I click on new page user link
    And enter first user first name as FirstName2
    Then verify users is on confirm page