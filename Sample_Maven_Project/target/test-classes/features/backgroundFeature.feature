@wip
Feature: simple feature file

  Background:
    Given I am in users page

  Scenario: creating new user 1
    Then I click on new user link
    And enter user first name as FirstName
    And enter user last name as LastName
    And enter user email id as First.Last@gmail.com
    And enter user username as UserName
    And enter user password as Password
    And enter user phone as 12345677891
    And enter user address as TW Gurgaon
    And enter user postcode as 122002
    And enter user year of dob as 1991
    And enter user month of dob as March
    And enter user day of dob as 21
    And enter user sex as Female
    And enter user comments as attending vodqa
    And click create user button
    Then verify user is on confirm page

  Scenario: creating new user 2
    Then I click on new user link
    And enter user first name as FirstName
    And enter user last name as LastName
    And enter user email id as First.Last@gmail.com
    And enter user username as UserName
    And enter user password as Password
    And enter user phone as 12345677891
    And enter user address as TW Gurgaon
    And enter user postcode as 122002
    And enter user year of dob as 1991
    And enter user month of dob as March
    And enter user day of dob as 21
    And enter user sex as Female
    And enter user comments as attending vodqa
    And click create user button
    Then verify user is on confirm page