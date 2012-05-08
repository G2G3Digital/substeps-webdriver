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

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.base.Supplier;
import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.substeps.runner.INotifier;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryScenario;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryScenario;
import com.technophobia.substeps.runner.JunitNotifier;
import com.technophobia.substeps.runner.Scope;
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
        if (!Configuration.shutDownWebdriver()) {
            doShutdown = false;
        } else if (webDriverContextSupplier.get() != null
                && webDriverContextSupplier.get().hasFailed()
                && (!Configuration.closeVisualWebDriveronFail() && driverType().isVisual())) {
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
        return Configuration.driverType();
    }


    /**
     * By default the HtmlUnit driver is set to en-us. This can cause problems
     * with formatters.
     */
    private void setDriverLocale(final WebDriver driver) {

        try {
            final Field field = ReflectionUtils.findField(driver.getClass(), "webClient");
            ReflectionUtils.makeAccessible(field);

            final WebClient webClient = (WebClient) field.get(driver);
            if (webClient != null) {
                webClient.addRequestHeader("Accept-Language", "en-gb");
            }

        } catch (final IllegalAccessException ex) {
            logger.warn(ex.getMessage());
        }
    }
}
