package com.github.yingzhuo.bayonet.jdbc.datasource.dynamic;

import java.lang.annotation.*;

/**
 * 数据源切换注解。
 *
 * <p>可标注在方法或类上，配合 {@link DataSourceSwitchingAspect} 在方法执行前切换到指定的数据源，
 * 方法执行完毕后自动清理上下文。</p>
 *
 * <p><b>优先级规则</b>：方法上的注解优先于类上的注解。即当方法上无此注解时，
 * 会查找类上是否有此注解并使用其值。</p>
 *
 * <p><b>注意</b>：标注在类上时，请用于实现类而非接口上，避免代理机制无法正确读取注解。</p>
 *
 * <p><b>使用示例</b></p>
 * <pre>{@code
 * @DataSourceSwitch("slave")
 * public class UserServiceImpl implements UserService {
 *
 *     @DataSourceSwitch("master") // 此方法使用 master，覆盖类级别的 slave
 *     public User findAdmin() { ... }
 *
 *     public List<User> listUsers() { ... } // 此方法使用类级别的 slave
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
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSourceSwitch {

    /**
     * 数据源标识符。
     *
     * @return 数据源名称，与 {@link DynamicDataSource} 的 targetDataSources 中的 key 对应
     */
    String value();

}
