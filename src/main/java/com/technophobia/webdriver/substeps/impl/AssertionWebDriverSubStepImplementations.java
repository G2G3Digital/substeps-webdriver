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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.substeps.model.SubSteps.StepImplementations;
import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.substeps.step.StepImplementationUtils;
import com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown;
import com.technophobia.webdriver.util.WebDriverContext;
import com.technophobia.webdriver.util.WebDriverSubstepsBy;

@StepImplementations(requiredInitialisationClasses = DefaultExecutionSetupTearDown.class)
public class AssertionWebDriverSubStepImplementations extends AbstractWebDriverSubStepImplementations {

    private static final Logger logger = LoggerFactory.getLogger(AssertionWebDriverSubStepImplementations.class);

    private final FinderWebDriverSubStepImplementations finder = new FinderWebDriverSubStepImplementations();


    public AssertionWebDriverSubStepImplementations() {
        super();
    }


    public AssertionWebDriverSubStepImplementations(final Supplier<WebDriverContext> webDriverContextSupplier) {
        super(webDriverContextSupplier);
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
        Assert.assertThat(getThreadLocalWebDriverContext().getCurrentElement().getText(), is(expected));
    }


    /**
     * Check that the current input field has the expected text value
     * 
     * @example AssertCurrentInput value="Hello World!"
     * @section Assertions
     * @param expected
     *            the expected value
     */
    @Step("AssertCurrentInput value=\"([^\"]*)\"")
    public void assertValueInCurrentInput(final String expected) {
        logger.debug("Asserting the current input has the value" + expected);
        Assert.assertThat(getThreadLocalWebDriverContext().getCurrentElement()
                .getAttribute("value"), is(expected));
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
        Assert.assertThat(getThreadLocalWebDriverContext().getCurrentElement().getText(), containsString(expected));
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
        logger.debug("Asserting current element has the attribute " + attribute + "with value " + expected);
        final String attributeValue = getThreadLocalWebDriverContext().getCurrentElement().getAttribute(attribute);
        Assert.assertNotNull("Expecting to find attribute " + attribute + " on current element", attributeValue);
        Assert.assertThat(attributeValue, is(expected));
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
        logger.debug("Asserting tag element " + tag + " has attribute " + attributeName + " with value "
                + attributeValue);
        final List<WebElement> itemList = webDriver().findElements(By.tagName(tag));
        boolean found = false;

        for (final WebElement item : itemList) {
            final String itemAttributeValue = item.getAttribute(attributeName);
            if (StringUtils.isNotBlank(itemAttributeValue) && itemAttributeValue.contains(attributeValue)) {
                found = true;
                break;
            }
        }

        Assert.assertTrue("expecting child element to contain attribute: " + attributeName + " with value "
                + attributeValue, found);
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
     *            the text you expect to find in the page source - this can
     *            include quotes.
     */
    @Step("AssertPageSourceContains \"(.*)\"$")
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

        final Map<String, String> expectedAttributes = StepImplementationUtils.convertToMap(attributeString);

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

        if (type != null) {
            Assert.assertTrue("unexpected type", elem.getAttribute("type") != null
                    && elem.getAttribute("type").compareToIgnoreCase(type) == 0);
        }
    }


    /**
     * Utility method to check that an element is of a particular tag
     * 
     * @param elem
     * @param tag
     * @param type
     */
    public static void assertElementIs(final WebElement elem, final String tag) {
        assertElementIs(elem, tag, null);
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

        final WebElement element = this.finder.findById(elementId);
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

        final WebElement element = this.finder.findById(elementId);
        final String text = element.getText();

        Object retrievedValue = null;
        for (final Scope scope : Scope.values()) {
            final Object valueFromScope = ExecutionContext.get(scope, rememberedValueName);
            if (valueFromScope != null) {
                retrievedValue = valueFromScope;
            }
        }

        Assert.assertFalse("The remembered value was different to the text of the element compared against",
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

        final WebElement element = this.finder.findById(elementId);
        final String text = element.getText();

        Object retrievedValue = null;
        for (final Scope scope : Scope.values()) {
            final Object valueFromScope = ExecutionContext.get(scope, rememberedValueName);
            if (valueFromScope != null) {
                retrievedValue = valueFromScope;
            }
        }

        Assert.assertEquals("The remembered value was different to the text of the element compared against",
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
        WebElement webElement = waitForElementToContainSomeText(By.id(elementId));
        Assert.assertNotNull(String.format("Expected to find a non-empty element with 'id=%s' but didn't.", elementId), webElement);
    }


    /**
     * Asserts that an element (identified by ID) eventually gets some specific
     * text inserted into it (by JavaScript, probably)
     * 
     * @example AssertEventuallyContains mySpanId "text I eventually expect"
     * @section Assertions
     * @param elementId
     *            HTML ID of element
     * @param text
     *            the expected text
     */
    @Step("AssertEventuallyContains ([^\"]*) \"([^\"]*)\"")
    public void assertEventuallyContains(final String elementId, final String text) {
        WebElement webElement = webDriverContext().waitForElement(WebDriverSubstepsBy.ByIdContainingText(elementId, text));
        Assert.assertNotNull(String.format("Expected to find a element with 'id=%s' containing text '%s' but didn't.", elementId, text), webElement);
    }


    /**
     * Assert that the specified text is not found within the page
     * 
     * @example AssertNotPresent text="undesirable text"
     * @section Assertions
     * @param text
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
    public WebElement waitForElementToContainSomeText(final By by) {

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

}
