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
        return this.webDriverContextSupplier.get().getWebDriver();
    }


    protected WebDriverContext webDriverContext() {
        return this.webDriverContextSupplier.get();
    }


    public byte[] getScreenshotBytes() {

        final WebDriver webDriver = webDriver();

        return TakesScreenshot.class.isAssignableFrom(webDriver.getClass()) ? getScreenshotBytes((TakesScreenshot) webDriver)
                : null;
    }


    private byte[] getScreenshotBytes(final TakesScreenshot screenshotTaker) {

        return screenshotTaker.getScreenshotAs(OutputType.BYTES);
    }
}
