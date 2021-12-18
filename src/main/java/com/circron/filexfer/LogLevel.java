package com.circron.filexfer;

import org.apache.logging.log4j.Level;

public final class LogLevel {
    static Level logLevel;

    private LogLevel() {
        logLevel = Level.ALL;
    }

    public static Level setLogLevel(String level) {
        logLevel = Level.getLevel(level);
        return logLevel;
    }

    public static Level getLogLevel() {
        return logLevel;
    }

}
