package com.github.yingzhuo.bayonet.utility.collection.iterator;

import com.github.yingzhuo.bayonet.utility.collection.EnumerationIterator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumerationIteratorTest {

    @Test
    void should_iterate_over_elements() {
        var enumeration = Collections.enumeration(List.of("a", "b", "c"));
        var iterator = new EnumerationIterator<>(enumeration);

        assertThat(iterator).toIterable().containsExactly("a", "b", "c");
    }

    @Test
    void should_iterate_via_newInstance() {
        var enumeration = Collections.enumeration(List.of("x", "y"));
        var iterator = EnumerationIterator.newInstance(enumeration);

        assertThat(iterator).toIterable().containsExactly("x", "y");
    }

    @Test
    void should_be_empty_when_enumeration_is_empty() {
        var enumeration = Collections.enumeration(List.of());
        var iterator = new EnumerationIterator<>(enumeration);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void should_support_forEachRemaining() {
        var enumeration = Collections.enumeration(List.of("a", "b"));
        var iterator = new EnumerationIterator<>(enumeration);

        var result = new ArrayList<String>();
        iterator.forEachRemaining(result::add);

        assertThat(result).containsExactly("a", "b");
    }

    @Test
    void should_throw_when_enumeration_is_null_in_constructor() {
        assertThatThrownBy(() -> new EnumerationIterator<>(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_enumeration_is_null_in_newInstance() {
        assertThatThrownBy(() -> EnumerationIterator.newInstance(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
