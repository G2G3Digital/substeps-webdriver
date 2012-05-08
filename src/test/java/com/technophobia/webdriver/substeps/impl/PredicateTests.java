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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.technophobia.webdriver.util.RadioButtonPredicate;

/**
 * @author ian
 * 
 */
public class PredicateTests {

    @Test
    public void testRadioButtonPredicate() {
        final RadioButtonPredicate predicate = new RadioButtonPredicate("name", "value");

        WebElement elem = mock(WebElement.class);

        when(elem.getAttribute("value")).thenReturn("value");
        when(elem.getAttribute("name")).thenReturn("name");
        when(elem.getAttribute("type")).thenReturn("radio");

        Assert.assertTrue(predicate.apply(elem));

        when(elem.getAttribute("type")).thenReturn("text");
        Assert.assertFalse(predicate.apply(elem));

        final RadioButtonPredicate predicate2 = new RadioButtonPredicate();
        predicate2.setText("text");

        elem = mock(WebElement.class);

        when(elem.getAttribute("value")).thenReturn(null);
        when(elem.getAttribute("name")).thenReturn("name");
        when(elem.getAttribute("type")).thenReturn("radio");
        when(elem.getText()).thenReturn("text");

        Assert.assertTrue(predicate2.apply(elem));

        final RadioButtonPredicate predicate3 = new RadioButtonPredicate();
        predicate3.setText("fred");

        Assert.assertFalse(predicate3.apply(elem));

    }

}
