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
public sealed interface LogLevel permits
        LogLevel.Trace,
        LogLevel.Debug,
        LogLevel.Info,
        LogLevel.Warning,
        LogLevel.Error {

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

    /**
     * TRACE 级别。
     *
     * @param logger SLF4J 日志对象
     */
    record Trace(Logger logger) implements LogLevel {

        @Override
        public boolean isEnabled() {
            return logger.isTraceEnabled();
        }

        @Override
        public void log(String format, Object... args) {
            logger.trace(format, args);
        }
    }

    /**
     * DEBUG 级别。
     *
     * @param logger SLF4J 日志对象
     */
    record Debug(Logger logger) implements LogLevel {

        @Override
        public boolean isEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public void log(String format, Object... args) {
            logger.debug(format, args);
        }
    }

    /**
     * INFO 级别。
     *
     * @param logger SLF4J 日志对象
     */
    record Info(Logger logger) implements LogLevel {

        @Override
        public boolean isEnabled() {
            return logger.isInfoEnabled();
        }

        @Override
        public void log(String format, Object... args) {
            logger.info(format, args);
        }
    }

    /**
     * WARNING 级别。
     *
     * @param logger SLF4J 日志对象
     */
    record Warning(Logger logger) implements LogLevel {

        @Override
        public boolean isEnabled() {
            return logger.isWarnEnabled();
        }

        @Override
        public void log(String format, Object... args) {
            logger.warn(format, args);
        }
    }

    /**
     * ERROR 级别。
     *
     * @param logger SLF4J 日志对象
     */
    record Error(Logger logger) implements LogLevel {

        @Override
        public boolean isEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public void log(String format, Object... args) {
            logger.error(format, args);
        }
    }
}
