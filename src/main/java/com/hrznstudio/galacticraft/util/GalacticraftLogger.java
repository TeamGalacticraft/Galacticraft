/*
 * Copyright (c) 2019 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftLogger {
    private static final Logger logger = LogManager.getLogger("Galacticraft");

    public static void info(CharSequence message) {
        logger.info("[Galacticraft] " + message);
    }

    public static void info(CharSequence message, Object... params) {
        logger.info("[Galacticraft] " + message.toString(), params);
    }

    public static void info(CharSequence message, Throwable t) {
        logger.info("[Galacticraft] " + message, t);
    }

    public static void warn(CharSequence message) {
        logger.warn("[Galacticraft] " + message);
    }

    public static void warn(CharSequence message, Object... params) {
        logger.warn("[Galacticraft] " + message.toString(), params);
    }

    public static void warn(CharSequence message, Throwable t) {
        logger.warn("[Galacticraft] " + message, t);
    }

    public static void warn(Throwable t) {
        logger.warn("[Galacticraft] " + t);
    }

    public static void error(CharSequence message) {
        logger.error("[Galacticraft] " + message);
    }

    public static void error(CharSequence message, Object... params) {
        logger.error("[Galacticraft] " + message.toString(), params);
    }

    public static void error(CharSequence message, Throwable t) {
        logger.error("[Galacticraft] " + message, t);
    }

    public static void error(Throwable t) {
        logger.error("[Galacticraft] " + t);
    }

    public static void fatal(CharSequence message) {
        logger.fatal("[Galacticraft] " + message);
    }

    public static void fatal(CharSequence message, Object... params) {
        logger.fatal("[Galacticraft] " + message.toString(), params);
    }

    public static void fatal(CharSequence message, Throwable t) {
        logger.fatal("[Galacticraft] " + message, t);
    }

    public static void fatal(Throwable t) {
        logger.fatal("[Galacticraft] " + t);
    }


}
