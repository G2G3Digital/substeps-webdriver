Webdriver Substeps - Release Notes
==================================

- A list of changes per release 

1.1.0
-----

- Upgraded dependency on webdriver-java to 2.28.0
- Some changes to work with api which has been split out of core
- Addition of Table row related functionality; FindTableRowWithColumnsThatContainText, FindElementInRow etc
- DismissAlert step implementation

1.0.2
-----
- Refactored some of the Webdriver locating code into new 'By' classes
- DoubleClick and Context click support
- removed Email steps from webdriver-susbteps - these were based on Dumbster and not reliable (caused deadlock)

1.0.0
-----
- Added a property to disable js with HTMLUnit.
- move to 1.0.0 versions of other substeps libraries

0.0.6
-----
- Increased version of substeps-core and substeps-runner for enhanced reporting

0.0.5
-----
- Modified StepImplementation classes to specify the required initialisation classes
- Changed the dependency on activation.jar to an available version

 
0.0.4
-----
- Changes as a result of core changes to Notifications.
- fixed the base url when specifying relative paths on Windows.
- enabled use of the IE driver.
- removed unused config property.
- BUG: fix around FindChildByTagAndAttributes - wasn't actually finding children, siblings were also being reported.
- inclusion of step implementation glossary info
- exposed the DriverType from WebDriverContext
- improved some of the step implementation docs
- BUG: NavigateTo now uses base.url property unless the url begins with file or http