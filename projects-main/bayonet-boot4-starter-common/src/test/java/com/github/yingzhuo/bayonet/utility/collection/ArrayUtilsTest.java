package com.github.yingzhuo.bayonet.utility.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArrayUtilsTest {

    // ============== isEmpty ==============

    @Test
    void should_return_true_when_array_is_null() {
        assertThat(ArrayUtils.isEmpty(null)).isTrue();
    }

    @Test
    void should_return_true_when_array_is_empty() {
        assertThat(ArrayUtils.isEmpty(new String[0])).isTrue();
    }

    @Test
    void should_return_false_when_array_is_not_empty() {
        assertThat(ArrayUtils.isEmpty(new String[]{"a"})).isFalse();
    }

    // ============== isNotEmpty ==============

    @Test
    void should_return_false_when_array_is_null() {
        assertThat(ArrayUtils.isNotEmpty(null)).isFalse();
    }

    @Test
    void should_return_false_when_array_is_empty() {
        assertThat(ArrayUtils.isNotEmpty(new String[0])).isFalse();
    }

    @Test
    void should_return_true_when_array_is_not_empty() {
        assertThat(ArrayUtils.isNotEmpty(new String[]{"a"})).isTrue();
    }

    // ============== size ==============

    @Test
    void should_return_zero_when_array_is_null() {
        assertThat(ArrayUtils.size(null)).isZero();
    }

    @Test
    void should_return_zero_when_array_is_empty() {
        assertThat(ArrayUtils.size(new String[0])).isZero();
    }

    @Test
    void should_return_length() {
        assertThat(ArrayUtils.size(new String[]{"a", "b", "c"})).isEqualTo(3);
    }

    // ============== firstNonNull ==============

    @Test
    void should_return_null_when_array_is_null() {
        String[] input = null;
        assertThat(ArrayUtils.firstNonNull(input)).isNull();
    }

    @Test
    void should_return_null_when_array_is_empty() {
        var result = ArrayUtils.firstNonNull(new String[0]);
        assertThat(result).isNull();
    }

    @Test
    void should_return_first_non_null() {
        String result = ArrayUtils.firstNonNull(new String[]{null, "a", "b"});
        assertThat(result).isEqualTo("a");
    }

    @Test
    void should_return_null_when_all_null() {
        String result = ArrayUtils.firstNonNull(new String[]{null, null});
        assertThat(result).isNull();
    }

    @Test
    void should_return_first_element_when_no_nulls() {
        String result = ArrayUtils.firstNonNull(new String[]{"x", "y"});
        assertThat(result).isEqualTo("x");
    }

}
