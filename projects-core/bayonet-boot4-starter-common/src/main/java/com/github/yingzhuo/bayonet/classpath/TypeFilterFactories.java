package com.github.yingzhuo.bayonet.classpath;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.*;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link TypeFilter} 工厂工具类。
 * <p>提供静态方法快速创建各种 {@link TypeFilter}，涵盖注解匹配、类型赋值、类元信息判定
 * （接口/抽象/final/独立类/内部类等）以及组合逻辑（{@link #not not} / {@link #any any} / {@link #all all}）。</p>
 *
 * <pre>{@code
 * var filter = TypeFilterFactories.all(
 *     TypeFilterFactories.isConcrete(),
 *     TypeFilterFactories.hasAnnotation(Component.class)
 * );
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeFilterFactories {

    /**
     * 匹配标注了指定注解的类（含元注解和接口）。
     *
     * @param annotationType 注解类型
     * @return TypeFilter
     */
    public static TypeFilter hasAnnotation(Class<? extends Annotation> annotationType) {
        return hasAnnotation(annotationType, true, true);
    }

    /**
     * 匹配标注了指定注解的类。
     *
     * @param annotationType          注解类型
     * @param considerMetaAnnotations 是否考虑元注解
     * @param considerInterfaces      是否考虑接口上的注解
     * @return TypeFilter
     */
    public static TypeFilter hasAnnotation(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations,
                                           boolean considerInterfaces) {
        Assert.notNull(annotationType, "annotationType is required");
        return new AnnotationTypeFilter(annotationType, considerMetaAnnotations, considerInterfaces);
    }

    /**
     * 匹配可赋值给指定类型的类。
     *
     * @param targetType 目标类型
     * @return TypeFilter
     */
    public static TypeFilter assignable(Class<?> targetType) {
        Assert.notNull(targetType, "targetType is required");
        return new AssignableTypeFilter(targetType);
    }

    /**
     * 匹配全限定类名与指定值相等的类。
     *
     * @param className 类名
     * @return TypeFilter
     */
    public static TypeFilter fullyQualifiedNameEquals(String className) {
        return fullyQualifiedNameEquals(className, false);
    }

    /**
     * 匹配全限定类名与指定值相等的类（可选忽略大小写）。
     *
     * @param className  类名
     * @param ignoreCase 是否忽略大小写
     * @return TypeFilter
     */
    public static TypeFilter fullyQualifiedNameEquals(String className, boolean ignoreCase) {
        Assert.hasText(className, "className is required");
        if (ignoreCase) {
            return (reader, readerFactory) -> className.equalsIgnoreCase(reader.getClassMetadata().getClassName());
        } else {
            return (reader, readerFactory) -> className.equals(reader.getClassMetadata().getClassName());
        }
    }

    /**
     * 匹配全限定类名匹配指定正则的类。
     *
     * @param pattern 正则表达式
     * @return TypeFilter
     */
    public static TypeFilter fullyQualifiedNameMatches(Pattern pattern) {
        Assert.notNull(pattern, "pattern is required");
        return new RegexPatternTypeFilter(pattern);
    }

    /**
     * 匹配接口。
     *
     * @return TypeFilter
     */
    public static TypeFilter isInterface() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.isInterface();
            }
        };
    }

    /**
     * 匹配非接口。
     *
     * @return TypeFilter
     */
    public static TypeFilter isNotInterface() {
        return not(isInterface());
    }

    /**
     * 匹配抽象类。
     *
     * @return TypeFilter
     */
    public static TypeFilter isAbstract() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.isAbstract();
            }
        };
    }

    /**
     * 匹配具体类（非抽象、非接口）。
     *
     * @return TypeFilter
     */
    public static TypeFilter isConcrete() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.isConcrete();
            }
        };
    }

    /**
     * 匹配注解。
     *
     * @return TypeFilter
     */
    public static TypeFilter isAnnotation() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.isAnnotation();
            }
        };
    }

    /**
     * 匹配非注解。
     *
     * @return TypeFilter
     */
    public static TypeFilter isNotAnnotation() {
        return not(isAnnotation());
    }

    /**
     * 匹配 final 类。
     *
     * @return TypeFilter
     */
    public static TypeFilter isFinal() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.isFinal();
            }
        };
    }

    /**
     * 匹配非 final 类。
     *
     * @return TypeFilter
     */
    public static TypeFilter isNotFinal() {
        return not(isFinal());
    }

    /**
     * 匹配独立类（顶级类或静态内部类）。
     *
     * @return TypeFilter
     */
    public static TypeFilter isIndependent() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.isIndependent();
            }
        };
    }

    /**
     * 匹配有父类的类。
     *
     * @return TypeFilter
     */
    public static TypeFilter hasSuperClass() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.hasSuperClass();
            }
        };
    }

    /**
     * 匹配内部类。
     *
     * @return TypeFilter
     */
    public static TypeFilter isInnerClass() {
        return new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return metadata.hasEnclosingClass();
            }
        };
    }

    /**
     * 匹配非内部类。
     *
     * @return TypeFilter
     */
    public static TypeFilter isNotInnerClass() {
        return not(isInnerClass());
    }

    /**
     * 匹配实现了指定接口的类。
     *
     * @param interfaceType 接口类型
     * @return TypeFilter
     */
    public static TypeFilter implementsInterface(final Class<?> interfaceType) {
        Assert.notNull(interfaceType, "interfaceType is required");
        return new AbstractTypeHierarchyTraversingFilter(true, true) {
            @Override
            protected Boolean matchInterface(String interfaceName) {
                return interfaceType.getName().equals(interfaceName);
            }
        };
    }

    /**
     * 匹配未实现指定接口的类。
     *
     * @param interfaceType 接口类型
     * @return TypeFilter
     */
    public static TypeFilter notImplementsInterface(final Class<?> interfaceType) {
        return not(implementsInterface(interfaceType));
    }

    // ------

    /**
     * 逻辑非。
     *
     * @param f 原过滤器
     * @return 取反后的过滤器
     */
    public static TypeFilter not(final TypeFilter f) {
        Assert.notNull(f, "filter is required");
        return (reader, readerFactory) -> !f.match(reader, readerFactory);
    }

    /**
     * 逻辑或（任一匹配即返回 true）。
     *
     * @param filters 过滤器数组（至少 2 个）
     * @return 组合过滤器
     */
    public static TypeFilter any(TypeFilter... filters) {
        Assert.notNull(filters, "filters is null");
        Assert.noNullElements(filters, "filters has null element(s)");
        return new Any(Arrays.asList(filters));
    }

    /**
     * 逻辑与（全部匹配才返回 true）。
     *
     * @param filters 过滤器数组（至少 2 个）
     * @return 组合过滤器
     */
    public static TypeFilter all(TypeFilter... filters) {
        Assert.notNull(filters, "filters is null");
        Assert.noNullElements(filters, "filters has null element(s)");
        return new All(Arrays.asList(filters));
    }

    /**
     * 始终返回 {@code true} 的过滤器。
     *
     * @return TypeFilter
     */
    public static TypeFilter alwaysTrue() {
        return (reader, readerFactory) -> true;
    }

    /**
     * 始终返回 {@code false} 的过滤器。
     *
     * @return TypeFilter
     */
    public static TypeFilter alwaysFalse() {
        return (reader, readerFactory) -> false;
    }

    // ------

    private record All(List<TypeFilter> list) implements TypeFilter {

        private All(List<TypeFilter> list) {
            Assert.notNull(list, "list is required");
            Assert.noNullElements(list, "list has null element(s)");
            Assert.isTrue(list.size() >= 2, "list size must greater than 1");
            this.list = new ArrayList<>(list);
        }

        public boolean match(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
            for (TypeFilter filter : list) {
                if (!filter.match(reader, readerFactory)) {
                    return false;
                }
            }
            return true;
        }
    }

    private record Any(List<TypeFilter> list) implements TypeFilter {

        private Any(List<TypeFilter> list) {
            Assert.notNull(list, "list is required");
            Assert.noNullElements(list, "list has null element(s)");
            Assert.isTrue(list.size() >= 2, "list size must greater than 1");
            this.list = new ArrayList<>(list);
        }

        public boolean match(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
            for (TypeFilter filter : list) {
                if (filter.match(reader, readerFactory)) {
                    return true;
                }
            }
            return false;
        }
    }
}
