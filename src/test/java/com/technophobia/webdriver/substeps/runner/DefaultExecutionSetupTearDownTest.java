package com.technophobia.webdriver.substeps.runner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;

import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.substeps.runner.MutableSupplier;
import com.technophobia.webdriver.util.WebDriverContext;

/**
 * Tests interaction of webdriver.shutdown, webdriver.reuse, and
 * visual.webdriver.close.on.fail on webdriver shutdown and restart
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
        this.std = new DefaultExecutionSetupTearDown(this.config);

        // initialise context with visual webdriver and set test state to failed
        this.context = new WebDriverContext(DefaultDriverType.FIREFOX, this.webDriver);

        ((MutableSupplier<WebDriverContext>) DefaultExecutionSetupTearDown.currentWebDriverContext()).set(this.context);

        // creating a new webdriver instance will use the factory.
        ExecutionContext.put(Scope.SUITE, WebDriverFactory.WEB_DRIVER_FACTORY_KEY, this.factory);
    }


    /**
     * Global shutdown enabled - should always shutdown
     */
    @Test
    public void shouldShutdownIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(true);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.context.setFailed();

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver).quit();
    }


    /**
     * Global shutdown enabled - should always shutdown
     */
    @Test
    public void shouldShutdownIfGlobalShutdownEnabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(true);
        when(this.config.reuseWebDriver()).thenReturn(false);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.context.setFailed();

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver).quit();
    }


    /**
     * Global shutdown enabled - should always shutdown
     */
    @Test
    public void shouldShutdownIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(this.config.shutDownWebdriver()).thenReturn(true);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.context.setFailed();

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver).quit();
    }


    /**
     * Case where the user doesn't care about retaining browser instances for
     * failed tests, and wants to reuse instances in subsequent tests.
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.context.setFailed();

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver, never()).quit();
    }


    /**
     * Case where user doesn't care about retaining browser instances for failed
     * tests, but hasn't declared that they want to reuse them either - so
     * shutdown anyway.
     */
    @Test
    public void shouldShutdownIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(false);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.context.setFailed();

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver).quit();
    }


    /**
     * Case where user wants to keep browser open for failed tests (possibly to
     * see page on failure), and doesn't want the browser to be reused - don't
     * shutdown.
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailDisabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(false);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.context.setFailed();

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver, never()).quit();
    }


    /**
     * Case where user wants to keep browser open for failed tests (possibly to
     * see page on failure), but does want the browser to be reused - don't
     * shutdown because the test has failed
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.context.setFailed();

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver, never()).quit();
    }


    /**
     * Case where user wants to keep browser open for failed tests (possibly to
     * see page on failure), but does want the browser to be reused - don't
     * shutdown because the test has passed and we want to reuse the webdriver
     * instance
     */
    @Test
    public void shouldNotShutdownIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabledAndTestPasses() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);

        when(this.webDriver.manage()).thenReturn(this.options);

        this.std.basePostScenariotearDown();

        verify(this.options).deleteAllCookies();
        verify(this.webDriver, never()).quit();
    }


    /********* startup ***********/

    /**
     * Clean start - no webdriver context yet.
     */
    @Test
    public void shouldStartupOnCleanStartBeforeWebDriverContextIsInitialised() {
        ((MutableSupplier<WebDriverContext>) DefaultExecutionSetupTearDown.currentWebDriverContext()).set(null);

        this.std.basePreScenarioSetup();

        verify(this.factory).createWebDriver();
    }


    /**
     * Global shutdown enabled - should always startup
     */
    @Test
    public void shouldStartupIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);
        when(this.config.reuseWebDriver()).thenReturn(true);

        this.context.setFailed();
        this.std.basePreScenarioSetup();

        verify(this.factory).createWebDriver();
    }


    /**
     * Global shutdown enabled - should always startup
     */
    @Test
    public void shouldStartupIfGlobalShutdownEnabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(true);
        when(this.config.reuseWebDriver()).thenReturn(false);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);

        this.context.setFailed();
        this.std.basePreScenarioSetup();

        verify(this.factory).createWebDriver();
    }


    /**
     * Global shutdown enabled - should always startup
     */
    @Test
    public void shouldStartupIfGlobalShutdownEnabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(this.config.shutDownWebdriver()).thenReturn(true);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);

        this.context.setFailed();
        this.std.basePreScenarioSetup();

        verify(this.factory).createWebDriver();
    }


    /**
     * Case where the user doesn't care about retaining browser instances for
     * failed tests, and wants to reuse instances in subsequent tests - should
     * not startup because we'll reuse the existing instance.
     */
    @Test
    public void shouldNotStartupIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);

        this.context.setFailed();
        this.std.basePreScenarioSetup();

        verify(this.factory, never()).createWebDriver();
    }


    /**
     * Case where user doesn't care about retaining browser instances for failed
     * tests, but hasn't declared that they want to reuse them either - will
     * have shut down, so should restart.
     */
    @Test
    public void shouldStartupIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailEnabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(false);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(true);

        this.context.setFailed();
        this.std.basePreScenarioSetup();

        verify(this.factory).createWebDriver();
    }


    /**
     * Case where user wants to keep browser open for failed tests (possibly to
     * see page on failure), and doesn't want the browser to be reused - must
     * startup because previous test failed and browser is being kept open.
     */
    @Test
    public void shouldStartupIfGlobalShutdownDisabledAndReuseDisabledAndCloseOnFailDisabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(false);

        this.context.setFailed();
        this.std.basePreScenarioSetup();

        verify(this.factory).createWebDriver();
    }


    /**
     * Case where user wants to keep browser open for failed tests (possibly to
     * see page on failure), but does also want the browser to be reused - must
     * startup because previous test failed and browser is being kept open.
     */
    @Test
    public void shouldStartupIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabled() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);

        this.context.setFailed();
        this.std.basePreScenarioSetup();

        verify(this.factory).createWebDriver();
    }


    /**
     * Case where user wants to keep browser open for failed tests (possibly to
     * see page on failure), but does want the browser to be reused - don't
     * startup because the test has passed and we want to reuse the webdriver
     * instance
     */
    @Test
    public void shouldNotStartupIfGlobalShutdownDisabledAndReuseEnabledAndCloseOnFailDisabledAndTestPasses() {
        when(this.config.shutDownWebdriver()).thenReturn(false);
        when(this.config.reuseWebDriver()).thenReturn(true);
        when(this.config.closeVisualWebDriveronFail()).thenReturn(false);

        this.std.basePreScenarioSetup();

        verify(this.factory, never()).createWebDriver();
    }

}
