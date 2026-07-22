package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.ConstraintValidator;
import org.jspecify.annotations.Nullable;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

abstract class AbstractValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    @Nullable
    @SuppressWarnings("unchecked")
    protected final <T> T getPropertyValue(Object bean, String propertyName)
            throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
            if (descriptor.getName().equals(propertyName) && descriptor.getReadMethod() != null) {
                return (T) descriptor.getReadMethod().invoke(bean);
            }
        }
        return null;
    }

}
