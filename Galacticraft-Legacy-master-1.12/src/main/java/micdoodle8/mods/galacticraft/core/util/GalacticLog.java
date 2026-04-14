/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
//import lombok.Getter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GalacticLog
{

    @Getter
    private final Logger logger;

    public GalacticLog(Object modClass)
    {
        this.logger = LogManager.getLogger(modClass.getClass().getSimpleName());
    }

    public void catching(Throwable t)
    {
        this.logger.catching(t);
    }

    public void debug(String msg, Object... params)
    {
        if (ConfigManagerCore.enableDebug || GCCoreUtil.isDeobfuscated())
        {
            this.logger.info("[GCDebug] " + msg, params);
        }
    }

    public void error(String msg, Object... params)
    {
        this.logger.error(msg, params);
    }
    
    public void error(Throwable t, String msg)
    {
        this.logger.error(msg, t);
    }

    public void fatal(String msg, Object... params)
    {
        this.logger.fatal(msg, params);
    }

    public void info(String msg, Object... params)
    {
        this.logger.info(msg, params);
    }

    public void log(Level level, String msg, Object... params)
    {
        this.logger.log(level, msg, params);
    }

    public void trace(String msg, Object... params)
    {
        this.logger.trace(msg, params);
    }

    public void warn(String msg, Object... params)
    {
        this.logger.warn(msg, params);
    }

    public void warn(Throwable t, String msg, Object... params)
    {
        this.logger.warn(msg, params);
        this.logger.catching(t);
    }

    public void noticableWarning(String... strings)
    {
        noticableWarning(true, ImmutableList.copyOf(strings));
    }
    
    public void noticableWarning(Throwable t, String... strings)
    {
        noticableWarning(true, ImmutableList.copyOf(strings), t.getStackTrace());
    }

    public void noticableWarningNoTrace(String... strings)
    {
        noticableWarning(false, ImmutableList.copyOf(strings));
    }

    private void noticableWarning(boolean trace, List<String> lines)
    {
        this.noticableWarning(trace, lines, Thread.currentThread().getStackTrace());
    }
    
    private void noticableWarning(boolean trace, List<String> lines, StackTraceElement[] elements)
    {
        this.error("********************************************************************************");
        for (final String line : lines)
        {
            for (final String subline : wrapString(line, 78, false, new ArrayList<>()))
            {
                this.error("* " + subline);
            }
        }
        if (trace)
        {
            final StackTraceElement[] stackTrace = elements;
            for (int i = 2; i < 8 && i < stackTrace.length; i++)
            {
                this.warn("*  at {}{}", stackTrace[i].toString(), i == 7 ? "..." : "");
            }
        }
        this.error("********************************************************************************");
    }

    private static List<String> wrapString(String string, int lnLength, boolean wrapLongWords, List<String> list)
    {
        final String lines[] = WordUtils.wrap(string, lnLength, null, wrapLongWords).split(SystemUtils.LINE_SEPARATOR);
        Collections.addAll(list, lines);
        return list;
    }
}
