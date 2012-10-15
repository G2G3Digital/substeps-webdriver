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

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.technophobia.webdriver.substeps.runner.Condition;
import com.technophobia.webdriver.substeps.runner.DriverType;
import com.technophobia.webdriver.substeps.runner.WebdriverSubstepsConfiguration;

/**
 * A container used to hold the webdriver instance and the current element used
 * by step implementations.
 * 
 * @author imoore
 * 
 */
public class WebDriverContext {
    private static final Logger logger = LoggerFactory
            .getLogger(WebDriverContext.class);

    public static final String EXECUTION_CONTEXT_KEY = "_webdriver_context_key";

    private final WebDriver webDriver;
    private WebElement currentElement = null;
    private boolean failed = false;
    private final DriverType driverType;


    /**
     * @return the driverType
     */
    public DriverType getDriverType() {
        return driverType;
    }


    // may need to expand the params, javascript enabled, profile paths etc
    public WebDriverContext(final DriverType driverType) {
        // TODO - make this nicer by having a factory interface which the enum
        // implements, each instance will overide the method
        // focussing on overall functionality at present

        this.driverType = driverType;

        switch (driverType) {
        case FIREFOX: {
            webDriver = new FirefoxDriver();
            break;
        }
        case HTMLUNIT: {
            final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(
                    BrowserVersion.FIREFOX_3_6);
            htmlUnitDriver.setJavascriptEnabled(!WebdriverSubstepsConfiguration
                    .isJavascriptDisabledWithHTMLUnit());
            webDriver = htmlUnitDriver;
            break;

        }
        case CHROME: {

            webDriver = new ChromeDriver();
            break;

        }
        case IE: {

            // apparently this is required to get around some IE security
            // restriction.

            final DesiredCapabilities ieCapabilities = DesiredCapabilities
                    .internetExplorer();
            ieCapabilities
                    .setCapability(
                            InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                            true);

            logger.warn("Using IE Webdriver with IGNORING SECURITY DOMAIN");

            webDriver = new InternetExplorerDriver(ieCapabilities);
            break;
        }
        default: {
            throw new IllegalArgumentException("unknown driver type");
        }
        }
    }


    public WebElement getCurrentElement() {
        Assert.assertNotNull("expecting current element not to be null",
                currentElement);
        return currentElement;
    }


    public void setCurrentElement(final WebElement currentElement) {
        this.currentElement = currentElement;
    }


    public WebDriver getWebDriver() {
        return webDriver;
    }


    public void shutdownWebDriver() {
        logger.debug("Shutting WebDriver down");
        if (webDriver != null) {
            webDriver.manage().deleteAllCookies();
            webDriver.quit();
        }
    }


    public boolean hasFailed() {
        return failed;
    }


    public void setFailed() {
        failed = true;
    }


    public WebElement waitForElement(final By by) {
        return ElementLocators.waitForElement(by,
                WebdriverSubstepsConfiguration.defaultTimeout(), webDriver);
    }


    public WebElement waitForElement(final By by, final long timeOutSeconds) {
        return ElementLocators.waitForElement(by, timeOutSeconds, webDriver);
    }


    public boolean waitForCondition(final Condition condition) {
        return ElementLocators.waitForCondition(condition, webDriver);
    }
}
