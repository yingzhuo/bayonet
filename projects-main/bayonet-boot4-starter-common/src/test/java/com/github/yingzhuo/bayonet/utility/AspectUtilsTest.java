package com.github.yingzhuo.bayonet.utility;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AspectUtilsTest {

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    // ============== getJoinPointMethod ==============

    @Test
    void should_get_method_from_joinPoint() throws NoSuchMethodException {
        var expectedMethod = String.class.getMethod("length");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(expectedMethod);

        var result = AspectUtils.getJoinPointMethod(joinPoint);
        assertThat(result).isEqualTo(expectedMethod);
    }

    @Test
    void should_throw_when_getJoinPointMethod_with_null_joinPoint() {
        assertThatThrownBy(() -> AspectUtils.getJoinPointMethod(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("joinPoint");
    }

    // ============== getJoinPointClass ==============

    @Test
    void should_get_target_class_from_joinPoint() {
        when(joinPoint.getTarget()).thenReturn("test");

        var result = AspectUtils.getJoinPointClass(joinPoint);
        assertThat(result).isEqualTo(String.class);
    }

    @Test
    void should_throw_when_getJoinPointClass_with_null_joinPoint() {
        assertThatThrownBy(() -> AspectUtils.getJoinPointClass(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("joinPoint");
    }

    // ============== resolvePointCutAnnotation — 方法级 ==============

    @Test
    void should_resolve_method_level_annotation() throws NoSuchMethodException {
        var method = Helper.class.getMethod("annotatedMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        var result = AspectUtils.resolvePointCutAnnotation(joinPoint, Deprecated.class);
        assertThat(result).isNotNull();
    }

    @Test
    void should_resolve_class_level_annotation_when_method_has_none() throws NoSuchMethodException {
        var method = HelperWithClassAnnotation.class.getMethod("plainMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getTarget()).thenReturn(new HelperWithClassAnnotation());

        var result = AspectUtils.resolvePointCutAnnotation(joinPoint, Deprecated.class);
        assertThat(result).isNotNull();
    }

    @Test
    void should_return_null_when_no_annotation_found() throws NoSuchMethodException {
        var method = Helper.class.getMethod("plainMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getTarget()).thenReturn(new Helper());

        var result = AspectUtils.resolvePointCutAnnotation(joinPoint, Deprecated.class);
        assertThat(result).isNull();
    }

    // ============== resolvePointCutAnnotation — includeClassLevel = false ==============

    @Test
    void should_not_resolve_class_level_when_includeClassLevel_false() throws NoSuchMethodException {
        var method = HelperWithClassAnnotation.class.getMethod("plainMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        var result = AspectUtils.resolvePointCutAnnotation(joinPoint, Deprecated.class, false);
        assertThat(result).isNull();
    }

    // ============== resolvePointCutAnnotation — 参数校验 ==============

    @Test
    void should_throw_when_resolve_with_null_joinPoint() {
        assertThatThrownBy(() -> AspectUtils.resolvePointCutAnnotation(null, Deprecated.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("joinPoint");
    }

    @Test
    void should_throw_when_resolve_with_null_annotationType() {
        assertThatThrownBy(() -> AspectUtils.resolvePointCutAnnotation(joinPoint, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("annotationType");
    }

    @Test
    void should_throw_when_resolve_with_includeClassLevel_with_null_joinPoint() {
        assertThatThrownBy(() -> AspectUtils.resolvePointCutAnnotation(null, Deprecated.class, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("joinPoint");
    }

    @Test
    void should_throw_when_resolve_with_includeClassLevel_with_null_annotationType() {
        assertThatThrownBy(() -> AspectUtils.resolvePointCutAnnotation(joinPoint, null, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("annotationType");
    }

    // ============== helper ==============

    static class Helper {
        @Deprecated
        public void annotatedMethod() {
        }

        public void plainMethod() {
        }
    }

    @Deprecated
    static class HelperWithClassAnnotation {
        public void plainMethod() {
        }
    }
}
