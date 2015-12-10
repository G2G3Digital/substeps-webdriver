substeps-webdriver [![Build Status](https://travis-ci.org/G2G3Digital/substeps-webdriver.svg)](https://travis-ci.org/G2G3Digital/substeps-webdriver) [![Maven Central](https://img.shields.io/maven-central/v/com.technophobia.substeps/webdriver-substeps.png?label=webdriver-substeps)](https://maven-badges.herokuapp.com/maven-central/com.technophobia.substeps/webdriver-substeps) 
==================

Webdriver based step implementations. 

Substeps documentation can be found [here](http://substeps.technophobia.com/ "Substeps documentation").  

There is also a [Substeps Google group](http://groups.google.com/group/substeps?hl=en-GB "Substeps Google group") if you have any queries and where new releases will ne announced.

Release Notes
=============

1.1.4
-----
* Move to v2.0 of core, library update across the board
* tidied up some javadocs
* Requires JDK 8


1.1.3
-----
* addition of ExecutionListener config in the pom, set to default step logger implemenation.
* Add in proxy capabilities for other browsers [Peter Phillips]
* Enabled full screen mode for visual browsers
* Refactored MatchingElementResultHandler into it's own class
* Browser js logs now printed via enabling trace on WebDriverBrowserLogs
* Clicks now wait until an item is clickable
* Travis-CI integration


1.1.2
-----
* Catch StaleElementExceptions in FindByTagAndAttributes - elements located in the initial search can become detached from the DOM, such elements can be discared from the results. 
* Removed some dead substeps in the self tests. 

1.1.1
-----
* New step implementation, FindParentByTagAndAttributes.
* Refactored some methods out of this project into the api
* 1.1.1 core and api dependency
* selenium 2.35.0 dependency
* Ability to reset webdriver between scenarios rather than close and restart
* WebDriverFactory customisation to allow customisation of the creation and initialisation of the WebDriver instance
* Clarify some Assertions and Finders, we've decided to remove Asserts that are simply Finders, and also tighten the wording of some steps.  Changes to steps :
    AssertTagElementStartsWithText -> FindFirstTagElementStartingWithText
    AssertValue id msg_id text = "Hello World" -> FindById msg_id and text = "Hello World"
    AssertChildElementsContainText xpath="li//a" text = "Log Out" -> FindFirstChildElementContainingText xpath="li//a" text = "Log Out"
    AssertTagElementContainsText tag="ul" text="list item itext" ->  FindFirstTagElementContainingText tag="ul" text="list item itext"
    FindTagElementContainingText ... -> FindFirstTagElementContainingText ...
* AssertPageSourceContains can now handle checking for quoted strings, eg checking javascript values or return values from ReSTful calls.
* new step implementation FindFirstChild ByTagAndAttributes
* new step implementation Find nth ByTagAndAttributes

1.1.0
-----
* Upgraded dependency on webdriver-java to 2.28.0
* Some changes to work with api which has been split out of core
* Addition of Table row related functionality; FindTableRowWithColumnsThatContainText, FindElementInRow etc
* DismissAlert step implementation

1.0.2
-----
* Refactored some of the Webdriver locating code into new 'By' classes
* DoubleClick and Context click support
* removed Email steps from webdriver-susbteps - these were based on Dumbster and not reliable (caused deadlock)

1.0.0
-----
* Added a property to disable js with HTMLUnit.
* move to 1.0.0 versions of other substeps libraries

0.0.6
-----
* Increased version of substeps-core and substeps-runner for enhanced reporting

0.0.5
-----
* Modified StepImplementation classes to specify the required initialisation classes
* Changed the dependency on activation.jar to an available version

 
0.0.4
-----
* Changes as a result of core changes to Notifications.
* fixed the base url when specifying relative paths on Windows.
* enabled use of the IE driver.
* removed unused config property.
* BUG: fix around FindChildByTagAndAttributes - wasn't actually finding children, siblings were also being reported.
* inclusion of step implementation glossary info
* exposed the DriverType from WebDriverContext
* improved some of the step implementation docs
* BUG: NavigateTo now uses base.url property unless the url begins with file or http
