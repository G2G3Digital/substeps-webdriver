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

public class InputPredicate implements WebElementPredicate {
    private final String name;
    private final String type;


    public InputPredicate(final String type, final String name) {
        this.name = name;
        this.type = type;
    }


    public boolean apply(final WebElement elem) {
        // is this the right type and name ?
        final String elemName = elem.getAttribute("name");
        if (elemName == null) {
            return false;
        }
        final String elemType = elem.getAttribute("type");
        if (elemType == null) {
            return false;
        }
        return name.compareToIgnoreCase(elemName) == 0 && type.compareToIgnoreCase(elemType) == 0;
    }


    public String getTagname() {
        return "input";
    }


    public String getDescription() {
        return "input type [" + type + "] name[" + name + "]";
    }

}
