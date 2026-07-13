package com.github.yingzhuo.bayonet.classpath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassPathScannerWorkerTest {

    private final ClassPathScannerWorker scanner = new ClassPathScannerWorker(false) {
        @Override
        public boolean isCandidateComponent(AnnotatedBeanDefinition bd) {
            return super.isCandidateComponent(bd);
        }
    };
    @Mock
    private AnnotatedBeanDefinition beanDefinition;
    @Mock
    private AnnotationMetadata metadata;

    @Test
    void should_accept_independent_non_annotation() {
        when(beanDefinition.getMetadata()).thenReturn(metadata);
        when(metadata.isIndependent()).thenReturn(true);
        when(metadata.isAnnotation()).thenReturn(false);

        assertThat(scanner.isCandidateComponent(beanDefinition)).isTrue();
    }

    @Test
    void should_reject_annotation() {
        when(beanDefinition.getMetadata()).thenReturn(metadata);
        when(metadata.isIndependent()).thenReturn(true);
        when(metadata.isAnnotation()).thenReturn(true);

        assertThat(scanner.isCandidateComponent(beanDefinition)).isFalse();
    }

    @Test
    void should_reject_non_independent() {
        when(beanDefinition.getMetadata()).thenReturn(metadata);
        when(metadata.isIndependent()).thenReturn(false);

        assertThat(scanner.isCandidateComponent(beanDefinition)).isFalse();
    }

    @Test
    void should_reject_non_independent_annotation() {
        when(beanDefinition.getMetadata()).thenReturn(metadata);
        when(metadata.isIndependent()).thenReturn(false);

        assertThat(scanner.isCandidateComponent(beanDefinition)).isFalse();
    }

}
