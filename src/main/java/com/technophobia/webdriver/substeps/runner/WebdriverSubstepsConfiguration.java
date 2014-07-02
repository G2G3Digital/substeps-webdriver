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

    /** @deprecated use getNetworkProxyHost instead */
    @Deprecated
    String getHtmlUnitProxyHost();

    /** @deprecated use getNetworkProxyPort instead */
    @Deprecated
    Integer getHtmlUnitProxyPort();

    String getNetworkProxyHost();
    
    int getNetworkProxyPort();
    
    Class<? extends WebDriverFactory> getWebDriverFactoryClass();
}
