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
import static org.hamcrest.Matchers.greaterThan;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.substeps.model.SubSteps.StepImplementations;
import com.technophobia.substeps.model.SubSteps.StepParameter;
import com.technophobia.substeps.model.parameter.IntegerConverter;
import com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown;
import com.technophobia.webdriver.util.WebDriverContext;
import com.technophobia.webdriver.util.WebDriverSubstepsBy;
import com.technophobia.webdriver.util.WebElementPredicate;

@StepImplementations(requiredInitialisationClasses = DefaultExecutionSetupTearDown.class)
public class FinderWebDriverSubStepImplementations extends AbstractWebDriverSubStepImplementations {

    private static final Logger logger = LoggerFactory.getLogger(FinderWebDriverSubStepImplementations.class);


    public FinderWebDriverSubStepImplementations() {
        super();
    }


    public FinderWebDriverSubStepImplementations(final Supplier<WebDriverContext> webDriverContextSupplier) {
        super(webDriverContextSupplier);
    }


    /**
     * Find an element by it's ID
     * 
     * @example FindById username
     * @section Location
     * 
     * @param id
     *            the id
     * @return the web element
     */
    @Step("FindById ([^\"]*)")
    public WebElement findById(final String id) {

        logger.debug("Looking for item with id " + id);
        webDriverContext().setCurrentElement(null);

        final WebElement elem = webDriverContext().waitForElement(By.id(id));
        Assert.assertNotNull("expecting an element with id " + id, elem);

        webDriverContext().setCurrentElement(elem);
        return elem;
    }


    /**
     * Find an element by it's ID with the specified timeout
     * 
     * @example FindByIdTimeout username timeout = 15 secs
     * @section Location
     * 
     * @param id
     *            the id
     * @param timeout
     *            the timeout
     * @return the web element
     */
    @Step("FindByIdTimeout ([^\"]*) timeout = ([^\"]*) secs")
    public WebElement findById(final String id, final String timeout) {
        logger.debug("Looking for item with id " + id + "within " + timeout + " seconds");
        final long t = Long.parseLong(timeout);

        webDriverContext().setCurrentElement(null);
        final WebElement elem = webDriverContext().waitForElement(By.id(id), t);
        Assert.assertNotNull("expecting an element with id " + id, elem);
        webDriverContext().setCurrentElement(elem);
        return elem;
    }


    /**
     * Find an id by xpath
     * 
     * @example FindByXpath
     * @section Location
     * 
     * @param xpath
     *            the xpath
     */
    @Step("FindByXpath ([^\"]*)")
    public void findByXpath(final String xpath) {
        logger.debug("Looking for item with xpath " + xpath);
        webDriverContext().setCurrentElement(null);
        final WebElement elem = webDriverContext().waitForElement(By.xpath(xpath));
        Assert.assertNotNull("expecting an element with xpath " + xpath, elem);
        webDriverContext().setCurrentElement(elem);
    }


    /**
     * Find an element using the name attribute of the element
     * 
     * @example FindByName "named field"
     * @section Location
     * 
     * @param name
     *            the name
     * @return the web element
     */
    @Step("FindByName \"?([^\"]*)\"?")
    public WebElement findByName(final String name) {
        logger.debug("Looking for item with name " + name);
        webDriverContext().setCurrentElement(null);
        final WebElement elem = webDriverContext().waitForElement(By.name(name));
        Assert.assertNotNull("expecting an element with name " + name, elem);
        webDriverContext().setCurrentElement(elem);
        return elem;
    }


    /**
     * Find element by predicate.
     * 
     * @example
     * @param predicate
     *            the predicate
     * @return the web element
     */
    public WebElement findElementByPredicate(final WebElementPredicate predicate) {
        logger.debug("About to find element by predicate " + predicate);
        WebElement rtn = null;

        final List<WebElement> elems = webDriver().findElements(By.tagName(predicate.getTagname()));

        for (final WebElement e : elems) {

            if (predicate.apply(e)) {
                rtn = e;
                break;
            }
        }
        if (rtn == null) {
            throw new IllegalStateException("Failed to find element by predicate: " + predicate.getDescription());
        }

        return rtn;
    }


    /**
     * Finds an element on the page with the specified tag and text
     * 
     * @example FindFirstTagElementContainingText tag="ul"
     *          text="list item itext"
     * @section Location
     * @param tag
     *            the tag
     * @param text
     *            the text
     */
    @Step("FindFirstTagElementContainingText tag=\"([^\"]*)\" text=\"([^\"]*)\"")
    public void findFirstTagElementContainingText(final String tag, final String text) {
        logger.debug("Finding tag element " + tag + "and asserting has text " + text);

        webDriverContext().setCurrentElement(null);

        final By by = WebDriverSubstepsBy.ByTagContainingText(tag, text);

        final WebElement elem = MatchingElementResultHandler.AtLeastOneElement.processResults(webDriverContext(), by,
                "expecting at least one child element to contain text: " + text);

        webDriverContext().setCurrentElement(elem);
    }


    /**
     * Finds an element that is a child of the current element using the name
     * attribute, another Find method should be used first
     * 
     * @example FindChild ByName name="child name"
     * @section Location
     * 
     * @param name
     *            the name
     * @return the web element
     */
    @Step("FindChild ByName name=\"?([^\"]*)\"?")
    public WebElement findChildByName(final String name) {
        logger.debug("Looking for child with name " + name);
        Assert.assertNotNull("expecting a current element", webDriverContext().getCurrentElement());
        final WebElement elem = webDriverContext().getCurrentElement().findElement(By.name(name));

        Assert.assertNotNull("expecting an element with name " + name, elem);
        webDriverContext().setCurrentElement(elem);
        return elem;
    }


    /**
     * Finds an element that is a child of the current element using the tag
     * name and specified attributes, another Find method should be used first
     * 
     * @example FindChild ByTagAndAttributes tag="input"
     *          attributes=[type="submit",value="Search"]
     * @section Location
     * 
     * @param tag
     *            the tag
     * @param attributeString
     *            the attribute string
     * @return the web element
     */
    @Step("FindChild ByTagAndAttributes tag=\"?([^\"]*)\"? attributes=\\[(.*)\\]")
    public WebElement findChildByTagAndAttributes(final String tag, final String attributeString) {
        logger.debug("Looking for child with tag " + tag + " and attributes " + attributeString);

        return findChildByTagAndAttributes(tag, attributeString, MatchingElementResultHandler.ExactlyOneElement);
    }


    private WebElement findChildByTagAndAttributes(final String tag, final String attributeString,
            final MatchingElementResultHandler resultHandler) {

        Assert.assertNotNull("expecting a current element", webDriverContext().getCurrentElement());

        final WebElement currentElement = webDriverContext().getCurrentElement();

        final By byTagAndAttributes = WebDriverSubstepsBy.ByTagAndAttributes(tag, attributeString);

        final By byCurrentElement = WebDriverSubstepsBy.ByCurrentWebElement(currentElement);

        final By chained = new ByChained(byCurrentElement, byTagAndAttributes);

        final String msg = "failed to locate a child element with tag: " + tag + " and attributes: " + attributeString;

        final WebElement elem = resultHandler.processResults(webDriverContext(), chained, msg);

        webDriverContext().setCurrentElement(elem);

        return elem;
    }


    /**
     * Finds the first child element of the 'current' element using the tag name
     * and specified attributes, another Find method should be used first
     * 
     * @example FindFirstChild ByTagAndAttributes tag="input"
     *          attributes=[type="submit",value="Search"]
     * @section Location
     * 
     * @param tag
     *            the tag
     * @param attributeString
     *            the attribute string
     * @return the web element
     */
    @Step("FindFirstChild ByTagAndAttributes tag=\"?([^\"]*)\"? attributes=\\[(.*)\\]")
    public WebElement findFirstChildByTagAndAttributes(final String tag, final String attributeString) {

        logger.debug("Looking for first child with tag " + tag + " and attributes " + attributeString);

        return findChildByTagAndAttributes(tag, attributeString, MatchingElementResultHandler.AtLeastOneElement);

    }


    /**
     * Finds a checkbox that is a child of the specified tag, that contains the
     * specified text; eg.
     * 
     * <pre>
     * <label>
     *  <input type="checkbox" name="checkbox_name" value="yeah"/>a checkbox <span>label</span>
     * </label>
     * </pre>
     * 
     * @example FindCheckbox inside tag="label" with label="a checkbox label>"
     * 
     * @section Location
     * 
     * @param tag
     *            the tag
     * @param label
     *            the checkbox label
     * @return the web element
     */
    @Step("FindCheckbox inside tag=\"?([^\"]*)\"? with label=\"([^\"]*)\"")
    public WebElement findCheckBox(final String tag, final String label) {

        return findInputInsideTag(label, tag, "checkbox");

    }


    // todo variant that also has attributes for the tag

    /**
     * Finds a radiobutton that is a child of the specified tag, that contains
     * the specified text; eg.
     * 
     * <pre>
     * <label>
     *  <input type="radio" name="radio_name" value="yeah"/>a radio <span>label</span>
     * </label>
     * </pre>
     * 
     * @example FindRadioButton inside tag="label" with label="a radio label>"
     * 
     * @section Location
     * 
     * @param tag
     *            the tag
     * @param label
     *            the radio button label
     * @return the web element
     */
    @Step("FindRadioButton inside tag=\"?([^\"]*)\"? with label=\"([^\"]*)\"")
    public WebElement findRadioButton(final String tag, final String label) {

        return findInputInsideTag(label, tag, "radio");
    }


    /**
     * @param label
     * @param tag
     * @param inputType
     * @return
     */
    public WebElement findInputInsideTag(final String label, final String tag, final String inputType) {
        WebElement elem = null;
        webDriverContext().setCurrentElement(null);

        // TODO - turn this into a function

        // look for elems with the right tag
        final List<WebElement> tagElems = getThreadLocalWebDriver().findElements(By.tagName(tag));

        checkElements("expecting some elements of tag: " + tag, tagElems);

        List<WebElement> matchingElems = null;

        for (final WebElement tagElem : tagElems) {

            // does this tag contain the right text ?
            if (label.compareTo(tagElem.getText()) == 0) {
                // yes
                // is there a radio button inside ?

                final List<WebElement> inputElements = tagElem.findElements(By.tagName("input"));

                if (inputElements != null && !inputElements.isEmpty()) {
                    // are they radio buttons ?

                    for (final WebElement inputElement : inputElements) {
                        final String type = inputElement.getAttribute("type");

                        if (type != null && type.compareToIgnoreCase(inputType) == 0) {
                            // bingo
                            if (matchingElems == null) {
                                matchingElems = new ArrayList<WebElement>();
                            }
                            matchingElems.add(inputElement);
                        }
                    }
                }
            }
        }

        elem = checkForOneMatchingElement("expecting an input of type " + inputType + " inside tag [" + tag
                + "] with label [" + label + "]", matchingElems);

        webDriverContext().setCurrentElement(elem);
        return elem;
    }


    /**
     * @param tag
     * @param tagElems
     */
    public void checkElements(final String msg, final List<WebElement> tagElems) {
        Assert.assertNotNull(msg, tagElems);
        Assert.assertTrue(msg, !tagElems.isEmpty());
    }


    /**
     * Find an element by tag name and a set of attributes and corresponding
     * values
     * 
     * @param tag
     *            the tag
     * @param attributeString
     *            the attribute string
     * @return the web element
     * @example FindByTagAndAttributes tag="input"
     *          attributes=[type="submit",value="Search"]
     * @section Location
     */
    @Step("FindByTagAndAttributes tag=\"?([^\"]*)\"? attributes=\\[(.*)\\]")
    public WebElement findByTagAndAttributes(final String tag, final String attributeString) {
        logger.debug("Looking for item with tag " + tag + " and attributes " + attributeString);

        return findByTagAndAttributes(tag, attributeString, MatchingElementResultHandler.ExactlyOneElement);
    }


    private WebElement findByTagAndAttributes(final String tag, final String attributeString,
            final MatchingElementResultHandler handler) {

        webDriverContext().setCurrentElement(null);

        WebElement rtn = null;

        final By by = WebDriverSubstepsBy.ByTagAndAttributes(tag, attributeString);

        final String msg = "failed to locate an element with tag: " + tag + " and attributes: " + attributeString;

        rtn = handler.processResults(webDriverContext(), by, msg);

        webDriverContext().setCurrentElement(rtn);

        return rtn;
    }


    /**
     * Finds the first element by tag name and a set of attributes and
     * corresponding values
     * 
     * @param tag
     *            the tag
     * @param attributeString
     *            the attribute string
     * @return the web element
     * @example FindFirstByTagAndAttributes tag="input"
     *          attributes=[type="submit",value="Search"]
     * @section Location
     */
    @Step("FindFirstByTagAndAttributes tag=\"?([^\"]*)\"? attributes=\\[(.*)\\]")
    public WebElement findFirstByTagAndAttributes(final String tag, final String attributeString) {
        logger.debug("Looking for first item with tag " + tag + " and attributes " + attributeString);

        return findByTagAndAttributes(tag, attributeString, MatchingElementResultHandler.AtLeastOneElement);
    }


    /**
     * Finds the n th element by tag name and a set of attributes and
     * corresponding values
     * 
     * @param n
     *            the nth matching element we wish to find
     * @param tag
     *            the tag
     * @param attributeString
     *            the attribute string
     * @return the web element
     * @example FindNthByTagAndAttributes n=2 tag="input"
     *          attributes=[type="submit",value="Search"]
     * @section Location
     */
    @Step("FindNthByTagAndAttributes n=\"?([^\"]*)\"? tag=\"?([^\"]*)\"? attributes=\\[(.*)\\]")
    public WebElement findNthByTagAndAttributes(@StepParameter(converter = IntegerConverter.class) final Integer nth,
            final String tag, final String attributeString) {
        logger.debug("Looking for nth item with tag " + tag + " and attributes " + attributeString);

        webDriverContext().setCurrentElement(null);

        WebElement rtn = null;

        final By by = WebDriverSubstepsBy.NthByTagAndAttributes(tag, attributeString, nth);

        final String msg = "failed to locate the " + nth.intValue() + "th element with tag: " + tag
                + " and attributes: " + attributeString;

        final MatchingElementResultHandler.NthElement handler = new MatchingElementResultHandler.NthElement(nth);

        rtn = handler.processResults(webDriverContext(), by, msg);

        webDriverContext().setCurrentElement(rtn);

        return rtn;
    }


    /**
     * Finds an element by tag name and a set of attributes and corresponding
     * values, that has a child tag element of the specified type and having the
     * specified text
     * 
     * @param tag
     *            the parent tag
     * @param attributeString
     *            the parent attribute string
     * @param childTag
     *            the child tag
     * @param childText
     *            the child's text
     * 
     * @return the web element
     * @example FindParentByTagAndAttributes tag="table"
     *          attributes=[class="mytable"] ThatHasChild tag="caption"
     *          text="wahoo"
     * @section Location
     */
    @Step("FindParentByTagAndAttributes tag=\"?([^\"]*)\"? attributes=\\[(.*)\\] ThatHasChild tag=\"?([^\"]*)\"? text=\"([^\"]*)\"")
    public WebElement findParentByTagAndAttributesThatHasChildWithTagAndText(final String tag,
            final String attributeString, final String childTag, final String childText) {

        webDriverContext().setCurrentElement(null);

        WebElement rtn = null;

        final By by = WebDriverSubstepsBy.ByTagAndAttributes(tag, attributeString);

        final By childBy = WebDriverSubstepsBy.ByTagAndWithText(childTag, childText);

        final String assertionMessage = "Failed to locate a parent element with tag: " + tag + " and attributes: "
                + attributeString + " with a child element of tag: " + childTag + " with text: " + childText;

        final String findParentAssertionMessage = "failed to locate an element with tag: " + tag + " and attributes: "
                + attributeString;
        final String multipleChildrenMessage = "More than one child element found for parent with tag: " + childTag
                + " and text: " + childText;

        rtn = findParentByWithChildBy(by, childBy, assertionMessage, findParentAssertionMessage,
                multipleChildrenMessage);
        return rtn;
    }


    /**
     * @param by
     * @param childBy
     * @param assertionMessage
     * @param findParentAssertionMessage
     * @param multipleChildrenMessage
     * @return
     */
    private WebElement findParentByWithChildBy(final By by, final By childBy, final String assertionMessage,
            final String findParentAssertionMessage, final String multipleChildrenMessage) {
        WebElement rtn;
        List<WebElement> matchingElements = null;

        final List<WebElement> candidateParentElements = webDriver().findElements(by);

        Assert.assertNotNull(findParentAssertionMessage, candidateParentElements);
        Assert.assertFalse(findParentAssertionMessage, candidateParentElements.isEmpty());

        for (final WebElement parent : candidateParentElements) {

            final List<WebElement> children = parent.findElements(childBy);

            // do we care if there are more than one matching child ? lets go
            // with no..
            if (!children.isEmpty()) {

                if (matchingElements == null) {
                    matchingElements = new ArrayList<WebElement>();
                }
                matchingElements.add(parent);
                if (children.size() > 1) {
                    logger.info(multipleChildrenMessage);
                }
            }
        }
        rtn = checkForOneMatchingElement(assertionMessage, matchingElements);

        webDriverContext().setCurrentElement(rtn);
        return rtn;
    }


    /**
     * Finds an element by tag name and a set of attributes and corresponding
     * values, that has a child tag element of the specified type that has the
     * specified attributes..
     * 
     * @param tag
     *            the parent tag
     * @param attributeString
     *            the parent attribute string
     * @param childTag
     *            the child tag
     * @param childAttributeString
     *            the child's attribute string
     * 
     * @return the web element
     * @example FindParentByTagAndAttributes tag="table"
     *          attributes=[class="mytable"] ThatHasChild tag="caption"
     *          attributes=[class="childClass"]
     * @section Location
     */
    @Step("FindParentByTagAndAttributes tag=\"?([^\"]*)\"? attributes=\\[(.*)\\] ThatHasChild tag=\"?([^\"]*)\"? attributes=\\[(.*)\\]")
    public WebElement findParentByTagAndAttributesThatHasChildWithTagAndAttributes(final String tag,
            final String attributeString, final String childTag, final String childAttributeString) {

        webDriverContext().setCurrentElement(null);

        WebElement rtn = null;

        final By by = WebDriverSubstepsBy.ByTagAndAttributes(tag, attributeString);

        final By childBy = WebDriverSubstepsBy.ByTagAndAttributes(childTag, childAttributeString);

        final String assertionMessage = "Failed to locate a parent element with tag: " + tag + " and attributes: "
                + attributeString + " with a child element of tag: " + childTag + " and attributes: "
                + childAttributeString;

        final String findParentAssertionMessage = "failed to locate an element with tag: " + tag + " and attributes: "
                + attributeString;
        final String multipleChildrenMessage = "More than one child element found for parent with tag: " + childTag
                + " and and attributes: " + childAttributeString;

        rtn = findParentByWithChildBy(by, childBy, assertionMessage, findParentAssertionMessage,
                multipleChildrenMessage);
        return rtn;
    }

    /**
     * Enum encapsulating logic around how to process multiple results found as
     * result of a By. Enum instances should specialise the checking of multiple
     * results
     * 
     * @author imoore
     * 
     */
    private static abstract class MatchingElementResultHandler {

        // this class started off as an Enum before the NthElement handler, left
        // a bit of sugar in to maintain the existing code
        static final AtLeastOneElementImpl AtLeastOneElement = new AtLeastOneElementImpl();
        static final ExactlyOneElementImpl ExactlyOneElement = new ExactlyOneElementImpl();

        /**
         * This class will check that only one matching element is found.
         * finding multiple elements will result in failure
         * 
         */
        private static class ExactlyOneElementImpl extends MatchingElementResultHandler {

            @Override
            WebElement checkMatchingElements(final List<WebElement> matchingElems, final String msg) {

                return checkForOneMatchingElement(msg, matchingElems);
            }
        }

        /**
         * This class will look for one matching element and disregard others.
         * If no elements are found a failure will be reported. The first
         * element out of multiple will be returned.
         */
        private static class AtLeastOneElementImpl extends MatchingElementResultHandler {

            @Override
            WebElement checkMatchingElements(final List<WebElement> matchingElems, final String msg) {

                WebElement rtn = null;

                if (matchingElems != null && !matchingElems.isEmpty()) {
                    rtn = matchingElems.get(0);
                }

                Assert.assertNotNull(msg, rtn);
                return rtn;
            }
        }

        /**
         * This class will look for at least n matching elements and return the
         * nth
         * 
         */
        private static class NthElement extends MatchingElementResultHandler {

            int idx;


            NthElement(final int n) {
                Assert.assertThat(n, greaterThan(0));
                this.idx = n - 1;
            }


            @Override
            WebElement checkMatchingElements(final List<WebElement> matchingElems, final String msg) {
                WebElement rtn = null;

                if (matchingElems != null && !matchingElems.isEmpty() && matchingElems.size() > this.idx) {
                    rtn = matchingElems.get(this.idx);
                }

                Assert.assertNotNull(msg, rtn);
                return rtn;
            }
        }


        abstract WebElement checkMatchingElements(List<WebElement> matchingElems, final String msg);


        WebElement processResults(final WebDriverContext ctx, final By by, final String msg) {
            WebElement rtn = null;

            List<WebElement> matchingElems = ctx.getWebDriver().findElements(by);
            
            List<WebElement> results = new ArrayList<WebElement>();

            if (matchingElems == null || matchingElems.isEmpty()) {

                // wait for at least one - if we need to wait, we will only find
                // one
                final WebElement elementWaitedFor = ctx.waitForElement(by);
             
                results.add(elementWaitedFor);
            }
            else
            {
            	results.addAll(matchingElems);
            }

            rtn = checkMatchingElements(results, msg);

            return rtn;
        }

        //
        // MatchingElementResultHandler(final int n) {
        // this.idx = n - 1;
        // }

    }


    /**
     * Checks that a list of WebElements only contains one (not empty and not
     * too many).
     * 
     * @param msg
     * @param matchingElems
     * @return
     */
    public static WebElement checkForOneMatchingElement(final String msg, final List<WebElement> matchingElems) {
        WebElement rtn = null;
        if (matchingElems != null && matchingElems.size() > 1) {
            // ambiguous
            Assert.fail("Found too many elements that meet this criteria");
            // TODO - need some more debug here
        }

        else if (matchingElems != null) {
            rtn = matchingElems.get(0);
        }

        Assert.assertNotNull(msg, rtn);
        return rtn;
    }


    /**
     * Gets the element with text.
     * 
     * @param type
     *            the type
     * @param text
     *            the text
     * @return the element with text
     */
    public WebElement findElementWithText(final String type, final String text) {
        WebElement elem = null;
        final List<WebElement> elems = webDriver().findElements(By.tagName(type));
        if (elems != null) {
            for (final WebElement e : elems) {

                if (text.equalsIgnoreCase(e.getText())) {
                    elem = e;
                    break;
                }
            }
        }
        return elem;
    }


    /**
     * Find the element with id that has the text ....
     * 
     * @example FindById msg_id and text = "Hello World"
     * @section Location
     * @param id
     *            the id
     * @param expected
     *            the expected
     */
    @Step("FindById ([^\"]*) and text = \"([^\"]*)\"")
    public void findByIdAndText(final String id, final String expected) {
        logger.debug("Finding element with id " + id + " and text " + expected);

        try {
            webDriverContext().setCurrentElement(null);

            final By byIdAndText = WebDriverSubstepsBy.ByIdAndText(id, expected);

            final WebElement elem = webDriverContext().waitForElement(byIdAndText);

            Assert.assertNotNull("expecting to find an element with id: " + id, elem);
            webDriverContext().setCurrentElement(elem);
        } catch (final TimeoutException e) {
            logger.debug("timed out waiting for id: " + id + " with text: " + expected + " page src:\n"
                    + webDriver().getPageSource());
            throw e;
        }
    }


    /**
     * From the current element, apply the xpath selecting the first child that
     * has the text ...
     * 
     * @example FindFirstChildElementContainingText xpath="li//a" text =
     *          "Log Out"
     * @section Location
     * @param xpath
     *            the xpath
     * @param text
     *            the text
     */
    @Step("FindFirstChildElementContainingText xpath=\"([^\"]*)\" text=\"([^\"]*)\"")
    public void findChildElementContainingText(final String xpath, final String text) {
        logger.debug("Find child element with xpath " + xpath + " has the text " + text);

        final By byCurrentElement = WebDriverSubstepsBy.ByCurrentWebElement(webDriverContext().getCurrentElement());

        final By chained = new ByChained(byCurrentElement, WebDriverSubstepsBy.ByXpathContainingText(xpath, text));

        webDriverContext().setCurrentElement(null);

        final WebElement elem = MatchingElementResultHandler.AtLeastOneElement.processResults(webDriverContext(),
                chained, "expecting at least one child element to contain text: " + text);

        webDriverContext().setCurrentElement(elem);

    }


    /**
     * Finds the first html tag that starts with the specified text
     * 
     * @example FindTagElementStartingWithText tag="ul" text="list item itext"
     * @section Location
     * @param tag
     *            the tag
     * @param text
     *            the text
     */
    @Step("FindFirstTagElementStartingWithText tag=\"([^\"]*)\" text=\"([^\"]*)\"")
    public void findFirstTagElementStartingWithText(final String tag, final String text) {

        logger.debug("findTagElementStartsWithText tag: " + tag + " has text " + text);

        webDriverContext().setCurrentElement(null);

        final By by = WebDriverSubstepsBy.ByTagStartingWithText(tag, text);

        final WebElement elem = MatchingElementResultHandler.AtLeastOneElement.processResults(webDriverContext(), by,
                "expecting at least one child element to contain text: " + text);

        webDriverContext().setCurrentElement(elem);

    }
}
