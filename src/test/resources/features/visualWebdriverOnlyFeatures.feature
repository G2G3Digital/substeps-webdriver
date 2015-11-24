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
    
    Given I click the Dont click me button   
    Then I see an Alert "I told you not to click me!"
    
# TODO this scenario isn't currently correct - the js actually removes the button from the dom and puts another one back in, which is not the use case...
#Scenario: Is item clickable
#	Given I go to the self test page
#	Then I click a button which makes the div I'm going to click visible in 2 seconds time
#    Check to see if I wait until the div I've just made visbile in 2 secs is clickable    