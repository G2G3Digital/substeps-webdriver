package com.technophobia.webdriver.substeps.runner;

/**
 * User: dmoss
 * Date: 20/05/13
 * Time: 21:58
 */
public interface WebdriverSubstepsConfiguration {
    String baseURL();

    DefaultDriverType driverType();

    String driverLocale();

    boolean shutDownWebdriver();

    boolean isJavascriptDisabledWithHTMLUnit();

    boolean closeVisualWebDriveronFail();

    boolean reuseWebDriver();

    long defaultTimeout();

    String getHtmlUnitProxyHost();

    Integer getHtmlUnitProxyPort();

    Class<? extends WebDriverFactory> getWebDriverFactoryClass();
}
