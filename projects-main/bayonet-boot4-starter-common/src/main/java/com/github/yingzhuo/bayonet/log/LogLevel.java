package com.github.yingzhuo.bayonet.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志级别抽象。
 * <p>封装 SLF4J {@link Logger} 的日志方法，提供统一的级别判断和日志输出接口。</p>
 *
 * @author 应卓
 * @since 4.1.1
 */
public sealed interface LogLevel {

    /**
     * 创建 TRACE 级别。
     *
     * @param name 日志名称
     * @return TRACE 级别实例
     */
    static LogLevel trace(String name) {
        return trace(LoggerFactory.getLogger(name));
    }

    /**
     * 创建 TRACE 级别。
     *
     * @param clazz 日志来源类
     * @return TRACE 级别实例
     */
    static LogLevel trace(Class<?> clazz) {
        return trace(LoggerFactory.getLogger(clazz));
    }

    /**
     * 创建 TRACE 级别。
     *
     * @param logger SLF4J 日志对象
     * @return TRACE 级别实例
     */
    static LogLevel trace(Logger logger) {
        return new Trace(logger);
    }

    /**
     * 创建 DEBUG 级别。
     *
     * @param name 日志名称
     * @return DEBUG 级别实例
     */
    static LogLevel debug(String name) {
        return debug(LoggerFactory.getLogger(name));
    }

    /**
     * 创建 DEBUG 级别。
     *
     * @param clazz 日志来源类
     * @return DEBUG 级别实例
     */
    static LogLevel debug(Class<?> clazz) {
        return debug(LoggerFactory.getLogger(clazz));
    }

    /**
     * 创建 DEBUG 级别。
     *
     * @param logger SLF4J 日志对象
     * @return DEBUG 级别实例
     */
    static LogLevel debug(Logger logger) {
        return new Debug(logger);
    }

    /**
     * 创建 INFO 级别。
     *
     * @param name 日志名称
     * @return INFO 级别实例
     */
    static LogLevel info(String name) {
        return info(LoggerFactory.getLogger(name));
    }

    /**
     * 创建 INFO 级别。
     *
     * @param clazz 日志来源类
     * @return INFO 级别实例
     */
    static LogLevel info(Class<?> clazz) {
        return info(LoggerFactory.getLogger(clazz));
    }

    /**
     * 创建 INFO 级别。
     *
     * @param logger SLF4J 日志对象
     * @return INFO 级别实例
     */
    static LogLevel info(Logger logger) {
        return new Info(logger);
    }

    /**
     * 创建 WARNING 级别。
     *
     * @param name 日志名称
     * @return WARNING 级别实例
     */
    static LogLevel warning(String name) {
        return warning(LoggerFactory.getLogger(name));
    }

    /**
     * 创建 WARNING 级别。
     *
     * @param clazz 日志来源类
     * @return WARNING 级别实例
     */
    static LogLevel warning(Class<?> clazz) {
        return warning(LoggerFactory.getLogger(clazz));
    }

    /**
     * 创建 WARNING 级别。
     *
     * @param logger SLF4J 日志对象
     * @return WARNING 级别实例
     */
    static LogLevel warning(Logger logger) {
        return new Warning(logger);
    }

    /**
     * 创建 ERROR 级别。
     *
     * @param name 日志名称
     * @return ERROR 级别实例
     */
    static LogLevel error(String name) {
        return error(LoggerFactory.getLogger(name));
    }

    /**
     * 创建 ERROR 级别。
     *
     * @param clazz 日志来源类
     * @return ERROR 级别实例
     */
    static LogLevel error(Class<?> clazz) {
        return error(LoggerFactory.getLogger(clazz));
    }

    /**
     * 创建 ERROR 级别。
     *
     * @param logger SLF4J 日志对象
     * @return ERROR 级别实例
     */
    static LogLevel error(Logger logger) {
        return new Error(logger);
    }

    /**
     * 判断当前级别是否启用。
     *
     * @return 启用返回 {@code true}
     */
    boolean isEnabled();

    /**
     * 输出日志。
     *
     * @param format 格式化字符串
     * @param args   参数
     */
    void log(String format, Object... args);
}

// 实现:

record Trace(Logger logger) implements LogLevel {

    public boolean isEnabled() {
        return logger.isTraceEnabled();
    }

    public void log(String format, Object... args) {
        logger.trace(format, args);
    }
}

record Debug(Logger logger) implements LogLevel {

    public boolean isEnabled() {
        return logger.isDebugEnabled();
    }

    public void log(String format, Object... args) {
        logger.debug(format, args);
    }
}

record Info(Logger logger) implements LogLevel {

    public boolean isEnabled() {
        return logger.isInfoEnabled();
    }

    public void log(String format, Object... args) {
        logger.info(format, args);
    }
}

record Warning(Logger logger) implements LogLevel {

    public boolean isEnabled() {
        return logger.isWarnEnabled();
    }

    public void log(String format, Object... args) {
        logger.warn(format, args);
    }
}

record Error(Logger logger) implements LogLevel {

    public boolean isEnabled() {
        return logger.isErrorEnabled();
    }

    public void log(String format, Object... args) {
        logger.error(format, args);
    }
}