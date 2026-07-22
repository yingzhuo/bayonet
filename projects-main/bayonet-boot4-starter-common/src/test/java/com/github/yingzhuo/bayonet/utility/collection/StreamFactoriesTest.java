package com.github.yingzhuo.bayonet.utility.collection;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class StreamFactoriesTest {

    // ============== nullSafeNewStream (T[]) ==============

    @Test
    void should_create_stream_from_array() {
        assertThat(StreamFactories.nullSafeNewStream("a", "b", "c"))
                .containsExactly("a", "b", "c");
    }

    @Test
    void should_filter_null_from_array() {
        assertThat(StreamFactories.nullSafeNewStream("a", null, "b"))
                .containsExactly("a", "b");
    }

    @Test
    void should_return_empty_stream_for_null_array() {
        assertThat(StreamFactories.nullSafeNewStream((Object[]) null))
                .isEmpty();
    }

    // ============== nullSafeNewStream (Collection) ==============

    @Test
    void should_create_stream_from_collection() {
        assertThat(StreamFactories.nullSafeNewStream(List.of("a", "b")))
                .containsExactly("a", "b");
    }

    @Test
    void should_filter_null_from_collection() {
        var list = Arrays.asList("a", null, "b");
        assertThat(StreamFactories.nullSafeNewStream(list))
                .containsExactly("a", "b");
    }

    @Test
    void should_return_empty_stream_for_null_collection() {
        assertThat(StreamFactories.nullSafeNewStream((Collection<?>) null))
                .isEmpty();
    }

    // ============== of(Iterator) ==============

    @Test
    void should_create_stream_from_iterator() {
        assertThat(StreamFactories.of(List.of("a", "b").iterator()))
                .containsExactly("a", "b");
    }

    @Test
    void should_return_empty_stream_for_null_iterator() {
        assertThat(StreamFactories.of((Iterator<?>) null))
                .isEmpty();
    }

    // ============== of(Enumeration) ==============

    @Test
    void should_create_stream_from_enumeration() {
        var enumeration = Collections.enumeration(List.of("a", "b"));
        assertThat(StreamFactories.of(enumeration))
                .containsExactly("a", "b");
    }

    @Test
    void should_return_empty_stream_for_null_enumeration() {
        assertThat(StreamFactories.of((Enumeration<?>) null))
                .isEmpty();
    }

    // ============== parallel ==============

    @Test
    void should_create_parallel_stream_from_iterator() {
        try (var stream = StreamFactories.of(List.of("a", "b").iterator(), true)) {
            assertThat(stream.isParallel()).isTrue();
        }
    }

    @Test
    void should_create_parallel_stream_from_enumeration() {
        var enumeration = Collections.enumeration(List.of("a", "b"));
        try (var stream = StreamFactories.of(enumeration, true)) {
            assertThat(stream.isParallel()).isTrue();
        }
    }

}
