# CLAUDE.md

本文件为 Claude Code（claude.ai/code）在此仓库中工作时提供指引。

## 什么是 Bayonet？

Bayonet 是应卓开发的 Spring Boot 4.x 增强库，提供可复用的自动配置、工具类和集成支持，涵盖
Web、安全、数据（JPA/Redis/MongoDB）、校验、日志等领域。

## 构建与测试命令

所有命令通过 Gradle 包装器（`./gradlew`）执行。`Makefile` 封装了常用操作。

| 命令             | 作用                                                                             |
|----------------|--------------------------------------------------------------------------------|
| `make build`   | 跳过测试进行构建（`./gradlew -x check -x test build`）                                   |
| `make test`    | 运行全部测试（`./gradlew test`）                                                       |
| `make check`   | 运行检查（含 checkstyle）（`./gradlew check`）                                          |
| `make clean`   | 清理构建产物                                                                         |
| `make install` | 发布到 Maven 本地仓库（`./gradlew -x test -x check publishToMavenLocal --no-parallel`） |
| `make publish` | 先发布到本地仓库，再发布到 Maven Central（需输入 yes 确认）                                        |

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
├── projects-core/
│   ├── bayonet-bom/                       # BOM（物料清单）POM 模块
│   └── bayonet-boot4-starter-common/      # 基础与工具（主模块）
└── gradle/
    ├── libs.versions.toml                 # 版本目录（已预留，可迁移版本号至此集中管理）
    └── wrapper/                           # Gradle 包装器
```

### 构建系统

- **Gradle 8.14.5**，腾讯云镜像，JDK 17 目标，Oracle JVM 供应商
- 通过 `buildSrc/` 中的约定插件统一模块配置，各模块按 ID 引用（`buildlogic.java-conventions` 等）
- Spring Boot BOM（`org.springframework.boot:spring-boot-dependencies:4.1.0`）提供受管理的依赖版本
- 仓库顺序：阿里云镜像 → Maven Central → Gradle Plugin Portal → Spring 仓库
- 禁用模块元数据生成（发布优化），版本属性统一在 `gradle.properties` 中管理

### 模块约定

- `projects-core/` 下的模块应用 `buildlogic.java-conventions`，该插件配置 Java 17、`-parameters` javac 标志、JUnit
  Platform、sources JAR、javadoc JAR 及 Spring Boot 依赖管理
- JAR 清单包含 `Module-Name`、`Implementation-Version`、`Build-Jdk-Spec`、`Gradle-Version`
- 处理资源时会将根目录的 `LICENSE.txt` 和 `NOTICE.txt`（如存在）打包进 `META-INF/`

### 添加新模块

1. 在 `settings.gradle` 中用 `include ':projects-core:<模块名>'` 注册
2. 在 `projects-core/<模块名>/build.gradle` 中应用所需的约定插件
3. 如需发布，应用 `buildlogic.maven-central-jar-conventions`（JAR 模块）或 `buildlogic.maven-central-bom-conventions`（BOM
   模块）+ `buildlogic.sonatype-conventions`

### 发布

- 需发布的模块应用 `buildlogic.maven-central-jar-conventions` 或 `buildlogic.maven-central-bom-conventions` +
  `buildlogic.sonatype-conventions`
- 签名使用 GPG（`signing.useGpgCmd()`）
- Sonatype 凭证来自 `sonatypeUsername`/`sonatypePassword` Gradle 属性或 `SONATYPE_USERNAME`/`SONATYPE_PASSWORD` 环境变量
- 发布类型：`AUTOMATIC`

### 关键依赖（bayonet-boot4-starter-common）

- **编译期（compileOnly）**：spring-boot-autoconfigure、spring-boot-starter-*
  （web、security、validation、data-jpa、data-redis、data-mongodb、aspectj、logging）、groovy、lombok
- **API**：slf4j-api
- **注解处理器**：spring-boot-configuration-processor、spring-boot-autoconfigure-processor、lombok
- **测试**：junit-jupiter、spring-boot-starter-test
