package com.github.yingzhuo.bayonet.security.token;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompositeTokenResolverTest {

    @Mock
    private WebRequest webRequest;

    @Mock
    private TokenResolver first;

    @Mock
    private TokenResolver second;

    // ============== 构造器 ==============

    @Test
    void should_create_from_list() {
        assertThat(new CompositeTokenResolver(List.of(first, second))).isNotNull();
    }

    @Test
    void should_create_via_staticFactory() {
        assertThat(CompositeTokenResolver.of(first, second)).isNotNull();
    }

    @Test
    void should_throw_when_listIsNull() {
        assertThatThrownBy(() -> new CompositeTokenResolver((List<TokenResolver>) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_listContainsNull() {
        assertThatThrownBy(() -> new CompositeTokenResolver(Arrays.asList(first, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_notThrow_when_immutableList() {
        assertThat(new CompositeTokenResolver(List.of(first, second))).isNotNull();
    }

    // ============== resolve ==============

    @Test
    void should_return_firstMatch() {
        when(first.resolve(webRequest)).thenReturn("token-from-first");
        var resolver = new CompositeTokenResolver(List.of(first, second));
        assertThat(resolver.resolve(webRequest)).isEqualTo("token-from-first");
    }

    @Test
    void should_return_second_when_firstReturnsNull() {
        when(second.resolve(webRequest)).thenReturn("token-from-second");
        var resolver = new CompositeTokenResolver(List.of(first, second));
        assertThat(resolver.resolve(webRequest)).isEqualTo("token-from-second");
    }

    @Test
    void should_return_null_when_allReturnNull() {
        var resolver = new CompositeTokenResolver(List.of(first, second));
        assertThat(resolver.resolve(webRequest)).isNull();
    }

    @Test
    void should_return_null_when_emptyList() {
        var resolver = new CompositeTokenResolver(List.of());
        assertThat(resolver.resolve(webRequest)).isNull();
    }

    // ============== Order 排序 ==============

    @Test
    void should_respect_order_annotation() {
        var low = new LowPriorityResolver();
        var high = new HighPriorityResolver();
        // 按 Order(2), Order(1) 传入，预期 Order(1) 先执行
        var resolver = new CompositeTokenResolver(List.of(low, high));
        assertThat(resolver.resolve(webRequest)).isEqualTo("high");
    }

    @Order(2)
    static class LowPriorityResolver implements TokenResolver {
        @Override
        public @org.jspecify.annotations.Nullable String resolve(@NonNull WebRequest webRequest) {
            return "low";
        }
    }

    @Order(1)
    static class HighPriorityResolver implements TokenResolver {
        @Override
        public @org.jspecify.annotations.Nullable String resolve(@NonNull WebRequest webRequest) {
            return "high";
        }
    }

}
