Using Webdriver-Substeps
========================

For full instructions to set up a webdriver-substeps project refer to the main substeps `documentation <http://technophobia.github.com/substeps/getting_started.html>`_

With the appropriate dependencies set up, substeps can be composed of the stepImplementations listed `here <stepimplementations.html>`_.

To run the webdriver tests using Chrome, you will also need to install `ChromeDriver <http://code.google.com/p/selenium/wiki/ChromeDriver>`_.

Environment specific settings
-----------------------------

- It is likely you will need to tailor the execution of tests slightly for various environments, for example in a continuous integration server environment, 
  using the HTMLUnit driver probably makes the most sense, for developers and test authors the visual feedback of Firefox or Chrome is more preferable.

+--------------------------------+-------------------------------------------------------------------+-+
| Property                       | Description                                                       | |
+================================+===================================================================+=+
| base.url                       | used as the prefix for NavigateTo <url> instructions.  Vary this  | |
|                                | for targetting at different environments                          | |
|                                | use a relative or absolute path to use the file: protocol         | |
+--------------------------------+-------------------------------------------------------------------+-+
| driver.type                    | HTMLUNIT, FIREFOX, CHROME                                         | |
+--------------------------------+-------------------------------------------------------------------+-+
| default.webdriver.timeout.secs | The default value in seconds that webdriver will wait for a       | |
|                                | given condition                                                   | |
+--------------------------------+-------------------------------------------------------------------+-+
| visual.webdriver.close.on.fail | true by default, if set to false, when using Chrome or Firefox    | |
|                                | and an error occurs, the browser window is retained to aide       | |
|                                | debugging                                                         | |
+--------------------------------+-------------------------------------------------------------------+-+
| webdriver.shutdown             | true by default, override if you wish to keep the browswer        | |
|                                | windows open. Beware, this will consume lots of resources!        | |
+--------------------------------+-------------------------------------------------------------------+-+
| htmlunit.disable.javascript    | false by default, set to true to disable javascript with HTMLUnit | |
+--------------------------------+-------------------------------------------------------------------+-+


   