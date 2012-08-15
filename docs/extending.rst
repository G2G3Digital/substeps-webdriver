Extending Webdriver Step implementations
========================================

If you are using the Webdriver substeps library, you might need to create some custom step implementations to deal with the specifics of your web application.  Implementing such methods is easy; the Webdriver-substeps library exposes a number of methods to aid writing custom webdriver based implementations:

The underlying WebDriver instance is tied to the current thread and can be accessed via the following static method:

.. code-block:: java

   import static com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown.getThreadLocalWebDriver;

NB. Running in parrallel threads is not recommended because the underlying WebDriver implementation might not be itself threadsafe.

Generally speaking, webdriver step implementations work on the basis of first locating a particular element, and then performing an operation or assertion on that element.  Individual finder, assertion and operation type methods have been exposed to enable bespoke step definitions to be created outside of java code.  A Webdriver context bound to the current thread is used to store both the webdriver instance and a reference to the current thread.  The context can be accessed via the following static method:

.. code-block:: java

   import static com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown.getThreadLocalWebDriverContext;

Any attempt to use the current element will first check that the element is not null, any attempt to locate a new current element should also set the field to null first and fail if the desired element is not found.

If you wish to extend the webdriver-substeps classes, your new StepImplementations class will also need to include the initialisation class ``DefaultExecutionSetupTearDown``

New step implementations can make use of and aggregate existing functions, simply instantiate the implementation class and invoke the appropriate method accordingly. For example:

.. code-block:: java

   @StepImplementations (requiredInitialisationClasses=DefaultExecutionSetupTearDown.class)
   public MyWebDriverStepImplementations {

      @Step("DoSomething with parameter \"([^\"]*)\"")
      public void exampleOne(final String param)  {
         final FinderWebDriverSubStepImplementations finder = new FinderWebDriverSubStepImplementations();
         finder.findById("some_id");
         ...
      
