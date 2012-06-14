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
import org.junit.runner.notification.RunNotifier;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.runner.INotifier;
import com.technophobia.webdriver.util.WebDriverContext;

/**
 * 
 * @author imoore
 * 
 */
public class TestFailureListener implements INotifier {

    private final MutableSupplier<WebDriverContext> webDriverContextSupplier;


    public TestFailureListener(final MutableSupplier<WebDriverContext> webDriverContextSupplier) {
        super();
        this.webDriverContextSupplier = webDriverContextSupplier;
    }


    public void notifyTestFailed(final Description junitDescription, final Throwable cause) {
        final WebDriverContext webDriverContext = webDriverContextSupplier.get();
        // possible to have a failure before the webdrivercontext has been
        // initialised - missing ' default.webdriver.timeout.secs' property for
        // example
        if (webDriverContext != null) {
            webDriverContext.setFailed();
        }
    }


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#addListener(com.technophobia.substeps.runner.INotifier)
	 */
	public void addListener(final INotifier arg0)
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#notifyTestFailed(com.technophobia.substeps.execution.ExecutionNode, java.lang.Throwable)
	 */
	public void notifyTestFailed(final ExecutionNode arg0, final Throwable arg1)
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#notifyTestFinished(org.junit.runner.Description)
	 */
	public void notifyTestFinished(final Description arg0)
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#notifyTestFinished(com.technophobia.substeps.execution.ExecutionNode)
	 */
	public void notifyTestFinished(final ExecutionNode arg0)
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#notifyTestIgnored(org.junit.runner.Description)
	 */
	public void notifyTestIgnored(final Description arg0)
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#notifyTestStarted(org.junit.runner.Description)
	 */
	public void notifyTestStarted(final Description arg0)
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#notifyTestStarted(com.technophobia.substeps.execution.ExecutionNode)
	 */
	public void notifyTestStarted(final ExecutionNode arg0)
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#pleaseStop()
	 */
	public void pleaseStop()
	{
		// no op
	}


	/* (non-Javadoc)
	 * @see com.technophobia.substeps.runner.INotifier#setJunitRunNotifier(org.junit.runner.notification.RunNotifier)
	 */
	public void setJunitRunNotifier(final RunNotifier arg0)
	{
		// no op
	}
}
