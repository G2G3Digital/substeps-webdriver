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

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
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
    private static final Logger logger = LoggerFactory.getLogger(WebDriverContext.class);

    public static final String EXECUTION_CONTEXT_KEY = "_webdriver_context_key";

    private final WebDriver webDriver;
    private WebElement currentElement = null;
    private boolean failed = false;
    private final DriverType driverType;

    private Map<String, WebElement> elementStash = null;


    /**
     * @return the driverType
     */
    public DriverType getDriverType() {
        return this.driverType;
    }


    // may need to expand the params, javascript enabled, profile paths etc
    public WebDriverContext(final DriverType driverType) {
        // TODO - make this nicer by having a factory interface which the enum
        // implements, each instance will overide the method
        // focussing on overall functionality at present

        this.driverType = driverType;

        switch (driverType) {
        case FIREFOX: {
            this.webDriver = new FirefoxDriver();
            break;
        }
        case HTMLUNIT: {
            final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
            htmlUnitDriver.setJavascriptEnabled(!WebdriverSubstepsConfiguration.isJavascriptDisabledWithHTMLUnit());

            // Run via a proxy - HTML unit only for timebeing
            final String proxyHost = WebdriverSubstepsConfiguration.getHtmlUnitProxyHost();
            if (!StringUtils.isEmpty(proxyHost)) {
                final int proxyPort = WebdriverSubstepsConfiguration.getHtmlUnitProxyPort();
                htmlUnitDriver.setProxy(proxyHost, proxyPort);
            }

            this.webDriver = htmlUnitDriver;
            break;

        }
        case CHROME: {

            this.webDriver = new ChromeDriver();
            break;

        }
        case IE: {

            // apparently this is required to get around some IE security
            // restriction.

            final DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
            ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

            logger.warn("Using IE Webdriver with IGNORING SECURITY DOMAIN");

            this.webDriver = new InternetExplorerDriver(ieCapabilities);
            break;
        }
        default: {
            throw new IllegalArgumentException("unknown driver type");
        }
        }
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


    public void shutdownWebDriver() {
        logger.debug("Shutting WebDriver down");
        if (this.webDriver != null) {
            this.webDriver.manage().deleteAllCookies();
            this.webDriver.quit();
        }
    }


    public boolean hasFailed() {
        return this.failed;
    }


    public void setFailed() {
        this.failed = true;
    }


    public WebElement waitForElement(final By by) {
        return ElementLocators.waitForElement(by, WebdriverSubstepsConfiguration.defaultTimeout(), this.webDriver);
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
