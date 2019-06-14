package log;

import org.slf4j.LoggerFactory;

public final class Log {

    public interface ILogCategory {
    }

    public static void trace(ILogCategory category, String msg, Object arg1) {
        LoggerFactory.getLogger(category.toString()).trace(msg, arg1);
    }

    public static void trace(ILogCategory category, String msg, Object arg1, Object arg2) {
        LoggerFactory.getLogger(category.toString()).trace(msg, arg1, arg2);
    }

    public static void trace(ILogCategory category, String msg, Object... args) {
        LoggerFactory.getLogger(category.toString()).trace(msg, args);
    }

    public static void debug(ILogCategory category, String msg) {
        LoggerFactory.getLogger(category.toString()).debug(msg);
    }

    public static void debug(ILogCategory category, String msg, Object arg1) {
        LoggerFactory.getLogger(category.toString()).debug(msg, arg1);
    }

    public static void debug(ILogCategory category, String msg, Object arg1, Object arg2) {
        LoggerFactory.getLogger(category.toString()).debug(msg, arg1, arg2);
    }

    public static void debug(ILogCategory category, String msg, Object... args) {
        LoggerFactory.getLogger(category.toString()).debug(msg, args);
    }

    public static void info(ILogCategory category, String msg, Object arg1) {
        LoggerFactory.getLogger(category.toString()).info(msg, arg1);
    }

    public static void info(ILogCategory category, String msg, Object arg1, Object arg2) {
        LoggerFactory.getLogger(category.toString()).info(msg, arg1, arg2);
    }

    public static void info(ILogCategory category, String msg, Object... args) {
        LoggerFactory.getLogger(category.toString()).info(msg, args);
    }

    public static void warn(ILogCategory category, String msg) {
        LoggerFactory.getLogger(category.toString()).warn(msg);
    }

    public static void warn(ILogCategory category, String msg, Object arg1) {
        LoggerFactory.getLogger(category.toString()).warn(msg, arg1);
    }

    public static void warn(ILogCategory category, String msg, Object arg1, Object arg2) {
        LoggerFactory.getLogger(category.toString()).warn(msg, arg1, arg2);
    }

    public static void warn(ILogCategory category, String msg, Object... args) {
        LoggerFactory.getLogger(category.toString()).warn(msg, args);
    }

    public static void error(ILogCategory category, String msg) {
        LoggerFactory.getLogger(category.toString()).error(msg);
    }

    public static void error(ILogCategory category, String msg, Object arg1) {
        LoggerFactory.getLogger(category.toString()).error(msg, arg1);
    }

    public static void error(ILogCategory category, String msg, Object arg1, Object arg2) {
        LoggerFactory.getLogger(category.toString()).error(msg, arg1, arg2);
    }

    public static void error(ILogCategory category, String msg, Object... args) {
        LoggerFactory.getLogger(category.toString()).error(msg, args);
    }

}
