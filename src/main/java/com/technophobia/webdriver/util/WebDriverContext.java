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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.webdriver.substeps.runner.Condition;
import com.technophobia.webdriver.substeps.runner.DriverType;
import com.technophobia.webdriver.substeps.runner.WebdriverSubstepsPropertiesConfiguration;

/**
 * A container used to hold the webdriver instance and the current element used
 * by step implementations.
 * 
 * @author imoore
 */
public class WebDriverContext {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverContext.class);

    public static final String EXECUTION_CONTEXT_KEY = "_webdriver_context_key";

    private final DriverType driverType;
    private final WebDriver webDriver;
    private final WebDriverBrowserLogs browserLogs;

    private WebElement currentElement = null;
    private boolean failed = false;

    private Map<String, WebElement> elementStash = null;

    public WebDriverContext(final DriverType driverType, final WebDriver webDriver) {
        this.driverType = driverType;
        this.webDriver = webDriver;
        browserLogs = new WebDriverBrowserLogs(webDriver);
    }

    public WebElement getCurrentElement() {
        Assert.assertNotNull("expecting current element not to be null", this.currentElement);
        return this.currentElement;
    }

    public void setCurrentElement(final WebElement currentElement) {
        this.currentElement = currentElement;
    }

    public WebDriver getWebDriver() {
        return this.webDriver;
    }

    public DriverType getDriverType() {
        return this.driverType;
    }

    public void shutdownWebDriver() {
        logger.debug("Shutting WebDriver down");
        if (this.webDriver != null) {
            browserLogs.printBrowserLogs();
            this.webDriver.manage().deleteAllCookies();
            this.webDriver.quit();
        }
    }

    public void resetWebDriver() {
        logger.debug("Resetting WebDriver");
        if (this.webDriver != null) {
            browserLogs.printBrowserLogs();
            this.webDriver.manage().deleteAllCookies();
        }
    }

   

    public boolean hasFailed() {
        return this.failed;
    }

    public void setFailed() {
        this.failed = true;
    }

    public WebElement waitForElement(final By by) {
        return ElementLocators.waitForElement(by, WebdriverSubstepsPropertiesConfiguration.INSTANCE.defaultTimeout(),
                this.webDriver);
    }

    public WebElement waitForElement(final By by, final long timeOutSeconds) {
        return ElementLocators.waitForElement(by, timeOutSeconds, this.webDriver);
    }

    public boolean waitForCondition(final Condition condition) {
        return ElementLocators.waitForCondition(condition, this.webDriver);
    }

    public void stashElement(final String key, final WebElement element) {

        if (this.elementStash == null) {
            this.elementStash = new HashMap<String, WebElement>();
        }

        if (this.elementStash.containsKey(key)) {

            // do we care ?
            logger.debug("replacing existing object in stash using key: " + key);
        }

        this.elementStash.put(key, element);
    }

    public WebElement getElementFromStash(final String key) {

        final WebElement elem = this.elementStash != null ? this.elementStash.get(key) : null;

        Assert.assertNotNull("Attempt to retrieve a null element from the stash with key: " + key, elem);

        return elem;
    }
}
