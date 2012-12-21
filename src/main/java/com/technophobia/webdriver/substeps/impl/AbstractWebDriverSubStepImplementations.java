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

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.base.Supplier;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.technophobia.substeps.runner.ProvidesScreenshot;
import com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown;
import com.technophobia.webdriver.util.WebDriverContext;

public abstract class AbstractWebDriverSubStepImplementations implements ProvidesScreenshot {

    private final Supplier<WebDriverContext> webDriverContextSupplier;

    public AbstractWebDriverSubStepImplementations() {
        this(DefaultExecutionSetupTearDown.currentWebDriverContext());
    }

    public AbstractWebDriverSubStepImplementations(final Supplier<WebDriverContext> webDriverContextSupplier) {
        this.webDriverContextSupplier = webDriverContextSupplier;
    }

    /**
     * Convert a string of the form type="submit",value="Search" to a map.
     * 
     * @example
     * @param attributes
     *            the attributes string
     * @return the map
     */
    protected Map<String, String> convertToMap(final String attributes) {
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

    protected boolean elementHasExpectedAttributes(final WebElement e, final Map<String, String> expectedAttributes) {
        final Map<String, String> actualValues = new HashMap<String, String>();

        for (final String key : expectedAttributes.keySet()) {
            final String elementVal = e.getAttribute(key);

            // if no attribute will this throw an exception or just return
            // null ??
            actualValues.put(key, elementVal);

        }

        final MapDifference<String, String> difference = Maps.difference(expectedAttributes, actualValues);
        return difference.areEqual();
    }

    protected WebDriver webDriver() {
        return webDriverContextSupplier.get().getWebDriver();
    }

    protected WebDriverContext webDriverContext() {
        return webDriverContextSupplier.get();
    }

    public byte[] getScreenshotBytes() {

        WebDriver webDriver = webDriver();

        return TakesScreenshot.class.isAssignableFrom(webDriver.getClass()) ? getScreenshotBytes((TakesScreenshot) webDriver)
                : null;
    }

    private byte[] getScreenshotBytes(TakesScreenshot screenshotTaker) {

        return screenshotTaker.getScreenshotAs(OutputType.BYTES);
    }
}
