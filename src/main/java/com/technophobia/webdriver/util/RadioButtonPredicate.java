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
package com.technophobia.webdriver.util;

import org.openqa.selenium.WebElement;

public class RadioButtonPredicate implements WebElementPredicate {
    private String value = null;
    private String name = null;
    private String text = null;


    public RadioButtonPredicate() {

    }


    public RadioButtonPredicate(final String value) {
        this.value = value;
        name = null;
    }


    public RadioButtonPredicate(final String name, final String value) {
        this.value = value;
        this.name = name;
    }


    public boolean apply(final WebElement elem) {
        return

        "radio".compareToIgnoreCase(elem.getAttribute("type")) == 0
                && checkAttributeIfPresent(elem, "value", value)
                && checkAttributeIfPresent(elem, "name", name) &&

                (text == null || text.compareToIgnoreCase(elem.getText()) == 0);

        // return (this.value != null &&
        //
        // this.value.compareToIgnoreCase(elem.getAttribute("value"))==0 ||
        // this.value == null) &&
        // (this.name != null &&
        // this.name.compareToIgnoreCase(elem.getAttribute("name"))==0 ||
        // this.name== null);
    }


    private boolean checkAttributeIfPresent(final WebElement elem, final String attributeName,
            final String attributeValue) {
        return attributeValue == null
                || attributeValue.compareToIgnoreCase(elem.getAttribute(attributeName)) == 0;
    }


    public String getTagname() {
        return "input";
    }


    public String getDescription() {
        return "Radio button name[" + name + "] value [" + value + "]";
    }


    /**
     * @return the text
     */
    public String getText() {
        return text;
    }


    /**
     * @param text
     *            the text to set
     */
    public void setText(final String text) {
        this.text = text;
    }


    /**
     * @param name2
     */
    public void setName(final String name) {
        this.name = name;
    }
}