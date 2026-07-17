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

    // ============== getJoinPointMethodAnnotation ==============

    @Test
    void should_get_annotation_from_method() throws NoSuchMethodException {
        var method = Helper.class.getMethod("annotatedMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        var result = AspectUtils.getJoinPointMethodAnnotation(joinPoint, Deprecated.class);
        assertThat(result).isNotNull();
    }

    @Test
    void should_return_null_when_no_annotation() throws NoSuchMethodException {
        var method = Helper.class.getMethod("plainMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        var result = AspectUtils.getJoinPointMethodAnnotation(joinPoint, Deprecated.class);
        assertThat(result).isNull();
    }

    @Test
    void should_throw_when_getAnnotation_with_null_joinPoint() {
        assertThatThrownBy(() -> AspectUtils.getJoinPointMethodAnnotation(null, Deprecated.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("joinPoint");
    }

    @Test
    void should_throw_when_getAnnotation_with_null_annotationType() {
        assertThatThrownBy(() -> AspectUtils.getJoinPointMethodAnnotation(joinPoint, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("annotationType");
    }

    // ============== hasJoinPointMethodAnnotation ==============

    @Test
    void should_return_true_when_annotation_present() throws NoSuchMethodException {
        var method = Helper.class.getMethod("annotatedMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        assertThat(AspectUtils.hasJoinPointMethodAnnotation(joinPoint, Deprecated.class)).isTrue();
    }

    @Test
    void should_return_false_when_annotation_absent() throws NoSuchMethodException {
        var method = Helper.class.getMethod("plainMethod");
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        assertThat(AspectUtils.hasJoinPointMethodAnnotation(joinPoint, Deprecated.class)).isFalse();
    }

    // ============== helper ==============

    static class Helper {
        @Deprecated
        public void annotatedMethod() {}

        public void plainMethod() {}
    }

}
