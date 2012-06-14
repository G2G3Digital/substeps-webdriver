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

import static com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown.getThreadLocalWebDriver;
import static com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown.getThreadLocalWebDriverContext;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.webdriver.util.WebDriverContext;

public class AssertionWebDriverSubStepImplementations extends
        AbstractWebDriverSubStepImplementations {

    private static final Logger logger = LoggerFactory
            .getLogger(AssertionWebDriverSubStepImplementations.class);


    public AssertionWebDriverSubStepImplementations() {
        super();
    }


    public AssertionWebDriverSubStepImplementations(
            final Supplier<WebDriverContext> webDriverContextSupplier) {
        super(webDriverContextSupplier);
    }


    /**
     * Check that the element with id has the text ....
     * 
     * @example AssertValue id msg_id text = "Hello World"
     * @section Assertions
     * @param id
     *            the id
     * @param expected
     *            the expected
     */
    @Step("AssertValue id ([^\"]*) text = \"([^\"]*)\"")
    public void assertElementText(final String id, final String expected) {
        logger.debug("Asserting element with id " + id + " has the text " + expected);
        final WebDriverWait wait = new WebDriverWait(webDriver(), 10);

        final Function<WebDriver, WebElement> condition2 = new Function<WebDriver, WebElement>() {
            public WebElement apply(final WebDriver driver) {
                WebElement rtn = null;
                final WebElement elem = driver.findElement(By.id(id));

                if (elem != null && elem.getText().equalsIgnoreCase(expected)) {
                    rtn = elem;
                }
                return rtn;
            }
        };

        // Implementations should wait until the condition evaluates to a value
        // that is neither null nor false.
        try {
            webDriverContext().setCurrentElement(null);
            final WebElement elem = wait.until(condition2);
            Assert.assertNotNull("expecting to find an element with id: " + id, elem);
            webDriverContext().setCurrentElement(elem);
        } catch (final TimeoutException e) {
            logger.debug("timed out waiting for id: " + id + " with text: " + expected
                    + " page src:\n" + webDriver().getPageSource());
            throw e;
        }
    }


    /**
     * From the current element, apply the xpath and check to see if any of the
     * children have the text ...
     * 
     * @example AssertChildElementsContainText xpath="li//a" text = "Log Out"
     * @section Assertions
     * @param xpath
     *            the xpath
     * @param text
     *            the text
     */
    @Step("AssertChildElementsContainText xpath=\"([^\"]*)\" text=\"([^\"]*)\"")
    public void assertChildElementsContainText(final String xpath, final String text) {
        logger.debug("Asserting chile element with xpath " + xpath + " has the text " + text);
        final List<WebElement> itemList = webDriverContext().getCurrentElement().findElements(
                By.xpath(xpath));
        boolean found = false;
        for (final WebElement item : itemList) {
            final String itemText = item.getText();
            if (itemText.startsWith(text)) {
                found = true;
                break;
            }
        }

        Assert.assertTrue("expecting child element to contain text: " + text, found);
    }


    /**
     * Check that the current element has the expected text value
     * 
     * @example AssertCurrentElement text="Hello World!"
     * @section Assertions
     * @param expected
     *            the expected text
     */
    @Step("AssertCurrentElement text=\"([^\"]*)\"")
    public void assertTextInCurrentElement(final String expected) {
        logger.debug("Asserting the current element has the text " + expected);
        Assert.assertThat(getThreadLocalWebDriverContext().getCurrentElement().getText(),
                is(expected));
    }


    /**
     * Check that the current element contains the specified text
     * 
     * @example AssertCurrentElement text contains "Hello world"
     * @section Assertions
     * @param expected
     *            the expected text
     */
    @Step("AssertCurrentElement text contains \"([^\"]*)\"")
    public void assertTextInCurrentElementContains(final String expected) {
        logger.debug("Asserting current element contains the text " + expected);
        Assert.assertThat(getThreadLocalWebDriverContext().getCurrentElement().getText(),
                containsString(expected));
    }


    /**
     * Check that the current element has the specified attribute and value
     * 
     * @example AssertCurrentElement attribute="class" value="icon32x32"
     * @section Assertions
     * 
     * @param attribute
     *            the attribute name
     * @param expected
     *            the expected value of the attribute
     */
    @Step("AssertCurrentElement attribute=\"([^\"]*)\" value=\"([^\"]*)\"")
    public void assertAttributeInCurrentElement(final String attribute, final String expected) {
        logger.debug("Asserting current element has the attribute " + attribute + "with value "
                + expected);
        final String attributeValue = getThreadLocalWebDriverContext().getCurrentElement()
                .getAttribute(attribute);
        Assert.assertNotNull("Expecting to find attribute " + attribute + " on current element",
                attributeValue);
        Assert.assertThat(attributeValue, is(expected));
    }


    /**
     * Check that any of the html tags have the specified text
     * 
     * @example AssertTagElementContainsText tag="ul" text="list item itext"
     * @section Assertions
     * @param tag
     *            the tag
     * @param text
     *            the text
     */
    @Step("AssertTagElementContainsText tag=\"([^\"]*)\" text=\"([^\"]*)\"")
    public void assertTagElementContainsText(final String tag, final String text) {
        logger.debug("Asserting tag element " + tag + " has text " + text);
        final List<WebElement> itemList = webDriver().findElements(By.tagName(tag));
        boolean found = false;
        for (final WebElement item : itemList) {
            final String itemText = item.getText();
            if (itemText.startsWith(text)) {
                found = true;
                break;
            }
        }

        Assert.assertTrue("expecting child element to contain text: " + text, found);
    }


    /**
     * Check that any of the html tags have the specified attribute name and
     * value
     * 
     * @example AssertTagElementContainsText tag="ul" attributeName="class"
     *          attributeValue="a_list"
     * @section Assertions
     * @param tag
     *            the tag
     * @param attributeName
     *            the attribute name
     * @param attributeValue
     *            the attribute value
     */
    @Step("AssertTagElementContainsAttribute tag=\"([^\"]*)\" attributeName=\"([^\"]*)\" attributeValue=\"([^\"]*)\"")
    public void assertTagElementContainsAttribute(final String tag, final String attributeName,
            final String attributeValue) {
        logger.debug("Asserting tag element " + tag + " has attribute " + attributeName
                + " with value " + attributeValue);
        final List<WebElement> itemList = webDriver().findElements(By.tagName(tag));
        boolean found = false;

        for (final WebElement item : itemList) {
            final String itemAttributeValue = item.getAttribute(attributeName);
            if (StringUtils.isNotBlank(itemAttributeValue)
                    && itemAttributeValue.contains(attributeValue)) {
                found = true;
                break;
            }
        }

        Assert.assertTrue("expecting child element to contain attribute: " + attributeName
                + " with value " + attributeValue, found);
    }


    /**
     * Check that the page title is ....
     * 
     * @example AssertPageTitle is "My Home Page"
     * @section Assertions
     * @param expectedTitle
     *            the expected title
     */
    @Step("AssertPageTitle is \"([^\"]*)\"")
    public void assertPageTitle(final String expectedTitle) {
        logger.debug("Asserting the page title is " + expectedTitle);
        Assert.assertEquals("unexpected page title", expectedTitle, webDriver().getTitle());
    }


    /**
     * Simple text search on page source
     * 
     * @example AssertPageSourceContains "foobar"
     * 
     * @param expected
     *            Some text you expect to appear on the page
     */
    @Step("AssertPageSourceContains \"([^\"]*)\"")
    public void pageSourceContains(final String expected) {
        logger.debug("Checking page source for expeted content [" + expected + "]");

        final String pageSource = getThreadLocalWebDriver().getPageSource();

        Assert.assertThat(pageSource, containsString(expected));
    }


    /**
     * Check that the current element, a checkbox is checked or not
     * 
     * @example AssertCheckBox checked=true/false
     * @section Assertions
     * @param checkedString
     *            whether the radio button is checked or not
     */
    @Step("AssertCheckBox checked=\"?([^\"]*)\"?")
    public void assertCheckBoxIsChecked(final String checkedString) {

        // check that the current element is not null and is a radio btn
        final WebElement currentElem = getThreadLocalWebDriverContext().getCurrentElement();

        assertElementIs(currentElem, "input", "checkbox");

        // check the state
        final boolean checked = Boolean.parseBoolean(checkedString.trim());
        if (checked) {
            Assert.assertTrue("expecting checkbox to be checked", currentElem.isSelected());
        } else {
            Assert.assertFalse("expecting checkbox not to be checked", currentElem.isSelected());
        }
    }


    /**
     * Check that the current element, a radio button, is checked or not
     * 
     * @example AssertRadioButton checked=true/false
     * @section Assertions
     * @param checkedString
     *            whether the radio button is checked or not
     */
    @Step("AssertRadioButton checked=\"?([^\"]*)\"?")
    public void assertRadioButtonIsChecked(final String checkedString) {

        // check that the current element is not null and is a radio btn
        final WebElement currentElem = getThreadLocalWebDriverContext().getCurrentElement();

        assertElementIs(currentElem, "input", "radio");

        // check the state
        final boolean checked = Boolean.parseBoolean(checkedString.trim());
        if (checked) {
            Assert.assertTrue("expecting radio button to be checked", currentElem.isSelected());
        } else {
            Assert.assertFalse("expecting radio button not to be checked", currentElem.isSelected());
        }
    }


    /**
     * Check that the current element has the specified attributes
     * 
     * @example AssertCurrentElement has
     *          attributes=[type="submit",value="Search"]
     * @section Assertions
     * @param attributeString
     *            comma separated list of attributes and quoted values
     */

    @Step("AssertCurrentElement has attributes=\\[(.*)\\]")
    public void assertCurrentElementHasAttributes(final String attributeString) {

        final WebElement currentElem = getThreadLocalWebDriverContext().getCurrentElement();

        final Map<String, String> expectedAttributes = convertToMap(attributeString);

        Assert.assertTrue("element doesn't have expected attributes: " + attributeString,
                elementHasExpectedAttributes(currentElem, expectedAttributes));

    }


    /**
     * Utility method to check that an element is of a particular tag and type
     * 
     * @param elem
     * @param tag
     * @param type
     */
    public static void assertElementIs(final WebElement elem, final String tag, final String type) {

        Assert.assertNotNull("expecting an element", elem);
        Assert.assertTrue("unexpected tag", elem.getTagName() != null
                && elem.getTagName().compareToIgnoreCase(tag) == 0);
        Assert.assertTrue("unexpected type", elem.getAttribute("type") != null
                && elem.getAttribute("type").compareToIgnoreCase(type) == 0);
    }


    /**
     * Grab the text of an element (identified by id) and save it for the
     * duration of this scenario
     * 
     * @example RememberForScenario textFrom "projectName" as "savedProjectName"
     * 
     * @param elementId
     *            The ID of the HTML element
     * @param nameToSaveAs
     *            The variable name to save the text as for later retrieval
     */
    @Step("RememberForScenario textFrom \"([^\"]*)\" as \"([^\"]*)\"")
    public void rememberForScenario(final String elementId, final String nameToSaveAs) {
        final FinderWebDriverSubStepImplementations finder = new FinderWebDriverSubStepImplementations();

        final WebElement element = finder.findById(elementId);
        final String text = element.getText();
        ExecutionContext.put(Scope.SCENARIO, nameToSaveAs, text);
    }


    /**
     * Compare the text of an element (identified by ID) to a value previously
     * remembered
     * 
     * @example AssertDifferent rememberedValue "savedProjectName"
     *          compareToElement "projectName"
     * 
     * @param elementId
     *            The ID of the HTML element
     * @param nameToSaveAs
     *            The variable name to save the text as for later retrieval
     */
    @Step("AssertDifferent rememberedValue \"([^\"]*)\" compareToElement \"([^\"]*)\"")
    public void assertDifferent(final String rememberedValueName, final String elementId) {

        final FinderWebDriverSubStepImplementations finder = new FinderWebDriverSubStepImplementations();
        final WebElement element = finder.findById(elementId);
        final String text = element.getText();

        Object retrievedValue = null;
        for (final Scope scope : Scope.values()) {
            final Object valueFromScope = ExecutionContext.get(scope, rememberedValueName);
            if (valueFromScope != null) {
                retrievedValue = valueFromScope;
            }
        }

        Assert.assertFalse(
                "The remembered value was different to the text of the element compared against",
                retrievedValue.equals(text));
    }


    /**
     * Compare the text of an element (identified by ID) to a value previously
     * remembered - assert they're the same
     * 
     * @example AssertSame rememberedValue "savedProjectName" compareToElement
     *          "projectName"
     * 
     * @param elementId
     *            The ID of the HTML element
     * @param nameToSaveAs
     *            The variable name to save the text as for later retrieval
     */
    @Step("AssertSame rememberedValue \"([^\"]*)\" compareToElement \"([^\"]*)\"")
    public void assertSame(final String rememberedValueName, final String elementId) {

        final FinderWebDriverSubStepImplementations finder = new FinderWebDriverSubStepImplementations();

        final WebElement element = finder.findById(elementId);
        final String text = element.getText();

        Object retrievedValue = null;
        for (final Scope scope : Scope.values()) {
            final Object valueFromScope = ExecutionContext.get(scope, rememberedValueName);
            if (valueFromScope != null) {
                retrievedValue = valueFromScope;
            }
        }

        Assert.assertEquals(
                "The remembered value was different to the text of the element compared against",
                retrievedValue.toString(), text);
    }


    /**
     * Asserts that an element (identified by ID) eventually gets some text
     * inserted into it (by JavaScript, probably)
     * 
     * @example AssertEventuallyNotEmpty mySpan
     * @param elementId
     *            HTML ID of element
     */
    @Step("AssertEventuallyNotEmpty id=\"([^\"]*)\"")
    public void assertEventuallyNotEmpty(final String elementId) {
        waitForElementToContainText(By.id(elementId));
    }


    /**
     * Asserts that an element (identified by ID) eventually gets some specific
     * text inserted into it (by JavaScript, probably)
     * 
     * @example AssertEventuallyContains mySpan
     * @param elementId
     *            HTML ID of element
     */
    @Step("AssertEventuallyContains ([^\"]*) \"([^\"]*)\"")
    public void assertEventuallyContains(final String elementId, final String text) {
        waitForElementToContain(By.id(elementId), text);
    }


    /**
     * Assert that the specified text is not found within the page
     * 
     * @param id
     */
    @Step("AssertNotPresent text=\"([^\"]*)\"")
    public void assertNotPresent(final String text) {
        final String pageSource = getThreadLocalWebDriver().getPageSource();
        Assert.assertThat(pageSource, not(containsString(text)));
    }


    /**
     * Wait for an element to contain some (any) text
     * 
     * @example
     * @param by
     *            WebDriver By object that identifies the element
     * @return the web element
     */
    public WebElement waitForElementToContainText(final By by) {

        final WebDriverWait wait = new WebDriverWait(getThreadLocalWebDriver(), 10);
        final Function<WebDriver, WebElement> condition2 = new Function<WebDriver, WebElement>() {
            public WebElement apply(final WebDriver driver) {
                final WebElement rtn = driver.findElement(by);

                final String potentialVal = rtn.getText();

                if (potentialVal != null) {
                    return rtn;
                }

                return null;
            }
        };

        return wait.until(condition2);
    }


    /**
     * Wait for an element to contain some specific text
     * 
     * 
     * @param by
     *            WebDriver By object that identifies the element
     * @return the web element
     */
    public WebElement waitForElementToContain(final By by, final String expectedText) {

        final WebDriverWait wait = new WebDriverWait(getThreadLocalWebDriver(), 10);
        final Function<WebDriver, WebElement> condition2 = new Function<WebDriver, WebElement>() {
            public WebElement apply(final WebDriver driver) {
                final WebElement rtn = driver.findElement(by);

                final String potentialVal = rtn.getText();

                if (expectedText.contains(potentialVal)) {
                    return rtn;
                }

                return null;
            }
        };

        return wait.until(condition2);
    }
}
