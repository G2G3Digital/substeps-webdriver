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
package com.technophobia.webdriver.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.base.Function;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

/**
 * @author ian
 * 
 */
public class ByChildTagAndAttributesFunction implements Function<WebDriver, WebElement> {
    private final String tag;
    private final String attributeString;
    private final Map<String, String> expectedAttributes;
    private final WebElement parent;


    public ByChildTagAndAttributesFunction(final String tag, final String attributeString,
            final WebElement parent) {
        this.tag = tag;
        expectedAttributes = convertToMap(attributeString);
        this.attributeString = attributeString;
        this.parent = parent;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.google.common.base.Function#apply(java.lang.Object)
     */
    public WebElement apply(final WebDriver driver) {

        WebElement rtn = null;

        final List<WebElement> elems = parent.findElements(By.tagName(tag));

        Assert.assertNotNull("expecting some elements for tag: " + tag, elems);
        Assert.assertTrue("expecting some elements for tag: " + tag, !elems.isEmpty());

        List<WebElement> matchingElems = null;
        for (final WebElement e : elems) {
            // does this WebElement have the attributes that we need!

            final Map<String, String> actualValues = new HashMap<String, String>();

            for (final String key : expectedAttributes.keySet()) {
                final String elementVal = e.getAttribute(key);

                // if no attribute will this throw an exception or just return
                // null ??
                actualValues.put(key, elementVal);

            }

            final MapDifference<String, String> difference = Maps.difference(expectedAttributes,
                    actualValues);

            if (difference.areEqual()) {
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
            rtn = matchingElems.get(0);
        }

        Assert.assertNotNull("failed to locate an element with tag: " + tag + " and attributes: "
                + attributeString, rtn);

        return rtn;
    }


    /**
     * Convert to map.
     * 
     * @example
     * @param attributes
     *            the attributes
     * @return the map
     */
    private Map<String, String> convertToMap(final String attributes) {
        Map<String, String> attributeMap = null;

        // split the attributes up, will be received as a comma separated list
        // of name value pairs
        final String[] nvps = attributes.split(",");

        if (nvps != null) {
            for (final String nvp : nvps) {
                final String[] split = nvp.split("=");
                if (split != null && split.length == 2) {
                    if (attributeMap == null) {
                        attributeMap = new HashMap<String, String>();
                    }
                    attributeMap.put(split[0], split[1].replaceAll("\"", ""));
                }
            }
        }

        return attributeMap;
    }

}
