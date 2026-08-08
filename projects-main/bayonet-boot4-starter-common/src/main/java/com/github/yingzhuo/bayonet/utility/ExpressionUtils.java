package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * SpEL 表达式求值工具类
 *
 * <pre>{@code
 * var city = ExpressionUtils.evaluateValue(user, "address.city");
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpressionUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 以指定根对象求值 SpEL 表达式。
     *
     * @param rootObj    求值根对象（非 {@code null}）
     * @param expression SpEL 表达式（非 {@code null}）
     * @param <T>        期望的返回值类型
     * @return 求值结果；若表达式求值结果为 {@code null} 则返回 {@code null}
     * @throws org.springframework.expression.ExpressionException 表达式非法或求值失败时抛出
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T evaluateValue(Object rootObj, String expression) {
        Assert.notNull(rootObj, "rootObj must not be null");
        Assert.hasText(expression, "expression must not be empty");
        var ctx = new StandardEvaluationContext(rootObj);
        var exp = PARSER.parseExpression(expression);
        return (T) exp.getValue(ctx);
    }

    /**
     * 以指定根对象求值 SpEL 表达式，并要求求值结果非 {@code null}。
     * <p>当表达式求值结果为 {@code null} 时抛出 {@link IllegalStateException}，
     * 适用于期望表达式必有结果（如必须存在的属性）的场景。</p>
     *
     * @param rootObj    求值根对象（非 {@code null}）
     * @param expression SpEL 表达式（非 {@code null}）
     * @param <T>        期望的返回值类型
     * @return 求值结果（非 {@code null}）
     * @throws org.springframework.expression.ExpressionException 表达式非法或求值失败时抛出
     * @throws IllegalStateException                              求值结果为 {@code null} 时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T> T evaluateRequiredValue(Object rootObj, String expression) {
        var value = evaluateValue(rootObj, expression);
        Assert.state(value != null, "expression must not be null");
        return (T) value;
    }

}
