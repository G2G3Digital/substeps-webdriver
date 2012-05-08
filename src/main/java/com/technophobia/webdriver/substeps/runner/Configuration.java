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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.runner.SubStepConfigurationException;

public enum Configuration {

    ; // uninstantiable
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final Properties props;

    private static final int TIMEOUT_IN_SECONDS;
    private static final String BASE_URL;
    private static final DriverType DRIVER_TYPE;
    private static final String DRIVER_LOCALE;
    private static final boolean SHUTDOWN_WEBDRIVER;
    private static final boolean VISUAL_WEBDRIVER_CLOSE_ON_FAIL;

    private static final String DATABASE_URL;
    private static final String DATABASE_USERNAME;
    private static final String DATABASE_PASSWORD;

    private static int smtpServerPort = 7191;
    private static long defaultWebDriverTimeoutSecs = 10;

    static {
        props = initTestProperties();
        TIMEOUT_IN_SECONDS = Integer.parseInt(getRequiredProperty("wait.seconds"));
        BASE_URL = removeTrailingSlash(getRequiredProperty("base.url"));
        DRIVER_TYPE = DriverType.valueOf(getRequiredProperty("driver.type").toUpperCase());
        DRIVER_LOCALE = props.getProperty("webdriver.locale", "en-gb");
        SHUTDOWN_WEBDRIVER = Boolean.parseBoolean(props.getProperty("webdriver.shutdown", "true"));
        VISUAL_WEBDRIVER_CLOSE_ON_FAIL = Boolean.parseBoolean(props.getProperty(
                "visual.webdriver.close.on.fail", "true"));
        DATABASE_URL = getRequiredProperty("database.url");
        DATABASE_USERNAME = getRequiredProperty("database.username");
        DATABASE_PASSWORD = getRequiredProperty("database.password");

        final String smptpPortString = props.getProperty("smtp.port");
        if (smptpPortString != null) {
            smtpServerPort = Integer.parseInt(smptpPortString);
        }

        defaultWebDriverTimeoutSecs = Integer
                .parseInt(getRequiredProperty("default.webdriver.timeout.secs"));

        logger.info("Using properties:\n" + "TIMEOUT_IN_SECONDS: " + TIMEOUT_IN_SECONDS + "\n"
                + "BASE_URL: " + BASE_URL + "\n" + "DATABASE_URL: " + DATABASE_URL + "\n"
                + "DATABASE_USERNAME: " + DATABASE_USERNAME + "\n" + "DATABASE_PASSWORD: "
                + DATABASE_PASSWORD + "\n");

    }


    public static int timeoutInSeconds() {
        return TIMEOUT_IN_SECONDS;
    }


    private static String removeTrailingSlash(final String string) {
        if (string.endsWith("/")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }


    public static String baseURL() {
        return BASE_URL;
    }


    public static DriverType driverType() {
        return DRIVER_TYPE;
    }


    public static String driverLocale() {
        return DRIVER_LOCALE;
    }


    public static boolean shutDownWebdriver() {
        return SHUTDOWN_WEBDRIVER;
    }


    public static boolean closeVisualWebDriveronFail() {
        return VISUAL_WEBDRIVER_CLOSE_ON_FAIL;
    }


    public static String databaseUrl() {
        return DATABASE_URL;
    }


    public static String databaseUsername() {
        return DATABASE_USERNAME;
    }


    public static String databasePassword() {
        return DATABASE_PASSWORD;
    }


    public static int smtpPort() {
        return smtpServerPort;
    }


    public static Properties asProperties() {
        return (Properties) props.clone();
    }


    private static Properties initTestProperties() {
        final Properties tempProps = new Properties();
        final String resourceBundleName = resourceBundleName();
        try {
            tempProps.load(Configuration.class.getResourceAsStream(resourceBundleName));
        } catch (final Exception ex) {
            logger.error("Failed to load test configuration from " + resourceBundleName, ex);

            throw new SubStepConfigurationException(ex);
        }

        return tempProps;
    }


    private static String resourceBundleName() {
        return "/" + System.getProperty("environment", "localhost") + ".properties";
    }


    private static String getRequiredProperty(final String key) {
        if (!props.containsKey(key)) {
            final String message = key + " is a required property, but has not been found.";

            logger.error(message);
            throw new IllegalStateException(message);
        }

        return props.getProperty(key);
    }


    /**
     * @return
     */
    public static long defaultTimeout() {
        return defaultWebDriverTimeoutSecs;
    }

}
