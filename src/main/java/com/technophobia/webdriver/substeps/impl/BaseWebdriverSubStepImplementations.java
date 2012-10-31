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

import com.technophobia.substeps.model.SubSteps.AdditionalStepImplementations;
import com.technophobia.substeps.model.SubSteps.StepImplementations;
import com.technophobia.webdriver.substeps.runner.DefaultExecutionSetupTearDown;

/**
 * Class containing the base set of substep impls. This class will be generic
 * across projects, project specific functionality should live in a separate
 * StepImplementations class
 * 
 * @author imoore
 * 
 */
@StepImplementations(requiredInitialisationClasses = DefaultExecutionSetupTearDown.class)
@AdditionalStepImplementations({ ActionWebDriverSubStepImplementations.class,
        AssertionWebDriverSubStepImplementations.class,
        FinderWebDriverSubStepImplementations.class,
        FormWebDriverSubStepImplementations.class,
        StartupWebDriverSubStepImplementations.class,
        TableSubStepImplementations.class })
public class BaseWebdriverSubStepImplementations {
}
