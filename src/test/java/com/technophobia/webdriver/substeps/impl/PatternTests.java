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

import static org.hamcrest.CoreMatchers.is;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO
 * 
 * @author imoore
 * 
 */
public class PatternTests {
    @Test
    public void testPattern() {
    	
    	// TODO - use reflection to get hold of the string from the method
        final Pattern p = Pattern
                .compile("FindByTagAndAttributes tag=\"?([^\"]*)\"? attributes=\\[(.*)\\]");

        final String input = "FindByTagAndAttributes tag=\"input\" attributes=[type=submit,value=\"Search\"]";

        final Matcher matcher1 = p.matcher(input);
        final Matcher matcher = p.matcher(input);
        Assert.assertTrue(matcher1.matches());

        final int groupCount = matcher.groupCount();

        Assert.assertThat(groupCount, is(2));

        if (matcher.find()) {
            Assert.assertThat(matcher.group(1), is("input"));
            Assert.assertThat(matcher.group(2), is("type=submit,value=\"Search\""));
        } else {
            Assert.fail("should have found");
        }
    }


    @Test
    public void replacementTest() {
        String src = "AssertTagElementContainsText tag=\"([^\"]*)\" text=\"([^\"]*)\"";

        final String[] replacements = new String[] { "tag", "text" };

        for (final String s : replacements) {
            src = src.replaceFirst("\\([^\\)]*\\)", "<" + s + ">");
        }

        // System.out.println(src);
        final String desired = "AssertTagElementContainsText tag=\"<tag>\" text=\"<text>\"";

        // System.out.println(desired);

        Assert.assertThat(src, is(desired));
    }

}
