package com.github.yingzhuo.bayonet.utility.collection;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollectionUtilsTest {

    // ============== size ==============

    @Test
    void should_return_size_for_collection() {
        assertThat(CollectionUtils.size(List.of("a", "b"))).isEqualTo(2);
    }

    @Test
    void should_return_zero_when_collection_is_null() {
        assertThat(CollectionUtils.size((Collection<?>) null)).isZero();
    }

    @Test
    void should_return_size_for_map() {
        assertThat(CollectionUtils.size(Map.of("k", "v"))).isOne();
    }

    @Test
    void should_return_zero_when_map_is_null() {
        assertThat(CollectionUtils.size((Map<?, ?>) null)).isZero();
    }

    // ============== isEmpty ==============

    @Test
    void should_return_true_when_collection_is_null() {
        assertThat(CollectionUtils.isEmpty((Collection<?>) null)).isTrue();
    }

    @Test
    void should_return_true_when_collection_is_empty() {
        assertThat(CollectionUtils.isEmpty(List.of())).isTrue();
    }

    @Test
    void should_return_false_when_collection_is_not_empty() {
        assertThat(CollectionUtils.isEmpty(List.of("a"))).isFalse();
    }

    @Test
    void should_return_true_when_map_is_null() {
        assertThat(CollectionUtils.isEmpty((Map<?, ?>) null)).isTrue();
    }

    @Test
    void should_return_true_when_map_is_empty() {
        assertThat(CollectionUtils.isEmpty(Map.of())).isTrue();
    }

    // ============== isNotEmpty ==============

    @Test
    void should_return_false_when_collection_is_null() {
        assertThat(CollectionUtils.isNotEmpty((Collection<?>) null)).isFalse();
    }

    @Test
    void should_return_true_when_collection_is_not_empty() {
        assertThat(CollectionUtils.isNotEmpty(List.of("a"))).isTrue();
    }

    @Test
    void should_return_false_when_map_is_null() {
        assertThat(CollectionUtils.isNotEmpty((Map<?, ?>) null)).isFalse();
    }

    @Test
    void should_return_true_when_map_is_not_empty() {
        assertThat(CollectionUtils.isNotEmpty(Map.of("k", "v"))).isTrue();
    }

    // ============== nullSafeAdd ==============

    @Test
    void should_add_element() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAdd(list, "a");
        assertThat(list).containsExactly("a");
    }

    @Test
    void should_skip_null_element() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAdd(list, null);
        assertThat(list).isEmpty();
    }

    @Test
    void should_throw_when_collection_is_null_in_nullSafeAdd() {
        assertThatThrownBy(() -> CollectionUtils.nullSafeAdd(null, "a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== nullSafeAddAll (T[]) ==============

    @Test
    void should_add_all_from_array() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAddAll(list, new String[]{"a", "b"});
        assertThat(list).containsExactly("a", "b");
    }

    @Test
    void should_skip_null_elements_in_array() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAddAll(list, new String[]{"a", null, "b"});
        assertThat(list).containsExactly("a", "b");
    }

    @Test
    void should_handle_null_array() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAddAll(list, (String[]) null);
        assertThat(list).isEmpty();
    }

    // ============== nullSafeAddAll (Collection) ==============

    @Test
    void should_add_all_from_collection() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAddAll(list, List.of("a", "b"));
        assertThat(list).containsExactly("a", "b");
    }

    @Test
    void should_skip_null_elements_in_collection() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAddAll(list, Arrays.asList("a", null, "b"));
        assertThat(list).containsExactly("a", "b");
    }

    @Test
    void should_handle_null_collection() {
        var list = new ArrayList<String>();
        CollectionUtils.nullSafeAddAll(list, (Collection<String>) null);
        assertThat(list).isEmpty();
    }

    // ============== nullSafeAddAll (Map) ==============

    @Test
    void should_put_all_from_map() {
        var map = new HashMap<String, String>();
        CollectionUtils.nullSafeAddAll(map, Map.of("k1", "v1", "k2", "v2"));
        assertThat(map).containsEntry("k1", "v1").containsEntry("k2", "v2");
    }

    @Test
    void should_handle_null_map() {
        var map = new HashMap<String, String>();
        CollectionUtils.nullSafeAddAll(map, (Map<String, String>) null);
        assertThat(map).isEmpty();
    }

    // ============== 参数验证 ==============

    @Test
    void should_throw_when_collection_is_null_in_nullSafeAddAll_array() {
        assertThatThrownBy(() -> CollectionUtils.nullSafeAddAll(null, new String[]{"a"}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_collection_is_null_in_nullSafeAddAll_collection() {
        assertThatThrownBy(() -> CollectionUtils.nullSafeAddAll(null, List.of("a")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_map_is_null_in_nullSafeAddAll_map() {
        assertThatThrownBy(() -> CollectionUtils.nullSafeAddAll(null, Map.of("k", "v")))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
