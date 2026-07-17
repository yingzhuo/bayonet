package com.github.yingzhuo.bayonet.utility.collection;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortingUtilsTest {

    @Test
    void should_sort_array_with_default_comparator() {
        var list = new Low[]{new Low("a"), new Low("b")};
        SortingUtils.sort(list);
        assertThat(list).hasSize(2);
    }

    @Test
    void should_sort_array_with_custom_comparator() {
        var array = new String[]{"z", "a", "m"};
        SortingUtils.sort(array, Comparator.naturalOrder());
        assertThat(array).containsExactly("a", "m", "z");
    }

    // ============== 数组排序 ==============

    @Test
    void should_sort_array_with_null_comparator() {
        var array = new Low[]{new Low("a"), new Low("b")};
        SortingUtils.sort(array, null);
        assertThat(array).hasSize(2);
    }

    @Test
    void should_sort_list_with_default_comparator() {
        var list = new ArrayList<>(List.of(new Low("a"), new High("b")));
        SortingUtils.sort(list);
        assertThat(list).hasSize(2);
    }

    @Test
    void should_sort_list_with_custom_comparator() {
        var list = new ArrayList<>(List.of("z", "a", "m"));
        SortingUtils.sort(list, Comparator.naturalOrder());
        assertThat(list).containsExactly("a", "m", "z");
    }

    // ============== List 排序 ==============

    @Test
    void should_sort_list_with_null_comparator() {
        var list = new ArrayList<>(List.of(new Low("a"), new High("b")));
        SortingUtils.sort(list, null);
        assertThat(list).hasSize(2);
    }

    @Test
    void should_throw_when_array_is_null() {
        String[] nullArray = null;
        assertThatThrownBy(() -> SortingUtils.sort(nullArray))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_array_is_null_with_comparator() {
        String[] nullArray = null;
        assertThatThrownBy(() -> SortingUtils.sort(nullArray, Comparator.naturalOrder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== 参数验证 ==============

    @Test
    void should_throw_when_list_is_null() {
        List<String> nullList = null;
        assertThatThrownBy(() -> SortingUtils.sort(nullList))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_list_is_null_with_comparator() {
        List<String> nullList = null;
        assertThatThrownBy(() -> SortingUtils.sort(nullList, Comparator.naturalOrder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_handle_empty_array() {
        var array = new Low[0];
        SortingUtils.sort(array);
        assertThat(array).isEmpty();
    }

    @Test
    void should_handle_empty_list() {
        var list = new ArrayList<Low>();
        SortingUtils.sort(list);
        assertThat(list).isEmpty();
    }

    // ============== 空数组/空 List ==============

    @Order(2)
    static class High {
        String name;

        High(String name) {
            this.name = name;
        }
    }

    @Order(1)
    static class Low {
        String name;

        Low(String name) {
            this.name = name;
        }
    }

}
