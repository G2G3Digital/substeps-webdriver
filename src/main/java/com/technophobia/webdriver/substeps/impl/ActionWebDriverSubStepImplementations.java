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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.webdriver.substeps.runner.Condition;
import com.technophobia.webdriver.substeps.runner.WebdriverSubstepsConfiguration;
import com.technophobia.webdriver.util.WebDriverContext;

public class ActionWebDriverSubStepImplementations extends AbstractWebDriverSubStepImplementations {

    private static final Logger logger = LoggerFactory
            .getLogger(ActionWebDriverSubStepImplementations.class);

    private final FinderWebDriverSubStepImplementations locator;


    public ActionWebDriverSubStepImplementations() {
        locator = new FinderWebDriverSubStepImplementations();
    }


    public ActionWebDriverSubStepImplementations(
            final FinderWebDriverSubStepImplementations locator,
            final Supplier<WebDriverContext> webDriverContextSupplier) {
        super(webDriverContextSupplier);
        this.locator = locator;
    }


    /**
     * Navigate to a url using the base url specified in the properties
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
        webDriver().get(normaliseURL(url));
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
        locator.findById(id);
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
        final WebElement elem = locator.findElementWithText("button", buttonText.trim());
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


    // could use a pattern TODO - just use ends with for now
    /**
     * Wait for element with attribute.
     * 
     * @example
     * @param by
     *            the by
     * @param attribute
     *            the attribute
     * @param value
     *            the value
     * @return the web element
     */
    public WebElement waitForElementWithAttribute(final By by, final String attribute,
            final String value) {
        logger.debug("Waiting for element " + by + " with attribute " + attribute + " with value "
                + value);
        final WebDriverWait wait = new WebDriverWait(webDriver(), 10);
        final Function<WebDriver, WebElement> condition2 = new AttributeEndsWithFunction(by,
                attribute, value);

        // Implementations should wait until the condition evaluates to a value
        // that is neither null nor false.
        return wait.until(condition2);

    }

    private static class AttributeEndsWithFunction implements Function<WebDriver, WebElement> {

        private final By by;
        private final String attribute;
        private final String value;


        public AttributeEndsWithFunction(final By by, final String attribute, final String value) {
            this.by = by;
            this.attribute = attribute;
            this.value = value;
        }


        public WebElement apply(final WebDriver driver) {
            final WebElement rtn = driver.findElement(by);

            final String potentialVal = rtn.getAttribute(attribute);

            if (potentialVal != null && potentialVal.endsWith(value)) {
                return rtn;
            }

            return null;
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
                logger.debug(String.format("wait for page. Expected='%s', actual='%s'",
                        expectedTitle, pageTitle));
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
}
