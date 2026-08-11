import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.function.Supplier

/**
 * 查找必需的 Gradle 属性。
 * <p>从项目属性中读取指定属性，属性缺失时抛出异常。</p>
 *
 * @param project           目标项目
 * @param name              属性名
 * @param exceptionSupplier 属性缺失时提供的异常（默认抛出 {@code GradleException}）
 * @return 属性值（非 {@code null}）
 */
fun findRequiredProperty(
    project: Project,
    name: String,
    exceptionSupplier: Supplier<Exception> = Supplier { GradleException("cannot find '$name' property") }
): Any {
    return project.findProperty(name) ?: throw exceptionSupplier.get()
}
