package com.github.yingzhuo.bayonet.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 数字字符串解析工具类。
 *
 * <p>将字符串解析为指定类型的数值，支持十进制、十六进制（{@code #}、{@code 0x} 前缀）和科学计数法格式。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * int i = NumberParsingUtils.parse("42", Integer.class);
 * long l = NumberParsingUtils.parse("0xFF", Long.class);
 * BigDecimal d = NumberParsingUtils.parse("1.23E5", BigDecimal.class);
 * }</pre>
 *
 * @author 应卓
 * @see NumberUtils
 * @see BigDecimalUtils
 * @since 4.1.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberParsingUtils {

    /**
     * 将字符串解析为指定类型的数值。
     *
     * @param text         待解析的字符串，不能为 {@code null}
     * @param typeOfNumber 目标数值类型，不能为 {@code null}
     * @param <T>          目标数值类型
     * @return 解析后的数值
     * @throws IllegalArgumentException 字符串格式无效或类型不支持时抛出
     */
    public static <T extends Number> T parse(String text, Class<T> typeOfNumber) {
        Assert.notNull(text, "text is required");
        Assert.notNull(typeOfNumber, "type is required");

        text = text.replaceAll("[\\s,]", "");

        // 十六进制数特殊处理
        if (text.startsWith("#") || text.startsWith("-#") || text.startsWith("0x") || text.startsWith("0X")
                || text.startsWith("-0x") || text.startsWith("-0X")) {
            final BigInteger bigInteger = NumberUtils.parseNumber(text, BigInteger.class);
            return BigDecimalUtils.getValue(new BigDecimal(bigInteger), typeOfNumber);
        }

        // 科学计数法特殊处理
        if (text.contains("E") || text.contains("e")) {
            final BigDecimal bigDecimal = NumberUtils.parseNumber(text, BigDecimal.class);
            return BigDecimalUtils.getValue(bigDecimal, typeOfNumber);
        }

        try {
            return NumberUtils.parseNumber(text, typeOfNumber);
        } catch (IllegalArgumentException e) {
            return fallback(text, typeOfNumber);
        }
    }

    // ------

    private static <T extends Number> T fallback(String text, Class<T> type) {
        BigDecimal big;

        try {
            big = new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(getErrorMessage(text));
        }

        return BigDecimalUtils.getValue(big, type);
    }

    private static String getErrorMessage(String text) {
        return String.format("\"%s\" is not a valid number", text);
    }

}
