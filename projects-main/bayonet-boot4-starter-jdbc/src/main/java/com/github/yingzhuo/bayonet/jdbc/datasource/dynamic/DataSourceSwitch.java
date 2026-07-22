package com.github.yingzhuo.bayonet.jdbc.datasource.dynamic;

import java.lang.annotation.*;

/**
 * 数据源切换注解。
 *
 * <p>标注在方法上，配合 {@link DataSourceSwitchingAspect} 在方法执行前切换到指定的数据源，
 * 方法执行完毕后自动清理上下文。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * @DataSourceSwitch("slave")
 * public List<User> listUsers() {
 *     return userRepository.findAll();
 * }
 * }</pre>
 *
 * @author 应卓
 * @see DataSourceSwitchingAspect
 * @see DynamicDataSource
 * @since 4.1.1
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface DataSourceSwitch {

    /**
     * 数据源标识符。
     *
     * @return 数据源名称，与 {@link DynamicDataSource} 的 targetDataSources 中的 key 对应
     */
    String value();

}
