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
import com.technophobia.substeps.runner.INotificationDistributor;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryScenario;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryScenario;
import com.technophobia.webdriver.util.WebDriverContext;

public class DefaultExecutionSetupTearDown {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExecutionSetupTearDown.class);
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

        final INotificationDistributor notifier = (INotificationDistributor) ExecutionContext.get(
                Scope.SUITE, INotificationDistributor.NOTIFIER_DISTRIBUTOR_KEY);

        notifier.addListener(new TestFailureListener(webDriverContextSupplier));

        logger.info("beforeAllTestsSetup");

        String env = System.getProperty("environment");

        if (env == null) {
            logger.warn("\n\n\n****** NO ENVIRONMENT SET DEFAULTING TO LOCALHOST ADD -Denvironment=mycomputer\" TO OVERRIDE******* \n\n\n\n");
            env = "localhost";
            System.setProperty("environment", env);
        }

        logger.info("env prop: " + env);


        WebDriverFactory factory = createWebDriverFactory();
        ExecutionContext.put(Scope.SUITE, WebDriverFactory.WEB_DRIVER_FACTORY_KEY, factory);
    }


    @BeforeEveryScenario
    public final void basePreScenarioSetup() {
        startTimeMillis = System.currentTimeMillis();

        WebDriverFactory factory = (WebDriverFactory) ExecutionContext.get(Scope.SUITE, WebDriverFactory.WEB_DRIVER_FACTORY_KEY);
        webDriverContextSupplier.set(new WebDriverContext(factory.driverType(), factory.createWebDriver()));
    }


    @AfterEveryScenario
    public final void basePostScenariotearDown() {


        boolean doShutdown = true;
        // reasons *NOT* to shutdown

        final WebDriverContext webDriverContext = webDriverContextSupplier.get();

        if (!WebdriverSubstepsConfiguration.shutDownWebdriver()) {
            // this overrides everything else
            System.out.println("global don't shutdown");
            doShutdown = false;
        } else if (webDriverContext != null
                && webDriverContext.hasFailed()
                && (!WebdriverSubstepsConfiguration.closeVisualWebDriveronFail() && webDriverContext.getDriverType().isVisual())) {

            System.out.println("failed and visual shutdown");
            doShutdown = false;
        }

        if (doShutdown) {

            if (webDriverContext != null) {
                System.out.println("webDriverContextSupplier.get().hasFailed(): "
                        + webDriverContext.hasFailed());
            }

            System.out.println("WebdriverSubstepsConfiguration.closeVisualWebDriveronFail(): "
                    + WebdriverSubstepsConfiguration.closeVisualWebDriveronFail());

            System.out.println("driverType().isVisual(): " + webDriverContext.getDriverType().isVisual());

            System.out.println("doing shutdown");

            if (webDriverContext != null) {
                webDriverContext.shutdownWebDriver();
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

    private WebDriverFactory createWebDriverFactory() {
        Class<? extends WebDriverFactory> webDriverFactoryClass = WebdriverSubstepsConfiguration.getWebDriverFactoryClass();

        logger.debug("Creating WebDriverFactory of type [{}]", webDriverFactoryClass.getName());

        try {
            return webDriverFactoryClass.newInstance();
        } catch (InstantiationException ex) {
            throw new IllegalStateException(String.format("Failed to create WebDriverFactory %s.", webDriverFactoryClass.getName()), ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(String.format("Failed to create WebDriverFactory %s.", webDriverFactoryClass.getName()), ex);
        }
    }

}
