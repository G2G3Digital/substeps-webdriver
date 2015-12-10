/*
 *  Copyright Technophobia Ltd 2014
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

import static org.hamcrest.Matchers.greaterThan;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.technophobia.webdriver.util.WebDriverContext;

/**
 * Class encapsulating logic around how to process multiple results found as
 * result of a By. instances should specialise the checking of multiple
 * results.  This class started off as an Enum before the NthElement handler, left a bit of sugar in to maintain the existing code
 * 
 * @author imoore
 * 
 */
public abstract class MatchingElementResultHandler {

    public static final MatchingElementResultHandler.AtLeastOneElementImpl AtLeastOneElement = new AtLeastOneElementImpl();
    public static final MatchingElementResultHandler.ExactlyOneElementImpl ExactlyOneElement = new ExactlyOneElementImpl();
    public static final MatchingElementResultHandler.NoneFailingOneElementImpl NoneFailingOneElement = new NoneFailingOneElementImpl();

    /**
     * This class will check that only one matching element is found.
     * finding multiple elements will result in failure
     * 
     */
    public static class ExactlyOneElementImpl extends MatchingElementResultHandler {

        @Override
        WebElement checkMatchingElements(final List<WebElement> matchingElems, final String msg) {

            return checkForOneMatchingElement(msg, matchingElems);
        }
    }
    
    /**
     * This class fits into the MatchingElementResultHandler pattern, but doesn't throw an exception if no element is found, that is left to calling classes.
     *
     */
    public static class NoneFailingOneElementImpl extends MatchingElementResultHandler {

        @Override
        WebElement checkMatchingElements(final List<WebElement> matchingElems, final String msg) {

            WebElement rtn = null;
            if (matchingElems != null && matchingElems.size() > 1) {
                Assert.fail("Found " + matchingElems.size() + " elements that meet this criteria, expecting only one");
            }
            else if (matchingElems != null) {
                rtn = matchingElems.get(0);
            }
            return rtn;
        }
    }

    /**
     * This class will look for one matching element and disregard others.
     * If no elements are found a failure will be reported. The first
     * element out of multiple will be returned.
     */
    public static class AtLeastOneElementImpl extends MatchingElementResultHandler {

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
    public static class NthElement extends MatchingElementResultHandler {

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


    public WebElement processResults(final WebDriverContext ctx, final By by, final String msg) {
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

    /**
     * Checks that a list of WebElements only contains one (not empty and not
     * too many).
     * 
     * @param msg the assertion message
     * @param matchingElems the initial matching elements
     * @return the matching web element
     */
    public static WebElement checkForOneMatchingElement(final String msg, final List<WebElement> matchingElems) {
        WebElement rtn = null;
        if (matchingElems != null && matchingElems.size() > 1) {
            Assert.fail("Found " + matchingElems.size() + " elements that meet this criteria, expecting only one");
        }

        else if (matchingElems != null) {
            rtn = matchingElems.get(0);
        }

        Assert.assertNotNull(msg, rtn);
        return rtn;
    }
    
}