/*
 * Copyright Technophobia Ltd 2013
 *
 *     This file is part of Substeps.
 *
 *     Substeps is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version
 *
 *     Substeps is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.technophobia.webdriver.substeps.runner;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class DefaultWebDriverFactory implements WebDriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebDriverFactory.class);

    public WebDriver createWebDriver() {

        final WebDriver webDriver;

        switch (WebdriverSubstepsConfiguration.driverType()) {
            case FIREFOX: {
                webDriver = new FirefoxDriver();
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

                setDriverLocale(htmlUnitDriver);

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

                final DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
                ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

                LOG.warn("Using IE Webdriver with IGNORING SECURITY DOMAIN");

                webDriver = new InternetExplorerDriver(ieCapabilities);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown driver type");
            }
        }

        return webDriver;

    }

    public DriverType driverType() {
        return WebdriverSubstepsConfiguration.driverType();
    }

    /**
     * By default the HtmlUnit driver is set to en-us. This can cause problems
     * with formatters.
     */
    private void setDriverLocale(final WebDriver driver) {

        try {
            final Field field = driver.getClass().getDeclaredField("webClient");
            if (field != null) {
                final boolean original = field.isAccessible();
                field.setAccessible(true);

                final WebClient webClient = (WebClient) field.get(driver);
                if (webClient != null) {
                    webClient.addRequestHeader("Accept-Language", "en-gb");
                }
                field.setAccessible(original);
            } else {
                Assert.fail("Failed to get webclient field to set accept language");
            }
        } catch (final IllegalAccessException ex) {

            LOG.warn(ex.getMessage());

        } catch (final SecurityException e) {

            LOG.warn(e.getMessage());
        } catch (final NoSuchFieldException e) {

            LOG.warn(e.getMessage());
        }
    }

}
