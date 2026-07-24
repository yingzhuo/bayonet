package com.github.yingzhuo.bayonet.utility.reflection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InstanceCreatorBuilderTest {

    // ============== InstanceCreator.builder(Class) ==============

    @Test
    void should_create_builder_via_instanceCreator_forClass() {
        var builder = InstanceCreator.builder(Foo.class);
        assertThat(builder).isNotNull();
    }

    @Test
    void should_create_builder_via_instanceCreator_forClassName() {
        var builder = InstanceCreator.builder("com.github.yingzhuo.bayonet.utility.reflection.InstanceCreatorBuilderTest$Foo");
        assertThat(builder).isNotNull();
    }

    @Test
    void should_throw_when_instanceCreator_builder_className_invalid() {
        assertThatThrownBy(() -> InstanceCreator.builder("non.existent.Class"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("class not found");
    }

    // ============== InstanceCreator.builder(Class) ==============

    @Test
    void should_create_builder_forClass() {
        var builder = InstanceCreator.builder(Foo.class);
        assertThat(builder).isNotNull();
    }

    @Test
    void should_throw_when_forClass_null() {
        assertThatThrownBy(() -> InstanceCreator.builder((Class<?>) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetClass");
    }

    @Test
    void should_create_builder_forClassName() {
        var builder = InstanceCreator.builder("java.lang.String");
        assertThat(builder).isNotNull();
    }

    @Test
    void should_throw_when_forClassName_empty() {
        assertThatThrownBy(() -> InstanceCreator.builder(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetClassName");
    }

    @Test
    void should_throw_when_forClassName_notFound() {
        assertThatThrownBy(() -> InstanceCreator.builder("com.example.Nonexistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("class not found");
    }

    // ============== build + create — no-arg constructor ==============

    @Test
    void should_create_instance_with_noarg_constructor() {
        Foo foo = InstanceCreator.builder(Foo.class)
                .build()
                .create();
        assertThat(foo).isNotNull();
    }

    @Test
    void should_create_instance_with_parameterized_constructor() {
        Bar bar = InstanceCreator.builder(Bar.class)
                .constructorParams(String.class, int.class)
                .build()
                .create("hello", 42);
        assertThat(bar).isNotNull();
        assertThat(bar.getName()).isEqualTo("hello");
        assertThat(bar.getAge()).isEqualTo(42);
    }

    @Test
    void should_throw_when_constructor_not_found() {
        var creator = InstanceCreator.builder(Foo.class)
                .constructorParams(int.class)
                .build();

        assertThatThrownBy(() -> creator.create(123))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no such constructor");
    }

    @Test
    void should_return_correct_type() {
        Foo foo = InstanceCreator.builder(Foo.class)
                .build()
                .create();
        assertThat(foo).isNotNull();
    }

    // ============== setProperty ==============

    @Test
    void should_set_property_via_setter() {
        Foo foo = InstanceCreator.builder(Foo.class)
                .setProperty("name", "test-name")
                .build()
                .create();
        assertThat(foo.getName()).isEqualTo("test-name");
    }

    @Test
    void should_set_multiple_properties_in_order() {
        Foo foo = InstanceCreator.builder(Foo.class)
                .setProperty("name", "hello")
                .setProperty("age", 25)
                .build()
                .create();
        assertThat(foo.getName()).isEqualTo("hello");
        assertThat(foo.getAge()).isEqualTo(25);
    }

    @Test
    void should_set_property_after_parameterized_constructor() {
        Bar bar = InstanceCreator.builder(Bar.class)
                .constructorParams(String.class, int.class)
                .setProperty("name", "overridden")
                .build()
                .create("original", 10);
        assertThat(bar.getName()).isEqualTo("overridden");
        assertThat(bar.getAge()).isEqualTo(10);
    }

    @Test
    void should_throw_when_setter_not_found_and_silent_false() {
        var creator = InstanceCreator.builder(Foo.class)
                .setProperty("nonexistent", "value")
                .build();

        assertThatThrownBy(() -> creator.create())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void should_skip_when_setter_not_found_and_silent_true() {
        Foo foo = InstanceCreator.builder(Foo.class)
                .setProperty("nonexistent", "value")
                .silentOnSetterFailure(true)
                .build()
                .create();
        assertThat(foo).isNotNull();
    }

    // ============== silentOnSetterFailure ==============

    @Test
    void should_throw_when_setter_throws_and_silent_false() {
        var creator = InstanceCreator.builder(Baz.class)
                .setProperty("value", "anything")
                .build();

        assertThatThrownBy(() -> creator.create())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("setter failed");
    }

    @Test
    void should_skip_when_setter_throws_and_silent_true() {
        Baz baz = InstanceCreator.builder(Baz.class)
                .setProperty("value", "anything")
                .silentOnSetterFailure(true)
                .build()
                .create();
        assertThat(baz).isNotNull();
    }

    // ============== helper classes ==============

    public static class Foo {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Bar {
        private String name;
        private int age;

        public Bar(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }
    }

    public static class Baz {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            throw new RuntimeException("setter failed: " + value);
        }
    }
}
