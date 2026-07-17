# CLAUDE.md

本文件为 ClaudeCode 在此仓库中工作时提供指引。

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
bayonet/
├── buildSrc/                              # 约定插件（预编译脚本插件）
├── project-test/                          # 集成测试项目
├── projects-core/
│   ├── bayonet-bom/                       # BOM（物料清单）POM 模块
│   ├── bayonet-boot4-starter-common/      # 基础与工具（主模块）
│   ├── bayonet-boot4-starter-jwt/         # JWT 支持（auth0 java-jwt）
│   └── bayonet-boot4-starter-security/    # Spring Security 增强
└── gradle/                                # 版本目录和包装器
```

### 构建系统

- **Gradle 8.14.5**，JDK 17 目标
- 通过 `buildSrc/` 中的约定插件统一模块配置，各模块按 ID 引用（`buildlogic.java-conventions` 等）
- 仓库顺序：阿里云镜像 → Maven Central → Gradle Plugin Portal → Spring 仓库
- 禁用模块元数据生成（发布优化），版本属性统一在 `gradle.properties` 中管理

### 模块约定

- `projects-core/` 下的模块应用 `buildlogic.java-conventions`，该插件配置 Java 17、`-parameters` javac 标志、JUnit
  Platform、sources JAR、javadoc JAR 及 Spring Boot 依赖管理

### 发布

- 需发布的模块应用 `buildlogic.maven-central-jar-conventions` 或 `buildlogic.maven-central-bom-conventions` +
  `buildlogic.sonatype-conventions`
- 签名使用 GPG（`signing.useGpgCmd()`）
- Sonatype 凭证来自 `sonatypeUsername`/`sonatypePassword` Gradle 属性或 `SONATYPE_USERNAME`/`SONATYPE_PASSWORD` 环境变量
- 发布类型：`AUTOMATIC`
