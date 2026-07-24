package com.github.yingzhuo.bayonet.utility.reflection;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * 反射实例创建器。
 *
 * <p>通过反射方式创建指定类型的实例，配合 {@link InstanceCreatorBuilder} 使用。</p>
 *
 * <pre>{@code
 * // 通过 Class 创建
 * var creator1 = InstanceCreator.builder(Foo.class)
 *         .constructorParams(String.class)
 *         .build();
 * var foo1 = creator1.create("hello");
 *
 * // 通过类名字符串创建
 * var creator2 = InstanceCreator.builder("com.example.Foo")
 *         .build();
 * var foo2 = creator2.create();
 * }</pre>
 *
 * @author 应卓
 * @see InstanceCreatorBuilder
 * @since 4.1.1
 */
public interface InstanceCreator {

    /**
     * 创建 {@link InstanceCreatorBuilder}，按指定类型构建创建器。
     *
     * @param klass 目标类型，不可为 {@code null}
     * @return {@link InstanceCreatorBuilder} 实例
     */
    static InstanceCreatorBuilder builder(Class<?> klass) {
        Assert.notNull(klass, "targetClass must not be null");
        return new InstanceCreatorBuilder(klass);
    }

    /**
     * 创建 {@link InstanceCreatorBuilder}，按指定类型名称构建创建器。
     *
     * @param klassName 目标类型全限定名，不可为空
     * @return {@link InstanceCreatorBuilder} 实例
     */
    static InstanceCreatorBuilder builder(String klassName) {
        Assert.hasText(klassName, "targetClassName must not be empty");
        try {
            return new InstanceCreatorBuilder(ClassUtils.forName(klassName, null));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("class not found: '" + klassName + "'", e);
        }
    }

    /**
     * 通过反射方式创建一个实例。
     *
     * @param <T>    实例类型
     * @param params 构造器实际参数，与 {@link InstanceCreatorBuilder#constructorParams(Class[])} 声明的类型对应
     * @return 实例
     */
    <T> T create(Object... params);

}
