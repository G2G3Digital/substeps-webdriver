/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.technophobia.webdriver.substeps.impl;

import static org.hamcrest.CoreMatchers.is;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.substeps.model.SubSteps.StepImplementations;
import com.technophobia.webdriver.substeps.runner.Condition;
import com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown;
import com.technophobia.webdriver.substeps.runner.DriverType;
import com.technophobia.webdriver.substeps.runner.WebDriverSubstepsException;
import com.technophobia.webdriver.substeps.runner.WebdriverSubstepsConfiguration;
import com.technophobia.webdriver.util.WebDriverContext;

@StepImplementations(requiredInitialisationClasses = DefaultExecutionSetupTearDown.class)
public class ActionWebDriverSubStepImplementations extends AbstractWebDriverSubStepImplementations {

    private static final Logger logger = LoggerFactory.getLogger(ActionWebDriverSubStepImplementations.class);

    private final FinderWebDriverSubStepImplementations locator;


    public ActionWebDriverSubStepImplementations() {
        this.locator = new FinderWebDriverSubStepImplementations();
    }


    public ActionWebDriverSubStepImplementations(final FinderWebDriverSubStepImplementations locator,
            final Supplier<WebDriverContext> webDriverContextSupplier) {
        super(webDriverContextSupplier);
        this.locator = locator;
    }


    /**
     * Navigate to a url, if the url begins with http or file, the url will be
     * used as is, if a relative url is specified then it will be prepended with
     * the base url property
     * 
     * @example NavigateTo /myApp (will navigate to http://localhost/myApp if
     *          base.url is set to http://localhost)
     * @section Location
     * 
     * @param url
     *            the url
     */
    @Step("NavigateTo ([^\"]*)")
    public void navigateTo(final String url) {
        logger.debug("About to navigate to " + url);

        if (url.startsWith("file") || url.startsWith("http")) {
            webDriver().get(url);
        } else {
            webDriver().get(normaliseURL(url));
        }
    }


    /**
     * Find an element by id, then click it.
     * 
     * @example ClickById login
     * @section Clicks
     * 
     * @param id
     *            the id
     */
    @Step("ClickById ([^\"]*)")
    public void clickById(final String id) {
        logger.debug("About to click item with id " + id);
        this.locator.findById(id);
        click();
    }


    /**
     * Click (the current element)
     * 
     * @example Click
     * @section Clicks
     */
    @Step("Click")
    public void click() {
        logger.debug("About to click on current element");
        webDriverContext().getCurrentElement().click();
    }


    /**
     * Click the link "(....)" as it appears on the page
     * 
     * @example ClickLink "Contracts"
     * @section Clicks
     * @param linkText
     *            the link text
     */
    @Step("ClickLink \"([^\"]*)\"")
    public void clickLink(final String linkText) {
        logger.debug("About to click link with text " + linkText);
        webDriverContext().setCurrentElement(null);
        final WebElement elem = webDriver().findElement(By.linkText(linkText));
        Assert.assertNotNull("expecting to find a link: " + linkText, elem);
        webDriverContext().setCurrentElement(elem);
        elem.click();
    }


    /**
     * Click a button that has the text...
     * 
     * @example ClickButton submit
     * @section Clicks
     * @param buttonText
     *            the button text
     */
    @Step("ClickButton ([^\"]*)")
    public void clickButton(final String buttonText) {
        logger.debug("About to click button with text " + buttonText);
        webDriverContext().setCurrentElement(null);
        final WebElement elem = this.locator.findElementWithText("button", buttonText.trim());
        Assert.assertNotNull("expecting to find a button: " + buttonText, elem);
        webDriverContext().setCurrentElement(elem);
        elem.click();
    }


    @Step("ClickSubmitButton \"([^\"]*)\"")
    public void clickInput(final String buttonText) {
        logger.debug("About to click submit button with text " + buttonText);
        webDriverContext().setCurrentElement(null);
        final List<WebElement> elems = webDriver().findElements(By.tagName("input"));

        List<WebElement> matchingElems = null;
        for (final WebElement e : elems) {
            // does this WebElement have the attributes that we need!

            if (e.getAttribute("value").equals(buttonText)) {
                if (matchingElems == null) {
                    matchingElems = new ArrayList<WebElement>();
                }
                matchingElems.add(e);
            }

        }

        if (matchingElems != null && matchingElems.size() > 1) {
            // ambiguous
            Assert.fail("Found too many elements that meet this criteria");
            // TODO - need some more debug here
        }

        else if (matchingElems != null) {
            webDriverContext().setCurrentElement(matchingElems.get(0));
        }

        webDriverContext().getCurrentElement().click();
    }


    /**
     * Wait for the specified number of milliseconds
     * 
     * @example WaitFor 10
     * @section Location
     * @param value
     *            the value
     */
    @Step("WaitFor ([^\"]*)")
    public void waitFor(final String value) {
        logger.debug("About to wait for " + value + "ms");
        final long ms = Long.parseLong(value);
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException e) {
            logger.debug("interupt ex");
            // do we care?
        }
    }


    /**
     * Wait for the page title to change to the specified value
     * 
     * @example WaitForPageTitle "My Home Page"
     * @section Location
     * 
     * @param expectedTitle
     *            the expected title
     */
    @Step("WaitForPageTitle \"([^\"]*)\"")
    public void waitForPageTitle(final String expectedTitle) {
        logger.debug("Waiting for " + expectedTitle + " page");

        final boolean conditionMet = webDriverContext().waitForCondition(new Condition() {
            public boolean conditionMet() {
                final String pageTitle = webDriver().getTitle();
                logger.debug(String.format("wait for page. Expected='%s', actual='%s'", expectedTitle, pageTitle));
                return pageTitle.equals(expectedTitle);
            }

        });

        if (!conditionMet) {
            logger.debug(expectedTitle + " page not found");
            // Assert.fail("Webpage was not found within the time frame set.");
        }

        Assert.assertTrue(conditionMet);
    }


    private String normaliseURL(final String relativeURL) {
        return normalise(WebdriverSubstepsConfiguration.baseURL() + relativeURL);
    }


    private String normalise(final String urlToNormalise) {
        try {
            return new URI(urlToNormalise).toString();
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException("The url " + urlToNormalise + " is invalid.", ex);
        }
    }


    /**
     * Transfer the focus into the current element (set with a previous Find
     * method) which should be a frame or iframe
     * 
     * @example SwitchFrameToCurrentElement
     * @section Location
     * 
     */
    @Step("SwitchFrameToCurrentElement")
    public void switchFrameToCurrentElement() {

        final TargetLocator targetLocator = webDriver().switchTo();
        final WebDriver refocusedWebDriver = targetLocator.frame(webDriverContext().getCurrentElement());

        // yes I actually want to check these objects are the same!
        Assert.assertTrue(
                "Webdriver target locator has returned a different webdriver instance, some webdriver-substeps changes will be required to support this",
                refocusedWebDriver == webDriver());
    }


    /**
     * Performs a double click on the current element (set with a previous Find
     * method).
     * 
     * @example PerformDoubleClick
     * @section Clicks
     * 
     */
    @Step("PerformDoubleClick")
    public void doDoubleClick() {

        final Actions actions = new Actions(webDriver());

        actions.doubleClick(webDriverContext().getCurrentElement());

        actions.perform();
    }


    /**
     * Performs a context click (typically right click, unless this has been
     * changed by the user) on the current element.
     * 
     * @example PerformContextClick
     * @section Clicks
     * 
     */
    @Step("PerformContextClick")
    public void performContextClick() {

        if (webDriverContext().getDriverType() == DriverType.HTMLUNIT) {
            throw new WebDriverSubstepsException("PerformContextClick not supported in HTMLUnit");
        }

        final Actions actions = new Actions(webDriver());

        actions.contextClick(webDriverContext().getCurrentElement());

        actions.perform();
    }


    @Step("DismissAlert with message \"([^\"]*)\"")
    public void dismissAlertWithMessage(final String message) {

        // Get a handle to the open alert, prompt or confirmation

        final Alert alert = webDriverContext().getWebDriver().switchTo().alert();
        // this will throw a org.openqa.selenium.NoAlertPresentException if no
        // alert is present

        Assert.assertThat(alert.getText(), is(message));
        // And acknowledge the alert (equivalent to clicking "OK")
        alert.accept();
    }
}
