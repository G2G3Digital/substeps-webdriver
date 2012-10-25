package com.technophobia.webdriver.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

/**
 * 
 * @author imoore
 * 
 */
public abstract class WebDriverSubstepsBy {

    protected static boolean elementHasExpectedAttributes(final WebElement e,
            final Map<String, String> expectedAttributes) {
        final Map<String, String> actualValues = new HashMap<String, String>();

        for (final String key : expectedAttributes.keySet()) {
            final String elementVal = e.getAttribute(key);

            // if no attribute will this throw an exception or just return
            // null ??
            actualValues.put(key, elementVal);

        }

        final MapDifference<String, String> difference = Maps.difference(
                expectedAttributes, actualValues);
        return difference.areEqual();
    }

    public static abstract class BaseBy extends By {

        @Override
        public final List<WebElement> findElements(final SearchContext context) {
            List<WebElement> matchingElems = findElementsBy(context);

            if (matchingElems == null) {
                matchingElems = Collections.emptyList();
            }

            return matchingElems;
        }


        public abstract List<WebElement> findElementsBy(
                final SearchContext context);
    }

    public static class ByTagAndAttributes extends BaseBy {

        private final String tagName;
        private final Map<String, String> requiredAttributes;


        public ByTagAndAttributes(final String tagName,
                final Map<String, String> requiredAttributes) {
            this.tagName = tagName;
            this.requiredAttributes = requiredAttributes;
        }


        @Override
        public List<WebElement> findElementsBy(final SearchContext context) {

            List<WebElement> matchingElems = null;

            final List<WebElement> tagElements = context.findElements(By
                    .tagName(tagName));

            for (final WebElement e : tagElements) {
                // does this WebElement have the attributes that we need!

                if (elementHasExpectedAttributes(e, requiredAttributes)) {

                    if (matchingElems == null) {
                        matchingElems = new ArrayList<WebElement>();
                    }
                    matchingElems.add(e);
                }
            }

            return matchingElems;
        }
    }

    /**
     * A By for use with the current web element, to be chained with other Bys
     * 
     */
    public static class ByCurrentWebElement extends BaseBy {

        private final WebElement currentElement;


        public ByCurrentWebElement(final WebElement elem) {
            currentElement = elem;
        }


        @Override
        public List<WebElement> findElementsBy(final SearchContext context) {

            final List<WebElement> matchingElems = new ArrayList<WebElement>();
            matchingElems.add(currentElement);

            return matchingElems;
        }
    }

    public static class ByTagAndWithText extends BaseBy {

        private final String tag;
        private final String text;


        public ByTagAndWithText(final String tag, final String text) {

            this.tag = tag;
            this.text = text;
        }


        /*
         * (non-Javadoc)
         * 
         * @see
         * org.openqa.selenium.By#findElements(org.openqa.selenium.SearchContext
         * )
         */
        @Override
        public List<WebElement> findElementsBy(final SearchContext context) {

            List<WebElement> matchingElems = null;

            final List<WebElement> elems = context
                    .findElements(By.tagName(tag));
            if (elems != null) {
                for (final WebElement e : elems) {

                    if (text.equalsIgnoreCase(e.getText())) {

                        if (matchingElems == null) {
                            matchingElems = new ArrayList<WebElement>();
                        }
                        matchingElems.add(e);
                    }
                }
            }

            return matchingElems;
        }

    }
}
