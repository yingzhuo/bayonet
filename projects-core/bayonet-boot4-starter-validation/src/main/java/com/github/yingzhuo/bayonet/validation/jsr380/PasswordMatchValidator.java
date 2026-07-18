package com.github.yingzhuo.bayonet.validation.jsr380;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.Nullable;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * {@link PasswordMatch} 注解的校验器实现。
 * <p>通过反射获取指定属性的值，比较两者是否相等。
 * 根据 JSR 380 规范，{@code null} 值视为有效（由 {@link jakarta.validation.constraints.NotNull} 处理）。</p>
 *
 * @author 应卓
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String propertyName1;
    private String propertyName2;

    @Override
    public void initialize(PasswordMatch annotation) {
        this.propertyName1 = annotation.propertyName1();
        this.propertyName2 = annotation.propertyName2();
    }

    @Override
    public boolean isValid(@Nullable Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            var v1 = getPropertyValue(value, propertyName1);
            var v2 = getPropertyValue(value, propertyName2);
            if (v1 == null || v2 == null) {
                return true;
            }
            return v1.equals(v2);
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            return false;
        }
    }

    @Nullable
    private Object getPropertyValue(Object bean, String propertyName)
            throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
            if (descriptor.getName().equals(propertyName) && descriptor.getReadMethod() != null) {
                return descriptor.getReadMethod().invoke(bean);
            }
        }
        return null;
    }
}
