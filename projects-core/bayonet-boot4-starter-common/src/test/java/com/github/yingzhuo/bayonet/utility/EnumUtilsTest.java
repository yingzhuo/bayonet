package com.github.yingzhuo.bayonet.utility;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumUtilsTest {

    private enum Color {
        RED, GREEN, BLUE
    }

    // ============== getEnum ==============

    @Test
    void should_getEnum_when_nameExists() {
        assertThat(EnumUtils.getEnum(Color.class, "RED")).isEqualTo(Color.RED);
    }

    @Test
    void should_throw_when_getEnum_nameNotExists() {
        assertThatThrownBy(() -> EnumUtils.getEnum(Color.class, "YELLOW"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getEnum_enumClassIsNull() {
        assertThatThrownBy(() -> EnumUtils.getEnum(null, "RED"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getEnum_enumNameIsEmpty() {
        assertThatThrownBy(() -> EnumUtils.getEnum(Color.class, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getEnum_enumNameIsNull() {
        assertThatThrownBy(() -> EnumUtils.getEnum(Color.class, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getEnum with default ==============

    @Test
    void should_getEnum_withDefault_when_nameExists() {
        assertThat(EnumUtils.getEnum(Color.class, "RED", Color.BLUE)).isEqualTo(Color.RED);
    }

    @Test
    void should_return_default_when_getEnum_nameNotExists() {
        assertThat(EnumUtils.getEnum(Color.class, "YELLOW", Color.BLUE)).isEqualTo(Color.BLUE);
    }

    @Test
    void should_throw_when_getEnum_withDefault_enumClassIsNull() {
        assertThatThrownBy(() -> EnumUtils.getEnum(null, "RED", Color.BLUE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_getEnum_withDefault_defaultIsNull() {
        assertThatThrownBy(() -> EnumUtils.getEnum(Color.class, "RED", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getEnumIgnoreCase ==============

    @Test
    void should_getEnumIgnoreCase_when_nameMatchesCase() {
        assertThat(EnumUtils.getEnumIgnoreCase(Color.class, "red")).isEqualTo(Color.RED);
    }

    @Test
    void should_getEnumIgnoreCase_when_nameMixedCase() {
        assertThat(EnumUtils.getEnumIgnoreCase(Color.class, "GrEeN")).isEqualTo(Color.GREEN);
    }

    @Test
    void should_throw_when_getEnumIgnoreCase_nameNotExists() {
        assertThatThrownBy(() -> EnumUtils.getEnumIgnoreCase(Color.class, "YELLOW"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getEnumIgnoreCase with default ==============

    @Test
    void should_getEnumIgnoreCase_withDefault_when_nameExists() {
        assertThat(EnumUtils.getEnumIgnoreCase(Color.class, "blue", Color.RED)).isEqualTo(Color.BLUE);
    }

    @Test
    void should_return_default_when_getEnumIgnoreCase_nameNotExists() {
        assertThat(EnumUtils.getEnumIgnoreCase(Color.class, "YELLOW", Color.RED)).isEqualTo(Color.RED);
    }

    @Test
    void should_throw_when_getEnumIgnoreCase_withDefault_defaultIsNull() {
        assertThatThrownBy(() -> EnumUtils.getEnumIgnoreCase(Color.class, "RED", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getEnumList ==============

    @Test
    void should_getEnumList_when_valid() {
        var list = EnumUtils.getEnumList(Color.class);
        assertThat(list).containsExactly(Color.RED, Color.GREEN, Color.BLUE);
    }

    @Test
    void should_throw_when_getEnumList_enumClassIsNull() {
        assertThatThrownBy(() -> EnumUtils.getEnumList(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== getEnumMap ==============

    @Test
    void should_getEnumMap_when_valid() {
        var map = EnumUtils.getEnumMap(Color.class);
        assertThat(map).hasSize(3);
        assertThat(map).containsEntry("RED", Color.RED);
        assertThat(map).containsEntry("GREEN", Color.GREEN);
        assertThat(map).containsEntry("BLUE", Color.BLUE);
    }

    @Test
    void should_getEnumMap_preserveOrder() {
        var map = EnumUtils.getEnumMap(Color.class);
        assertThat(map.keySet()).containsExactly("RED", "GREEN", "BLUE");
    }

    @Test
    void should_throw_when_getEnumMap_enumClassIsNull() {
        assertThatThrownBy(() -> EnumUtils.getEnumMap(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== isValidEnum ==============

    @Test
    void should_return_true_when_isValidEnum_nameExists() {
        assertThat(EnumUtils.isValidEnum(Color.class, "RED")).isTrue();
    }

    @Test
    void should_return_false_when_isValidEnum_nameNotExists() {
        assertThat(EnumUtils.isValidEnum(Color.class, "YELLOW")).isFalse();
    }

    @Test
    void should_throw_when_isValidEnum_enumClassIsNull() {
        assertThatThrownBy(() -> EnumUtils.isValidEnum(null, "RED"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throw_when_isValidEnum_enumNameIsEmpty() {
        assertThatThrownBy(() -> EnumUtils.isValidEnum(Color.class, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== isValidEnumIgnoreCase ==============

    @Test
    void should_return_true_when_isValidEnumIgnoreCase_nameMatches() {
        assertThat(EnumUtils.isValidEnumIgnoreCase(Color.class, "red")).isTrue();
    }

    @Test
    void should_return_false_when_isValidEnumIgnoreCase_nameNotExists() {
        assertThat(EnumUtils.isValidEnumIgnoreCase(Color.class, "YELLOW")).isFalse();
    }

    @Test
    void should_throw_when_isValidEnumIgnoreCase_enumClassIsNull() {
        assertThatThrownBy(() -> EnumUtils.isValidEnumIgnoreCase(null, "RED"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
