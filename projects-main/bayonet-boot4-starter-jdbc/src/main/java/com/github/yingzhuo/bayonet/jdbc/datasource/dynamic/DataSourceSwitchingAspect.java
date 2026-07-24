package com.github.yingzhuo.bayonet.jdbc.datasource.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 数据源切换 AOP 切面。
 *
 * <p>拦截所有标注了 {@link DataSourceSwitch @DataSourceSwitch} 的方法或类，
 * 在方法执行前通过 {@link DataSourceContextHolder} 设置数据源标识符，
 * 在方法执行完毕（含异常）后在 {@code finally} 块中清理上下文，
 * 防止 {@link ThreadLocal} 内存泄漏。</p>
 *
 * <p>注解查找优先级：方法级 &gt; 类级。即先查找方法上的注解，若不存在则查找类上的注解。</p>
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
@Slf4j
@Aspect
public class DataSourceSwitchingAspect implements Ordered {

    /**
     * 拦截标注了 {@link DataSourceSwitch} 的方法或类。
     */
    @Pointcut("@annotation(com.github.yingzhuo.bayonet.jdbc.datasource.dynamic.DataSourceSwitch) || " +
            "@within(com.github.yingzhuo.bayonet.jdbc.datasource.dynamic.DataSourceSwitch)")
    private void pc() {
    }

    /**
     * 环绕通知：切换数据源并执行目标方法。
     * <p>先查找方法上的 {@link DataSourceSwitch}，不存在时回退到类上查找。</p>
     *
     * @param joinPoint 连接点
     * @return 目标方法返回值
     * @throws Throwable 目标方法抛出的异常
     */
    @Around("pc()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        var dsSwitch = resolveDataSourceSwitch(joinPoint);
        if (dsSwitch == null) {
            return joinPoint.proceed();
        }

        var datasource = dsSwitch.value();
        if (log.isDebugEnabled()) {
            log.debug("Switching to datasource '{}' for method '{}#{}'",
                    datasource, joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
        }

        DataSourceContextHolder.set(datasource);
        try {
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clear();
            if (log.isDebugEnabled()) {
                log.debug("Cleared datasource context for method '{}#{}'",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName());
            }
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

    // ------

    @Nullable
    private static DataSourceSwitch resolveDataSourceSwitch(ProceedingJoinPoint joinPoint) {
        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 方法级别优先
        var dsSwitch = AnnotationUtils.findAnnotation(method, DataSourceSwitch.class);
        if (dsSwitch != null) {
            return dsSwitch;
        }

        // 类级别回退
        return AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), DataSourceSwitch.class);
    }
}
