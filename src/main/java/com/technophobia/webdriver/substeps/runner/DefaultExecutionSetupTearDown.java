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
package com.technophobia.webdriver.substeps.runner;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.base.Supplier;
import com.technophobia.substeps.model.Scope;
import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.substeps.runner.INotifier;
import com.technophobia.substeps.runner.JunitNotifier;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryScenario;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryScenario;
import com.technophobia.webdriver.util.WebDriverContext;

public class DefaultExecutionSetupTearDown {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultExecutionSetupTearDown.class);
    private long startTimeMillis;

    private static final MutableSupplier<WebDriverContext> webDriverContextSupplier = new ExecutionContextSupplier<WebDriverContext>(
            Scope.SUITE, WebDriverContext.EXECUTION_CONTEXT_KEY);


    public static Supplier<WebDriverContext> currentWebDriverContext() {
        return webDriverContextSupplier;
    }


    public static WebDriver getThreadLocalWebDriver() {
        return getThreadLocalWebDriverContext().getWebDriver();
    }


    public static WebDriverContext getThreadLocalWebDriverContext() {
        return webDriverContextSupplier.get();
    }


    @BeforeAllFeatures
    public final void beforeAllFeaturesSetup() {

        final INotifier notifier = (INotifier) ExecutionContext.get(Scope.SUITE,
                JunitNotifier.NOTIFIER_EXECUTION_KEY);
        notifier.addListener(new TestFailureListener(webDriverContextSupplier));

        logger.info("beforeAllTestsSetup");

        String env = System.getProperty("environment");

        if (env == null) {
            logger.warn("\n\n\n****** NO ENVIRONMENT SET DEFAULTING TO LOCALHOST ADD -Denvironment=mycomputer\" TO OVERRIDE******* \n\n\n\n");
            env = "localhost";
            System.setProperty("environment", env);
        }

        logger.info("env prop: " + env);

    }


    @BeforeEveryScenario
    public final void basePreScenarioSetup() {
        // logger.debug("basePreScenarioSetup");

        startTimeMillis = System.currentTimeMillis();

        webDriverContextSupplier.set(new WebDriverContext(driverType()));

        if (driverType() == DriverType.HTMLUNIT) {
            setDriverLocale(webDriverContextSupplier.get().getWebDriver());
        }

    }


    @AfterEveryScenario
    public final void basePostScenariotearDown() {

        boolean doShutdown = true;
        // reasons *NOT* to shutdown

        // this overrides everything else
        if (!WebdriverSubstepsConfiguration.shutDownWebdriver()) {
            doShutdown = false;
        } else if (webDriverContextSupplier.get() != null
                && webDriverContextSupplier.get().hasFailed()
                && (!WebdriverSubstepsConfiguration.closeVisualWebDriveronFail() && driverType()
                        .isVisual())) {
            doShutdown = false;
        }

        if (doShutdown) {
            if (webDriverContextSupplier.get() != null) {
                webDriverContextSupplier.get().shutdownWebDriver();
            }
        }

        // TODO put long test threshold in config

        final long ticks = (System.currentTimeMillis() - startTimeMillis) / 1000;

        if (ticks > 30) {
            logger.warn(String.format("Test scenario took %s seconds", ticks));
        } else {
            logger.info(String.format("Test scenario took %s seconds", ticks));
        }
    }


    protected DriverType driverType() {
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

            logger.warn(ex.getMessage());

        } catch (final SecurityException e) {

            logger.warn(e.getMessage());
        } catch (final NoSuchFieldException e) {

            logger.warn(e.getMessage());
        }
    }
}
