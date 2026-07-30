package com.github.yingzhuo.bayonet.jdbc.datasource;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用动态数据源的注解。
 * <p>通过 {@link DynamicDataSource} 和 {@link DataSourceSwitch}
 * 实现运行时数据源切换，适用于多数据源场景。</p>
 *
 * <p>使用方式：</p>
 * <pre>{@code
 * @Configuration
 * @EnableDynamicDataSource
 * public class DataSourceConfig { }
 * }</pre>
 *
 * @author 应卓
 * @see DynamicDataSource
 * @see DataSourceSwitch
 * @see DataSourceContextHolder
 * @since 4.1.1
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EnableDynamicDataSourceConfiguration.class)
public @interface EnableDynamicDataSource {
}
