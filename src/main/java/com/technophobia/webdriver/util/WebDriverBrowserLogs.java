/*
 *  Copyright Technophobia Ltd 2014
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
package com.technophobia.webdriver.util;

import java.util.Date;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebDriverBrowserLogs {
    private static final Logger logger = LoggerFactory.getLogger(WebDriverBrowserLogs.class);
    
    private final WebDriver webDriver;
    
    public WebDriverBrowserLogs (WebDriver webDriver){
        this.webDriver = webDriver;
    }
    
    public void printBrowserLogs() {
        
        if (logger.isTraceEnabled()){
            final Logs logs = webDriver.manage().logs();
            if (logs != null) {
                final LogEntries logEntries = logs.get(LogType.BROWSER);
    
                final StringBuilder buf = new StringBuilder();
                buf.append("BROWSER LOGS:\n\n");
                for (final LogEntry entry : logEntries) {
                    buf.append(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage() + "\n");
                }
    
                logger.trace(buf.toString());
            }
        }
    }    
}
