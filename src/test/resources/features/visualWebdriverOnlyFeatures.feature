Tags: @visual

Feature: A set of features that are only applicable for visual webdriver implementations

Scenario: a visual scenario
    Given I go to the self test page
    Given the context menu hasn't been clicked
    And I click the context menu
    Then I see "context has been clicked"
    
    Given that I've not double clicked the link
    And I double click the link
    Then I can see I've double clicked