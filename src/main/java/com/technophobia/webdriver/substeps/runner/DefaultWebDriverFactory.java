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

import java.lang.reflect.Field;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public class DefaultWebDriverFactory implements WebDriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebDriverFactory.class);

    private final WebdriverSubstepsConfiguration configuration;

    public DefaultWebDriverFactory() {
        this(WebdriverSubstepsPropertiesConfiguration.INSTANCE);
    }

    public DefaultWebDriverFactory(final WebdriverSubstepsConfiguration configuration) {
        this.configuration = configuration;
    }

    public WebDriver createWebDriver() {

        final WebDriver webDriver;

        switch (configuration.driverType()) {
            case FIREFOX: {
                final DesiredCapabilities firefoxCapabilities = DesiredCapabilities.firefox();
                setNetworkCapabilities(firefoxCapabilities);

                setLoggingPreferences(firefoxCapabilities);

                webDriver = new FirefoxDriver(firefoxCapabilities);
                break;

            }
            case HTMLUNIT: {
                final HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
                htmlUnitDriver.setJavascriptEnabled(!configuration.isJavascriptDisabledWithHTMLUnit());

                // Run via a proxy - firstly try deprecated HTML unit only
                // properties
                final String htmlunitProxyHost = configuration.getHtmlUnitProxyHost();
                if (StringUtils.isNotEmpty(htmlunitProxyHost)) {
                    final int htmlunitProxyPort = configuration.getHtmlUnitProxyPort();
                    htmlUnitDriver.setProxy(htmlunitProxyHost, htmlunitProxyPort);
                }
                // Run via a proxy - secondly new network proxy settings
                final String proxyHost = configuration.getNetworkProxyHost();
                if (StringUtils.isNotEmpty(proxyHost)) {
                    final int proxyPort = configuration.getNetworkProxyPort();
                    htmlUnitDriver.setProxy(proxyHost, proxyPort);
                }

                setDriverLocale(htmlUnitDriver);

                webDriver = htmlUnitDriver;
                break;

            }
            case CHROME: {

                final DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
                setNetworkCapabilities(chromeCapabilities);

                setLoggingPreferences(chromeCapabilities);

                webDriver = new ChromeDriver(chromeCapabilities);
                break;

            }
            case IE: {

                // apparently this is required to get around some IE security
                // restriction.
                final DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
                ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                        true);

                LOG.warn("Using IE Webdriver with IGNORING SECURITY DOMAIN");

                setNetworkCapabilities(ieCapabilities);
                webDriver = new InternetExplorerDriver(ieCapabilities);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown driver type " + configuration.driverType());
            }
        }

        webDriver.manage().window().maximize();
        
        return webDriver;

    }

    private void setLoggingPreferences(final DesiredCapabilities chromeCapabilities) {
        // TODO switch on based on properties
        final LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        chromeCapabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    }

    private void setNetworkCapabilities(final DesiredCapabilities capabilities) {
        final String proxyHost = configuration.getNetworkProxyHost();
        if (StringUtils.isNotEmpty(proxyHost)) {
            final int proxyPort = configuration.getNetworkProxyPort();
            final String proxyHostAndPort = proxyHost + ":" + proxyPort;
            final org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
            proxy.setHttpProxy(proxyHostAndPort).setFtpProxy(proxyHostAndPort).setSslProxy(proxyHostAndPort);
            capabilities.setCapability(CapabilityType.PROXY, proxy);
            LOG.info("Proxy set to {}", proxyHostAndPort);
        }
    }

    public DriverType driverType() {
        return configuration.driverType();
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
