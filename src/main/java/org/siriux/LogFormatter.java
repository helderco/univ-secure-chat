
package org.siriux;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A formatter for JDK logs which produces a more concise log output
 * than the standard JDK setting.
 */
public class LogFormatter extends Formatter
{
    private final DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        String result = String.format( "%s %s [%d] [%s] %s\n",
              timeFormat.format(record.getMillis()),
              record.getLevel(),
              record.getThreadID(),
              record.getLoggerName(),
              record.getMessage()
        );

        Throwable t = record.getThrown();
        return t == null ? result : result + getStackTrace (t);
    }

    private String getStackTrace(Throwable t) {
        StringBuilder result = new StringBuilder();
        for (StackTraceElement ste : t.getStackTrace()) {
            result.append("    ");
            result.append(ste.toString());
            result.append("\n");
        }
        return result.toString();
    }
}
