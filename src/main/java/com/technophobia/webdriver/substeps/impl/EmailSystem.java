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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.google.common.base.Predicate;

/**
 * A wrapper around the Dumbster mock SMTP server
 *
 *
 * @author imoore
 *
 */
public class EmailSystem {
	
	private EmailSystem(){
		// uninstantiable
	}
	
    private static final Logger log = LoggerFactory.getLogger(EmailSystem.class);

    private SimpleSmtpServer server;

    private static final EmailSystem instance = new EmailSystem();

    private static final int port = 7191;


    public static void start() {

        if (instance.server != null) {
            log.debug("stopping current email server instance during start request");
            stop();
        }

        log.debug("start an email server instance");
        instance.server = SimpleSmtpServer.start(port);
    }


    /**
     *
     */
    public static void stop() {
        if (instance.server != null) {
            log.debug("stop email server");
            instance.server.stop();
        }
    }

    public static final String SMTP_HEADER_MSG_ID = "Message-ID";
    public static final String SMTP_HEADER_SUBJECT = "Subject";
    public static final String SMTP_HEADER_DATE = "Date";
    public static final String SMTP_HEADER_TO = "To";
    public static final String SMTP_HEADER_FROM = "From";

    public static class ToFromSubjectMatcher implements Predicate<SmtpMessage> {

        private String expectedRecipient;
        private String expectedSubject;
        private String expectedFrom;


        public ToFromSubjectMatcher() {
        }


        /**
         * If any of the recipient, subject, from fields are null then it will
         * be ignored when testing for the email.
         *
         * @param recipient
         * @param subject
         * @param from
         */
        public ToFromSubjectMatcher(final String recipient, final String subject, final String from) {
            expectedRecipient = recipient;
            expectedSubject = subject;
            expectedFrom = from;
        }


        /**
         * {@inheritDoc}
         */
        public boolean apply(final SmtpMessage input) {

            return fieldMatches(SMTP_HEADER_TO, expectedRecipient, input)
                    && fieldMatches(SMTP_HEADER_FROM, expectedFrom, input)
                    && fieldMatches(SMTP_HEADER_SUBJECT, expectedSubject, input);
        }


        private boolean fieldMatches(final String header, final String expected,
                final SmtpMessage input) {
            if (expected == null) {
                return true;
            }
            return expected.equals(input.getHeaderValue(header));
        }

    }


    public static List<SmtpMessage> getAllMessages() {
        final List<SmtpMessage> totalMessagesRecevied = new ArrayList<SmtpMessage>();

        @SuppressWarnings("unchecked")
		final Iterator<SmtpMessage> receivedEmail = instance.server.getReceivedEmail();

        while (receivedEmail.hasNext()) {
            log.debug("got message");
            totalMessagesRecevied.add(receivedEmail.next());
        }
        return totalMessagesRecevied;
    }


    public static List<SmtpMessage> getEmailsMatching(final Predicate<SmtpMessage> predicate) {

        // copy out the emails that Dumbster has NB. this may result in
        // exceptions as the list that the iterator is over is still being added
        // to!

        final List<SmtpMessage> totalMessagesRecevied = new ArrayList<SmtpMessage>();

        @SuppressWarnings("unchecked")
		final Iterator<SmtpMessage> receivedEmail = instance.server.getReceivedEmail();

        while (receivedEmail.hasNext()) {
            // log.debug("got message");
            totalMessagesRecevied.add(receivedEmail.next());
        }

        // should be able to now have a look at the messages without worrying
        // about a threading exception
        List<SmtpMessage> relevantMessages = null;

        for (final SmtpMessage msg : totalMessagesRecevied) {

            if (predicate.apply(msg)) {

                // we're interested in this message
                if (relevantMessages == null) {
                    relevantMessages = new ArrayList<SmtpMessage>();
                }
                relevantMessages.add(msg);
            }
        }
        return relevantMessages;

    }
}
