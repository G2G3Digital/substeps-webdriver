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
package com.technophobia.webdriver.substeps.impl;

import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dumbster.smtp.SmtpMessage;
import com.google.common.base.Supplier;
import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.webdriver.substeps.impl.EmailSystem.ToFromSubjectMatcher;
import com.technophobia.webdriver.util.WebDriverContext;

public class EmailWebDriverSubStepImplementations extends AbstractWebDriverSubStepImplementations {

    private static final Logger logger = LoggerFactory
            .getLogger(EmailWebDriverSubStepImplementations.class);


    public EmailWebDriverSubStepImplementations() {
        super();
    }


    public EmailWebDriverSubStepImplementations(
            final Supplier<WebDriverContext> webDriverContextSupplier) {
        super(webDriverContextSupplier);
    }


    /**
     * Start an email server. NB the application will need to be configured to
     * use the host(and port) where these tests are being run.
     * 
     * @example StartEmail
     * @section Email related
     */
    @Step("StartEmail")
    public void startEmail() {
        logger.debug("Starting email server");
        EmailSystem.start();
        // currentScenarioEmailContext = null;
    }


    /**
     * Stop the test email server.
     * 
     * @example StopEmail
     * @section Email related
     */
    @Step("StopEmail")
    public void stopEmail() {
        logger.debug("Stopping email server");
        EmailSystem.stop();
        // currentScenarioEmailContext = null;
    }


    /**
     * Check that an email was received to ... with a subject line of ...
     * 
     * @example AssertEmailReceived to "mickey@disney.com" with subject
     *          "You've won!"
     * @section Email related
     * @param recipient
     *            the recipient
     * @param subject
     *            the subject
     */
    @Step("AssertEmailReceived to \"([^\"]*)\" with subject \"([^\"]*)\"")
    public void assertEmailReceived(final String recipient, final String subject) {
        logger.debug("Asserting " + recipient + " received an email with subject " + subject);
        final ToFromSubjectMatcher predicate = new ToFromSubjectMatcher(recipient, subject, null);

        final List<SmtpMessage> emailsMatching = EmailSystem.getEmailsMatching(predicate);

        Assert.assertNotNull("expecting some emails matching criteria", emailsMatching);
        Assert.assertFalse("expecting some emails matching criteria", emailsMatching.isEmpty());

        // final SmtpMessage email = emailsMatching.get(0);

        logger.debug("save the email body into the scenario context, so it can be accessed by later steps.");
        // currentScenarioEmailContext.setAttribute("emailbody",
        // email.getBody());

    }
}
