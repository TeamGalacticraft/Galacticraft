/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Functionality annotated with ForRemoval will no longer be supported
 * and should not be used anymore in new code.
 */
@Documented
@Retention(RUNTIME)
@Target(
{TYPE, FIELD, METHOD, CONSTRUCTOR})
public @interface ForRemoval
{
    /**
     * Version which will most likely remove this feature.
     *
     * @return The deadline version or N/A if this isn't known yet
     */
    String deadline() default "N/A";
}
