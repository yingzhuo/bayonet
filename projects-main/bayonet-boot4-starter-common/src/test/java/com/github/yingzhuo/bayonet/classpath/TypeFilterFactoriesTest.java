package com.github.yingzhuo.bayonet.classpath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeFilterFactoriesTest {

    @Mock
    private MetadataReader reader;

    @Mock
    private MetadataReaderFactory readerFactory;

    @Mock
    private ClassMetadata metadata;

    // ============== alwaysTrue / alwaysFalse ==============

    @Test
    void alwaysTrue() throws Exception {
        assertThat(TypeFilterFactories.alwaysTrue().match(reader, readerFactory)).isTrue();
    }

    @Test
    void alwaysFalse() throws Exception {
        assertThat(TypeFilterFactories.alwaysFalse().match(reader, readerFactory)).isFalse();
    }

    // ============== not ==============

    @Test
    void not_should_negate() throws Exception {
        assertThat(TypeFilterFactories.not(TypeFilterFactories.alwaysTrue()).match(reader, readerFactory)).isFalse();
        assertThat(TypeFilterFactories.not(TypeFilterFactories.alwaysFalse()).match(reader, readerFactory)).isTrue();
    }

    @Test
    void not_should_throw_when_null() {
        assertThatThrownBy(() -> TypeFilterFactories.not(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== hasAnnotation ==============

    @Test
    void hasAnnotation_should_throw_when_null() {
        assertThatThrownBy(() -> TypeFilterFactories.hasAnnotation(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== assignable ==============

    @Test
    void assignable_should_throw_when_null() {
        assertThatThrownBy(() -> TypeFilterFactories.assignable(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== fullyQualifiedNameEquals ==============

    @Test
    void fullyQualifiedNameEquals_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.getClassName()).thenReturn("com.example.Foo");

        assertThat(TypeFilterFactories.fullyQualifiedNameEquals("com.example.Foo").match(reader, readerFactory)).isTrue();
        assertThat(TypeFilterFactories.fullyQualifiedNameEquals("com.example.Bar").match(reader, readerFactory)).isFalse();
    }

    @Test
    void fullyQualifiedNameEquals_should_ignoreCase() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.getClassName()).thenReturn("com.example.Foo");

        assertThat(TypeFilterFactories.fullyQualifiedNameEquals("COM.EXAMPLE.FOO", true).match(reader, readerFactory)).isTrue();
        assertThat(TypeFilterFactories.fullyQualifiedNameEquals("COM.EXAMPLE.BAR", true).match(reader, readerFactory)).isFalse();
    }

    @Test
    void fullyQualifiedNameEquals_should_throw_when_blank() {
        assertThatThrownBy(() -> TypeFilterFactories.fullyQualifiedNameEquals(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== fullyQualifiedNameMatches ==============

    @Test
    void fullyQualifiedNameMatches_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.getClassName()).thenReturn("com.example.Foo");

        assertThat(TypeFilterFactories.fullyQualifiedNameMatches(Pattern.compile("com\\.example\\..+")).match(reader, readerFactory)).isTrue();
        assertThat(TypeFilterFactories.fullyQualifiedNameMatches(Pattern.compile("com\\.other\\..+")).match(reader, readerFactory)).isFalse();
    }

    // ============== isInterface / isNotInterface ==============

    @Test
    void isInterface_should_match_when_interface() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isInterface()).thenReturn(true);

        assertThat(TypeFilterFactories.isInterface().match(reader, readerFactory)).isTrue();
    }

    @Test
    void isNotInterface_should_match_when_notInterface() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isInterface()).thenReturn(false);

        assertThat(TypeFilterFactories.isNotInterface().match(reader, readerFactory)).isTrue();
    }

    // ============== isAbstract / isConcrete ==============

    @Test
    void isAbstract_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isAbstract()).thenReturn(true);

        assertThat(TypeFilterFactories.isAbstract().match(reader, readerFactory)).isTrue();
    }

    @Test
    void isConcrete_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isConcrete()).thenReturn(true);

        assertThat(TypeFilterFactories.isConcrete().match(reader, readerFactory)).isTrue();
    }

    // ============== isAnnotation / isNotAnnotation ==============

    @Test
    void isAnnotation_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isAnnotation()).thenReturn(true);

        assertThat(TypeFilterFactories.isAnnotation().match(reader, readerFactory)).isTrue();
    }

    @Test
    void isNotAnnotation_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isAnnotation()).thenReturn(false);

        assertThat(TypeFilterFactories.isNotAnnotation().match(reader, readerFactory)).isTrue();
    }

    // ============== isFinal / isNotFinal ==============

    @Test
    void isFinal_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isFinal()).thenReturn(true);

        assertThat(TypeFilterFactories.isFinal().match(reader, readerFactory)).isTrue();
    }

    @Test
    void isNotFinal_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isFinal()).thenReturn(false);

        assertThat(TypeFilterFactories.isNotFinal().match(reader, readerFactory)).isTrue();
    }

    // ============== isIndependent ==============

    @Test
    void isIndependent_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.isIndependent()).thenReturn(true);

        assertThat(TypeFilterFactories.isIndependent().match(reader, readerFactory)).isTrue();
    }

    // ============== hasSuperClass ==============

    @Test
    void hasSuperClass_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.hasSuperClass()).thenReturn(true);

        assertThat(TypeFilterFactories.hasSuperClass().match(reader, readerFactory)).isTrue();
    }

    // ============== isInnerClass / isNotInnerClass ==============

    @Test
    void isInnerClass_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.hasEnclosingClass()).thenReturn(true);

        assertThat(TypeFilterFactories.isInnerClass().match(reader, readerFactory)).isTrue();
    }

    @Test
    void isNotInnerClass_should_match() throws Exception {
        when(reader.getClassMetadata()).thenReturn(metadata);
        when(metadata.hasEnclosingClass()).thenReturn(false);

        assertThat(TypeFilterFactories.isNotInnerClass().match(reader, readerFactory)).isTrue();
    }

    // ============== implementsInterface ==============

    @Test
    void implementsInterface_should_throw_when_null() {
        assertThatThrownBy(() -> TypeFilterFactories.implementsInterface(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============== any / all ==============

    @Test
    void any_should_returnTrue_when_anyMatches() throws Exception {
        var filter = TypeFilterFactories.any(TypeFilterFactories.alwaysTrue(), TypeFilterFactories.alwaysFalse());
        assertThat(filter.match(reader, readerFactory)).isTrue();
    }

    @Test
    void any_should_returnFalse_when_noneMatch() throws Exception {
        var filter = TypeFilterFactories.any(TypeFilterFactories.alwaysFalse(), TypeFilterFactories.alwaysFalse());
        assertThat(filter.match(reader, readerFactory)).isFalse();
    }

    @Test
    void all_should_returnTrue_when_allMatch() throws Exception {
        var filter = TypeFilterFactories.all(TypeFilterFactories.alwaysTrue(), TypeFilterFactories.alwaysTrue());
        assertThat(filter.match(reader, readerFactory)).isTrue();
    }

    @Test
    void all_should_returnFalse_when_anyFails() throws Exception {
        var filter = TypeFilterFactories.all(TypeFilterFactories.alwaysTrue(), TypeFilterFactories.alwaysFalse());
        assertThat(filter.match(reader, readerFactory)).isFalse();
    }

    @Test
    void any_should_throw_when_null_filters() {
        assertThatThrownBy(() -> TypeFilterFactories.any((TypeFilter[]) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void any_should_throw_when_null_element() {
        assertThatThrownBy(() -> TypeFilterFactories.any(TypeFilterFactories.alwaysTrue(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void any_should_throw_when_single_filter() {
        assertThatThrownBy(() -> TypeFilterFactories.any(TypeFilterFactories.alwaysTrue()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void all_should_throw_when_null_filters() {
        assertThatThrownBy(() -> TypeFilterFactories.all((TypeFilter[]) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void all_should_throw_when_null_element() {
        assertThatThrownBy(() -> TypeFilterFactories.all(TypeFilterFactories.alwaysTrue(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void all_should_throw_when_single_filter() {
        assertThatThrownBy(() -> TypeFilterFactories.all(TypeFilterFactories.alwaysTrue()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
