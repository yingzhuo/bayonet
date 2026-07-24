package com.github.yingzhuo.bayonet.utility.reflection;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link InstanceCreator} 的构建器。
 *
 * <p>使用建造者模式创建 {@link InstanceCreator} 实例，支持指定目标类型和构造器参数类型。</p>
 *
 * <pre>{@code
 * var creator = InstanceCreatorBuilder.forClass(Foo.class)
 *         .constructorParams(String.class, Integer.class)
 *         .build();
 *
 * var foo = creator.create("hello", 42);
 * }</pre>
 *
 * @author 应卓
 * @see InstanceCreator
 * @since 4.1.1
 */
public final class InstanceCreatorBuilder {

    private final Class<?> targetClass;
    private final List<Property> properties = new ArrayList<>();
    private Class<?>[] paramTypes = new Class<?>[0];
    private boolean silentOnSetterFailure = false;

    /**
     * 私有构造方法
     *
     * @param targetClass 目标类型
     */
    InstanceCreatorBuilder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * 指定构造器参数类型列表。
     *
     * @param paramTypes 参数类型列表，为空时表示使用无参构造器
     * @return 当前构建器
     */
    public InstanceCreatorBuilder constructorParams(Class<?>... paramTypes) {
        Assert.notNull(paramTypes, "paramTypes must not be null");
        this.paramTypes = paramTypes;
        return this;
    }

    /**
     * 设置属性值（通过 Spring {@link BeanWrapperImpl} 设置）。
     * <p>创建对象后会按添加顺序依次调用对应的 setter 方法。
     * 支持自动类型转换（如 {@code Integer} → {@code int}）。</p>
     *
     * @param propertyName  属性名，不可为空
     * @param propertyValue 属性值
     * @return 当前构建器
     */
    public InstanceCreatorBuilder setProperty(String propertyName, Object propertyValue) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        properties.add(new Property(propertyName, propertyValue));
        return this;
    }

    /**
     * 设置 setter 调用失败时的行为。
     *
     * @param silentOnSetterFailure {@code true} 时静默跳过失败，{@code false} 时抛出异常（默认）
     * @return 当前构建器
     */
    public InstanceCreatorBuilder silentOnSetterFailure(boolean silentOnSetterFailure) {
        this.silentOnSetterFailure = silentOnSetterFailure;
        return this;
    }

    /**
     * 构建 {@link InstanceCreator} 实例。
     *
     * @return {@link InstanceCreator} 实例
     */
    public InstanceCreator build() {
        return new DefaultInstanceCreator(
                this.targetClass,
                this.paramTypes,
                List.copyOf(this.properties),
                this.silentOnSetterFailure
        );
    }

    // ------

    private record DefaultInstanceCreator(
            Class<?> clazz,
            Class<?>[] paramTypes,
            List<Property> properties,
            boolean silentOnSetterFailure
    ) implements InstanceCreator {

        @Override
        @SuppressWarnings("unchecked")
        public <T> T create(Object... params) {
            Constructor<?> constructor;
            try {
                constructor = ReflectionUtils.accessibleConstructor(clazz(), paramTypes());
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("no such constructor in " + clazz(), e);
            }

            T instance;
            try {
                instance = (T) constructor.newInstance(params);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("cannot create instance of " + clazz(), e);
            } catch (InvocationTargetException e) {
                var cause = e.getCause();
                if (cause instanceof RuntimeException re) {
                    throw re;
                }
                if (cause instanceof Error er) {
                    throw er;
                }
                throw new IllegalArgumentException("constructor threw an exception in " + clazz(), cause);
            }

            var wrapper = new BeanWrapperImpl(instance);
            for (var prop : properties()) {
                try {
                    wrapper.setPropertyValue(prop.name(), prop.value());
                } catch (RuntimeException e) {
                    if (!silentOnSetterFailure()) {
                        throw e;
                    }
                }
            }
            return instance;
        }
    }

    private record Property(String name, Object value) {
    }
}
