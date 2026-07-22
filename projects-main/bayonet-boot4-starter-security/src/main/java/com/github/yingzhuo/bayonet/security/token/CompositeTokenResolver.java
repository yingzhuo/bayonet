package com.github.yingzhuo.bayonet.security.token;

import com.github.yingzhuo.bayonet.utility.collection.ArrayUtils;
import com.github.yingzhuo.bayonet.utility.collection.SortingUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * 组合 Token 解析器。
 * <p>按 {@link Order @Order} 或 {@link Ordered} 排序后，
 * 依次调用子解析器，返回第一个非 null 结果。若全部返回 null 则返回 null。</p>
 *
 * <pre>{@code
 * var resolver = CompositeTokenResolver.of(
 *     new BearerHeaderTokenResolver(),
 *     new HttpHeaderTokenResolver("X-Auth-Token")
 * );
 * String token = resolver.resolve(webRequest);
 * }</pre>
 *
 * @author 应卓
 * @since 4.1.0
 */
public record CompositeTokenResolver(Collection<TokenResolver> resolvers) implements TokenResolver {

    /**
     * 构造器（从Collection）
     *
     * @param resolvers 子解析器列表，不能为 {@code null}，不能包含 {@code null} 元素
     * @throws IllegalArgumentException 若 {@code resolvers} 为 {@code null} 或包含 {@code null}
     */
    public CompositeTokenResolver(Collection<TokenResolver> resolvers) {
        Assert.notNull(resolvers, "resolvers must not be null");
        Assert.noNullElements(resolvers, "resolvers must not contain null elements");

        var sorted = new ArrayList<>(resolvers);
        SortingUtils.sort(sorted);
        this.resolvers = Collections.unmodifiableList(sorted);
    }

    /**
     * 创建组合解析器（变参便捷方法）
     *
     * @param resolvers 子解析器列表
     * @return 组合解析器
     */
    public static TokenResolver of(TokenResolver... resolvers) {
        if (ArrayUtils.isEmpty(resolvers)) {
            return request -> null;
        }
        if (ArrayUtils.size(resolvers) == 1) {
            return resolvers[0];
        }
        return new CompositeTokenResolver(Arrays.asList(resolvers));
    }

    @Override
    public @Nullable String resolve(WebRequest webRequest) {
        for (var resolver : resolvers) {
            var token = resolver.resolve(webRequest);
            if (token != null) {
                return token;
            }
        }
        return null;
    }
}
