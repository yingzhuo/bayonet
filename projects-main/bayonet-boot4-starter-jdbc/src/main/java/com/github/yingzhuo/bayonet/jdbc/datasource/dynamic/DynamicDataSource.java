package com.github.yingzhuo.bayonet.jdbc.datasource.dynamic;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源。
 *
 * <p>继承 {@link AbstractRoutingDataSource}，通过 {@link DataSourceContextHolder} 获取当前线程的数据源标识符，
 * 实现运行时动态切换数据源。当 lookup key 为 {@code null} 时，使用默认数据源。</p>
 *
 * <p>与 {@link DataSourceSwitchingAspect} 和 {@link DataSourceSwitch} 注解配合使用，
 * 通过 AOP 在方法调用前自动切换数据源，调用结束后清理上下文。</p>
 *
 * <p><b>使用方式</b></p>
 * <pre>{@code
 * // 配置动态数据源
 * @Bean
 * public DynamicDataSource dynamicDataSource() {
 *     var ds = new DynamicDataSource();
 *     var target = new HashMap<Object, Object>();
 *     target.put("master", masterDS);
 *     target.put("slave", slaveDS);
 *     ds.setDefaultTargetDataSource(masterDS);
 *     ds.setTargetDataSources(target);
 *     return ds;
 * }
 *
 * // 在方法上指定数据源
 * @DataSourceSwitch("slave")
 * public List<User> listUsers() { ... }
 * }</pre>
 *
 * @author 应卓
 * @see AbstractRoutingDataSource
 * @see DataSourceContextHolder
 * @see DataSourceSwitch
 * @since 4.1.1
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 返回当前线程的数据源 lookup key。
     *
     * @return 数据源标识符，为 {@code null} 时使用默认数据源
     */
    @Override
    protected @Nullable Object determineCurrentLookupKey() {
        return DataSourceContextHolder.get();
    }

}
