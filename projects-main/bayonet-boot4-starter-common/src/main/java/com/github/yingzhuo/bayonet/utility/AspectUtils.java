package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * AOP 工具类，提供从 {@link JoinPoint} 中提取方法、目标对象等信息的静态方法。
 *
 * @author 应卓
 * @since 4.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AspectUtils {

    /**
     * 从连接点中获取被拦截的方法。
     *
     * @param joinPoint 连接点，不可为 {@code null}
     * @return 被拦截的 {@link Method}
     * @throws IllegalArgumentException {@code joinPoint} 为 {@code null} 时抛出
     */
    public static Method getJoinPointMethod(JoinPoint joinPoint) {
        Assert.notNull(joinPoint, "joinPoint is required");
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    /**
     * 从连接点中获取被拦截方法上的指定类型注解。
     * <p>内部使用 {@link AnnotationUtils#findAnnotation(Method, Class)}，
     * 会向上搜索父类及接口层级。</p>
     *
     * @param joinPoint      连接点，不可为 {@code null}
     * @param annotationType 注解类型，不可为 {@code null}
     * @param <A>            注解类型
     * @return 注解实例，若未找到则返回 {@code null}
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     * @deprecated 使用 {@link #resolvePointCutAnnotation(ProceedingJoinPoint, Class, boolean)} 代替
     */
    @Nullable
    @Deprecated(forRemoval = true)
    public static <A extends Annotation> A getJoinPointMethodAnnotation(JoinPoint joinPoint, Class<A> annotationType) {
        Assert.notNull(joinPoint, "joinPoint is required");
        Assert.notNull(annotationType, "annotationType is required");
        return AnnotationUtils.findAnnotation(getJoinPointMethod(joinPoint), annotationType);
    }

    /**
     * 判断被拦截方法上是否存在指定类型的注解。
     * <p>内部使用 {@link AnnotationUtils#findAnnotation(Method, Class)}，
     * 会向上搜索父类及接口层级。</p>
     *
     * @param joinPoint      连接点，不可为 {@code null}
     * @param annotationType 注解类型，不可为 {@code null}
     * @param <A>            注解类型
     * @return 存在返回 {@code true}，否则 {@code false}
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     * @deprecated 使用 {@link #resolvePointCutAnnotation(ProceedingJoinPoint, Class, boolean)} 代替
     */
    @Deprecated
    public static <A extends Annotation> boolean hasJoinPointMethodAnnotation(JoinPoint joinPoint, Class<A> annotationType) {
        return getJoinPointMethodAnnotation(joinPoint, annotationType) != null;
    }

    /**
     * 从连接点中解析指定类型的注解。
     * <p>优先级：方法级 &gt; 类级。默认包含类级查找，等价于 {@code resolvePointCutAnnotation(joinPoint, annotationType, true)}。</p>
     *
     * @param joinPoint      连接点，不可为 {@code null}
     * @param annotationType 注解类型，不可为 {@code null}
     * @param <A>            注解类型
     * @return 注解实例，若未找到则返回 {@code null}
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     */
    @Nullable
    public static <A extends Annotation> A resolvePointCutAnnotation(ProceedingJoinPoint joinPoint, Class<A> annotationType) {
        return resolvePointCutAnnotation(joinPoint, annotationType, true);
    }

    /**
     * 从连接点中解析指定类型的注解。
     * <p>优先级：方法级 &gt; 类级。当 {@code includeClassLevel} 为 {@code true} 时，
     * 若方法上无注解则回退到目标类上查找；否则仅查找方法级。</p>
     *
     * @param joinPoint         连接点，不可为 {@code null}
     * @param annotationType    注解类型，不可为 {@code null}
     * @param includeClassLevel 是否包含类级查找
     * @param <A>               注解类型
     * @return 注解实例，若未找到则返回 {@code null}
     * @throws IllegalArgumentException 任一参数为 {@code null} 时抛出
     */
    @Nullable
    public static <A extends Annotation> A resolvePointCutAnnotation(ProceedingJoinPoint joinPoint, Class<A> annotationType, boolean includeClassLevel) {
        Assert.notNull(joinPoint, "joinPoint is required");
        Assert.notNull(annotationType, "annotationType is required");

        var method = getJoinPointMethod(joinPoint);
        A annotation = AnnotationUtils.findAnnotation(method, annotationType);
        if (annotation != null) {
            return annotation;
        }

        if (includeClassLevel) {
            var clazz = joinPoint.getTarget().getClass();
            annotation = AnnotationUtils.findAnnotation(clazz, annotationType);
        }

        return annotation;
    }
}
