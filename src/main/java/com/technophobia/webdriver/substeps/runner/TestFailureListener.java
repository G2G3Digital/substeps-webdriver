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

import org.junit.runner.Description;

import com.technophobia.substeps.runner.EmptyNotifier;
import com.technophobia.substeps.runner.INotifier;
import com.technophobia.webdriver.util.WebDriverContext;

/**
 * 
 * @author imoore
 * 
 */
public class TestFailureListener extends EmptyNotifier implements INotifier {

    private final MutableSupplier<WebDriverContext> webDriverContextSupplier;


    public TestFailureListener(final MutableSupplier<WebDriverContext> webDriverContextSupplier) {
        super();
        this.webDriverContextSupplier = webDriverContextSupplier;
    }


    @Override
    public void notifyTestFailed(final Description junitDescription, final Throwable cause) {
        final WebDriverContext webDriverContext = webDriverContextSupplier.get();
        // possible to have a failure before the webdrivercontext has been
        // initialised - missing ' default.webdriver.timeout.secs' property for
        // example
        if (webDriverContext != null) {
            webDriverContext.setFailed();
        }
    }
}
