# CLAUDE.md

本文件为 ClaudeCode 在此仓库中工作时提供指引。

## 当前版本

`4.1.1` 在生成javadoc文档时，请使用此版本。

## 构建与测试命令

所有命令通过 Gradle 包装器（`./gradlew`）执行。`makefile` 封装了常用操作。

| 命令           | 作用                                                                                    |
|----------------|-----------------------------------------------------------------------------------------|
| `make compile` | 编译全部代码（`./gradlew classes`）                                                     |
| `make build`   | 跳过测试进行构建（`./gradlew -x check -x test build`）                                  |
| `make test`    | 运行全部测试（`./gradlew test`）                                                        |
| `make clean`   | 清理构建产物                                                                            |
| `make install` | 发布到 Maven 本地仓库（`./gradlew -x test -x check publishToMavenLocal --no-parallel`） |
| `make publish` | 先发布到本地仓库，再发布到 Maven Central（需输入 yes 确认）                             |

运行单个测试类：

```
./gradlew :projects-core:bayonet-boot4-starter-common:test --tests "com.github.yingzhuo.SomeTest"
```

运行单个测试方法：

```
./gradlew :projects-core:bayonet-boot4-starter-common:test --tests "com.github.yingzhuo.SomeTest#testMethod"
```

使用 `--info` 或 `--stacktrace` 获取诊断输出。

## 架构

### 多模块结构

```
+--- ':project-integration-test' - 集成测试 (临时)
\--- ':projects-core'
     +--- ':projects-core:bayonet-bom' - BOM
     +--- ':projects-core:bayonet-boot4-starter-common' - 基础工具和公共代码
     +--- ':projects-core:bayonet-boot4-starter-hocon' - HOCON配置文件增强
     +--- ':projects-core:bayonet-boot4-starter-jwt' - JWT增强
     +--- ':projects-core:bayonet-boot4-starter-security' - SpringSecurity增强
     +--- ':projects-core:bayonet-boot4-starter-validation' - Validation增强
     +--- ':projects-core:bayonet-boot4-starter-webcli' - WebClient增强
     +--- ':projects-core:bayonet-boot4-starter-webmvc' - WebMvc增强
     \--- ':projects-core:bayonet-boot4-starter-zxing' - 二维码条形码增强
```

### 构建系统

- **Gradle 9.6.1**，JDK 17 目标
- 通过 `buildSrc/` 中的约定插件统一模块配置，各模块按 ID 引用（`buildlogic.java-conventions` 等）
- 禁用模块元数据生成（发布优化），版本属性统一在 `gradle.properties` 中管理

### 模块约定

- `projects-main/` 下的模块应用 `buildlogic.java-conventions`，该插件配置 Java 17、`-parameters` javac 标志、JUnit
  Platform、sources JAR、javadoc JAR 及 Spring Boot 依赖管理
