package com.github.yingzhuo.bayonet.security.authentication;

import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * 逗号分隔字符串 → {@link RichUserDetails} 的转换器。
 * <p>格式：{@code id,username,authority1,authority2,...,enabled}</p>
 * <ul>
 *   <li>第一个 token → id</li>
 *   <li>第二个 token → username</li>
 *   <li>中间的 token → 权限</li>
 *   <li>最后一个 token → 若可解析为 {@code true}/{@code false}/{@code yes}/{@code no} 则为是否启用，否则视为权限</li>
 * </ul>
 *
 * @author 应卓
 * @since 4.1.1
 */
public class RichUserDetailsConverter implements Converter<String, RichUserDetails> {

    @Override
    public RichUserDetails convert(String source) {
        Assert.hasText(source, "source must not be blank");
        source = StringUtils.trimAllWhitespace(source);

        var tokens = new ArrayList<String>();
        var stringTokenizer = new StringTokenizer(source, ",");
        while (stringTokenizer.hasMoreTokens()) {
            tokens.add(stringTokenizer.nextToken());
        }

        Assert.state(tokens.size() >= 2, "source must contain at least id and username");

        var id = tokens.get(0);
        var username = tokens.get(1);

        var builder = RichUserDetails.builder()
                .id(id)
                .username(username);

        for (int i = 2; i < tokens.size(); i++) {
            var token = tokens.get(i);
            if (i == tokens.size() - 1) {
                var parsedEnabled = parseEnabled(token);
                if (parsedEnabled != null) {
                    builder.enabled(parsedEnabled);
                } else {
                    builder.authorities(token);
                }
            } else {
                builder.authorities(token);
            }
        }

        return builder.build();
    }

    @Nullable
    private Boolean parseEnabled(String token) {
        return switch (token.toLowerCase(Locale.ROOT)) {
            case "true", "yes" -> Boolean.TRUE;
            case "false", "no" -> Boolean.FALSE;
            default -> null;
        };
    }
}
