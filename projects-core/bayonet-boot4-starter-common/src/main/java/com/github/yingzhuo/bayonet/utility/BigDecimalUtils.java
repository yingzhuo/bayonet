package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collection;

import static org.springframework.util.NumberUtils.convertNumberToTargetClass;

/**
 * {@link BigDecimal} 运算工具类。
 *
 * <p>提供算术运算、极值、null 安全聚合及类型转换等便捷方法。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * BigDecimal sum = BigDecimalUtils.add(BigDecimal.TEN, 5);
 * BigDecimal quotient = BigDecimalUtils.divide(BigDecimal.TEN, 3);
 * BigDecimal max = BigDecimalUtils.max(BigDecimal.ONE, BigDecimal.TEN);
 * BigDecimal total = BigDecimalUtils.nullSafeAdd(a, b, c);
 * }</pre>
 *
 * @author 应卓
 * @see BigDecimal
 * @see RoundingMode
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BigDecimalUtils {

    /**
     * 绝对值。
     *
     * @param number 数值，不能为 {@code null}
     * @return 绝对值
     */
    public static BigDecimal abs(BigDecimal number) {
        Assert.notNull(number, "number is required");
        return number.abs();
    }

    /**
     * 加法。
     *
     * @param number1 被加数，不能为 {@code null}
     * @param number2 加数，不能为 {@code null}
     * @return 和
     */
    public static BigDecimal add(BigDecimal number1, Number number2) {
        Assert.notNull(number1, "number1 is required");
        Assert.notNull(number2, "number2 is required");
        return number1.add(convertNumberToTargetClass(number2, BigDecimal.class));
    }

    /**
     * 减法。
     *
     * @param number1 被减数，不能为 {@code null}
     * @param number2 减数，不能为 {@code null}
     * @return 差
     */
    public static BigDecimal subtract(BigDecimal number1, Number number2) {
        Assert.notNull(number1, "number1 is required");
        Assert.notNull(number2, "number2 is required");
        return number1.subtract(convertNumberToTargetClass(number2, BigDecimal.class));
    }

    /**
     * 乘法。
     *
     * @param number1 被乘数，不能为 {@code null}
     * @param number2 乘数，不能为 {@code null}
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal number1, Number number2) {
        Assert.notNull(number1, "number1 is required");
        Assert.notNull(number2, "number2 is required");
        return number1.multiply(convertNumberToTargetClass(number2, BigDecimal.class));
    }

    /**
     * 除法（默认精度 2，四舍五入）。
     *
     * @param number1 被除数，不能为 {@code null}
     * @param number2 除数，不能为 {@code null}
     * @return 商
     */
    public static BigDecimal divide(BigDecimal number1, Number number2) {
        return divide(number1, number2, 2, RoundingMode.HALF_UP);
    }

    /**
     * 除法（自定义精度和舍入模式）。
     *
     * @param number1     被除数，不能为 {@code null}
     * @param number2     除数，不能为 {@code null}
     * @param scale       精度
     * @param roundingMode 舍入模式，不能为 {@code null}
     * @return 商
     */
    public static BigDecimal divide(BigDecimal number1, Number number2, int scale, RoundingMode roundingMode) {
        Assert.notNull(number1, "number1 is required");
        Assert.notNull(number2, "number2 is required");
        Assert.notNull(roundingMode, "roundingMode is required");
        return number1.divide(convertNumberToTargetClass(number2, BigDecimal.class), scale, roundingMode);
    }

    /**
     * 乘方。
     *
     * @param number1 底数，不能为 {@code null}
     * @param n       指数
     * @return 幂
     */
    public static BigDecimal pow(BigDecimal number1, int n) {
        Assert.notNull(number1, "number1 is required");
        return number1.pow(n);
    }

    /**
     * 最小值（两数比较）。
     *
     * @param number1 数值 1，不能为 {@code null}
     * @param number2 数值 2，不能为 {@code null}
     * @return 较小值
     */
    public static BigDecimal min(BigDecimal number1, BigDecimal number2) {
        Assert.notNull(number1, "number1 is required");
        Assert.notNull(number2, "number2 is required");
        return number1.compareTo(number2) < 0 ? number1 : number2;
    }

    /**
     * 最小值（可变参数）。
     *
     * @param numbers 数值数组，不能为 {@code null} 或空，不能含 null 元素
     * @return 最小值
     */
    public static BigDecimal min(BigDecimal... numbers) {
        Assert.notNull(numbers, "numbers is required");
        Assert.notEmpty(numbers, "numbers is empty");
        Assert.noNullElements(numbers, "numbers has null element(s)");

        BigDecimal min = numbers[0];
        for (BigDecimal number : numbers) {
            min = min(min, number);
        }
        return min;
    }

    /**
     * 最小值（集合）。
     *
     * @param numbers 数值集合，不能为 {@code null}
     * @return 最小值
     */
    public static BigDecimal min(Collection<BigDecimal> numbers) {
        Assert.notNull(numbers, "numbers is null");
        BigDecimal min = null;
        for (BigDecimal number : numbers) {
            min = (min == null) ? number : min(min, number);
        }
        return min;
    }

    /**
     * 最大值（两数比较）。
     *
     * @param number1 数值 1，不能为 {@code null}
     * @param number2 数值 2，不能为 {@code null}
     * @return 较大值
     */
    public static BigDecimal max(BigDecimal number1, BigDecimal number2) {
        Assert.notNull(number1, "number1 is required");
        Assert.notNull(number2, "number2 is required");
        return number1.compareTo(number2) > 0 ? number1 : number2;
    }

    /**
     * 最大值（可变参数）。
     *
     * @param numbers 数值数组，不能为 {@code null} 或空，不能含 null 元素
     * @return 最大值
     */
    public static BigDecimal max(BigDecimal... numbers) {
        Assert.notNull(numbers, "numbers is required");
        Assert.notEmpty(numbers, "numbers is empty");
        Assert.noNullElements(numbers, "numbers has null element(s)");

        BigDecimal max = numbers[0];
        for (BigDecimal number : numbers) {
            max = max(max, number);
        }
        return max;
    }

    /**
     * 最大值（集合）。
     *
     * @param numbers 数值集合，不能为 {@code null}
     * @return 最大值
     */
    public static BigDecimal max(Collection<BigDecimal> numbers) {
        Assert.notNull(numbers, "numbers is required");
        BigDecimal max = null;
        for (BigDecimal number : numbers) {
            max = (max == null) ? number : max(max, number);
        }
        return max;
    }

    /**
     * null 安全加法（可变参数）。
     *
     * @param numbers 数值数组，可为 {@code null}，null 元素被忽略
     * @return 总和
     */
    public static BigDecimal nullSafeAdd(@Nullable BigDecimal... numbers) {
        BigDecimal result = BigDecimal.ZERO;
        if (numbers != null) {
            for (BigDecimal number : numbers) {
                if (number != null) {
                    result = result.add(number);
                }
            }
        }
        return result;
    }

    /**
     * null 安全加法（集合）。
     *
     * @param numbers 数值集合，可为 {@code null}，null 元素被忽略
     * @return 总和
     */
    public static BigDecimal nullSafeAdd(@Nullable Collection<BigDecimal> numbers) {
        if (numbers == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal number : numbers) {
            if (number != null) {
                result = result.add(number);
            }
        }
        return result;
    }

    /**
     * null 安全乘法（可变参数）。
     *
     * @param numbers 数值数组，可为 {@code null}，null 元素被忽略
     * @return 乘积
     */
    public static BigDecimal nullSafeMultiply(@Nullable BigDecimal... numbers) {
        BigDecimal result = BigDecimal.ONE;
        if (numbers != null) {
            for (BigDecimal number : numbers) {
                if (number != null) {
                    result = result.multiply(number);
                }
            }
        }
        return result;
    }

    /**
     * null 安全乘法（集合）。
     *
     * @param numbers 数值集合，可为 {@code null}，null 元素被忽略
     * @return 乘积
     */
    public static BigDecimal nullSafeMultiply(@Nullable Collection<BigDecimal> numbers) {
        if (numbers == null) {
            return BigDecimal.ONE;
        }
        BigDecimal result = BigDecimal.ONE;
        for (BigDecimal number : numbers) {
            if (number != null) {
                result = result.multiply(number);
            }
        }
        return result;
    }

    /**
     * 将 {@link BigDecimal} 转换为指定数值类型的值。
     *
     * @param number     {@link BigDecimal} 实例，不能为 {@code null}
     * @param numberType 目标类型，不能为 {@code null}
     * @param <T>        目标数值类型
     * @return 转换后的值
     * @throws IllegalArgumentException 不支持的数值类型时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T getValue(BigDecimal number, Class<T> numberType) {
        Assert.notNull(number, "number is required");
        Assert.notNull(numberType, "numberType is required");

        if (numberType == Byte.class) {
            return (T) Byte.valueOf(number.byteValue());
        }
        if (numberType == Short.class) {
            return (T) Short.valueOf(number.shortValue());
        }
        if (numberType == Integer.class) {
            return (T) Integer.valueOf(number.intValue());
        }
        if (numberType == Long.class) {
            return (T) Long.valueOf(number.longValue());
        }
        if (numberType == Float.class) {
            return (T) Float.valueOf(number.floatValue());
        }
        if (numberType == Double.class) {
            return (T) Double.valueOf(number.doubleValue());
        }
        if (numberType == BigInteger.class) {
            return (T) number.toBigInteger();
        }
        if (numberType == BigDecimal.class) {
            return (T) number;
        }

        throw new IllegalArgumentException(String.format("unsupported number type: %s", numberType.getName()));
    }

}
