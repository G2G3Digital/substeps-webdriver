package com.technophobia.webdriver.substeps.runner;

import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.webdriver.util.WebDriverContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests interaction of webdriver.shutdown, webdriver.reuse, and visual.webdriver.close.on.fail
 * on webdriver shutdown and restart
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultExecutionSetupTearDownTest {

    private DefaultExecutionSetupTearDown std;

    @Mock
    private WebdriverSubstepsConfiguration config;

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver.Options options;

    @Mock
    WebDriverFactory factory;

    private WebDriverContext context;

    @Before
    public void initialiseDependencies() {
        std = new DefaultExecutionSetupTearDown(config);

        //initialise context with visual webdriver and set test state to failed
        context = new WebDriverContext(DefaultDriverType.FIREFOX, webDriver);

        ((MutableSupplier<WebDriverContext>) std.currentWebDriverContext()).set(context);

        //creating a new webdriver instance will use the factory.
        ExecutionContext.put(Scope.SUITE, WebDriverFactory.WEB_DRIVER_FACTORY_KEY, factory);
    }

    /**
     * Global shutdown enabled - should always shutdown
     */
    @Test
    public void shouldShutdownIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(true);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);

        when(webDriver.manage()).thenReturn(options);

        context.setFailed();

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver).quit();
    }

    /**
     * Global shutdown enabled - should always shutdown
     */
    @Test
    public void shouldShutdownIfGlobalShutdownEnabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(true);
        when(config.reuseWebDriver()).thenReturn(false);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);

        when(webDriver.manage()).thenReturn(options);

        context.setFailed();

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver).quit();
    }

    /**
     * Global shutdown enabled - should always shutdown
     */
    @Test
    public void shouldShutdownIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(config.shutDownWebdriver()).thenReturn(true);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);

        when(webDriver.manage()).thenReturn(options);

        context.setFailed();

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver).quit();
    }

    /**
     * Case where the user doesn't care about retaining browser instances for failed tests, and wants to reuse
     * instances in subsequent tests.
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);

        when(webDriver.manage()).thenReturn(options);

        context.setFailed();

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver, never()).quit();
    }

    /**
     * Case where user doesn't care about retaining browser instances for failed tests, but hasn't declared that
     * they want to reuse them either - so shutdown anyway.
     */
    @Test
    public void shouldShutdownIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(false);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);

        when(webDriver.manage()).thenReturn(options);

        context.setFailed();

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver).quit();
    }

    /**
     * Case where user wants to keep browser open for failed tests (possibly to see page on failure), and doesn't
     * want the browser to be reused - don't shutdown.
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailDisabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(false);

        when(webDriver.manage()).thenReturn(options);

        context.setFailed();

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver, never()).quit();
    }

    /**
     * Case where user wants to keep browser open for failed tests (possibly to see page on failure), but
     * does want the browser to be reused - don't shutdown because the test has failed
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);

        when(webDriver.manage()).thenReturn(options);

        context.setFailed();

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver, never()).quit();
    }

    /**
     * Case where user wants to keep browser open for failed tests (possibly to see page on failure), but
     * does want the browser to be reused - don't shutdown because the test has passed and we want to reuse the
     * webdriver instance
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabledAndTestPasses() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);

        when(webDriver.manage()).thenReturn(options);

        std.basePostScenariotearDown();

        verify(options).deleteAllCookies();
        verify(webDriver, never()).quit();
    }


    /********* startup ***********/

    /**
     * Clean start - no webdriver context yet.
     */
    @Test
    public void shouldStartupOnCleanStartBeforeWebDriverContextIsInitialised() {
        ((MutableSupplier<WebDriverContext>) std.currentWebDriverContext()).set(null);

        std.basePreScenarioSetup();

        verify(factory).createWebDriver();
    }


    /**
     * Global shutdown enabled - should always startup
     */
    @Test
    public void shouldStartupIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);
        when(config.reuseWebDriver()).thenReturn(true);

        context.setFailed();
        std.basePreScenarioSetup();

        verify(factory).createWebDriver();
    }


    /**
     * Global shutdown enabled - should always startup
     */
    @Test
    public void shouldStartupIfGlobalShutdownEnabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(true);
        when(config.reuseWebDriver()).thenReturn(false);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);

        context.setFailed();
        std.basePreScenarioSetup();

        verify(factory).createWebDriver();
    }

    /**
     * Global shutdown enabled - should always startup
     */
    @Test
    public void shouldStartupIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(config.shutDownWebdriver()).thenReturn(true);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);

        context.setFailed();
        std.basePreScenarioSetup();

        verify(factory).createWebDriver();
    }

    /**
     * Case where the user doesn't care about retaining browser instances for failed tests, and wants to reuse
     * instances in subsequent tests - should not startup because we'll reuse the existing instance.
     */
    @Test
    public void shouldNotStartupIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);

        context.setFailed();
        std.basePreScenarioSetup();

        verify(factory, never()).createWebDriver();
    }

    /**
     * Case where user doesn't care about retaining browser instances for failed tests, but hasn't declared that
     * they want to reuse them either - will have shut down, so should restart.
     */
    @Test
    public void shouldStartupIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(false);
        when(config.closeVisualWebDriveronFail()).thenReturn(true);

        context.setFailed();
        std.basePreScenarioSetup();

        verify(factory).createWebDriver();
    }

    /**
     * Case where user wants to keep browser open for failed tests (possibly to see page on failure), and doesn't
     * want the browser to be reused - must startup because previous test failed and browser is being kept open.
     */
    @Test
    public void shouldStartupIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailDisabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(false);

        context.setFailed();
        std.basePreScenarioSetup();

        verify(factory).createWebDriver();
    }

    /**
     * Case where user wants to keep browser open for failed tests (possibly to see page on failure), but
     * does also want the browser to be reused - must startup because previous test failed and browser is being kept open.
     */
    @Test
    public void shouldStartupIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);

        context.setFailed();
        std.basePreScenarioSetup();

        verify(factory).createWebDriver();
    }

    /**
     * Case where user wants to keep browser open for failed tests (possibly to see page on failure), but
     * does want the browser to be reused - don't startup because the test has passed and we want to reuse the
     * webdriver instance
     */
    @Test
    public void shouldNotStartupIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabledAndTestPasses() {
        when(config.shutDownWebdriver()).thenReturn(false);
        when(config.reuseWebDriver()).thenReturn(true);
        when(config.closeVisualWebDriveronFail()).thenReturn(false);

        std.basePreScenarioSetup();

        verify(factory, never()).createWebDriver();
    }

}
