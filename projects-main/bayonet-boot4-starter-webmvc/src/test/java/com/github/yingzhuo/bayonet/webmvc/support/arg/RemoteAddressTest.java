package com.github.yingzhuo.bayonet.webmvc.support.arg;

import org.junit.jupiter.api.Test;

import java.lang.annotation.*;

import static org.assertj.core.api.Assertions.assertThat;

class RemoteAddressTest {

    @Test
    void should_be_retained_at_runtime() {
        var retention = RemoteAddress.class.getAnnotation(Retention.class);
        assertThat(retention).isNotNull();
        assertThat(retention.value()).isEqualTo(RetentionPolicy.RUNTIME);
    }

    @Test
    void should_target_parameter_and_annotation_type() {
        var target = RemoteAddress.class.getAnnotation(Target.class);
        assertThat(target).isNotNull();
        assertThat(target.value()).contains(ElementType.PARAMETER, ElementType.ANNOTATION_TYPE);
    }

    @Test
    void should_be_documented() {
        var documented = RemoteAddress.class.getAnnotation(Documented.class);
        assertThat(documented).isNotNull();
    }

}
