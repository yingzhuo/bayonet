package com.github.yingzhuo.bayonet.jdbc.datasource.dynamic;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

/**
 * 数据源切换 AOP 切面。
 *
 * <p>拦截所有标注了 {@link DataSourceSwitch @DataSourceSwitch} 的方法，
 * 在方法执行前通过 {@link DataSourceContextHolder} 设置数据源标识符，
 * 在方法执行完毕（含异常）后在 {@code finally} 块中清理上下文，
 * 防止 {@link ThreadLocal} 内存泄漏。</p>
 *
 * <p>使用 {@link Ordered#HIGHEST_PRECEDENCE 最高优先级} 执行，
 * 确保数据源在 {@code @Transactional} 等事务切面之前切换。</p>
 *
 * @author 应卓
 * @see DataSourceSwitch
 * @see DataSourceContextHolder
 * @see DynamicDataSource
 * @since 4.1.1
 */
@Aspect
public class DataSourceSwitchingAspect implements Ordered {

    /**
     * 环绕通知：切换数据源并执行目标方法。
     *
     * @param joinPoint 连接点
     * @param dsSwitch  数据源切换注解
     * @return 目标方法返回值
     * @throws Throwable 目标方法抛出的异常
     */
    @Around("@annotation(dsSwitch)")
    public Object around(ProceedingJoinPoint joinPoint, DataSourceSwitch dsSwitch) throws Throwable {
        DataSourceContextHolder.set(dsSwitch.value());
        try {
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }

    /**
     * 返回切面的执行优先级（最高优先级）。
     *
     * @return {@link Ordered#HIGHEST_PRECEDENCE}
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
