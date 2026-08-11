# AGENTS.md

本文件为 Codex 在此仓库中工作时提供指引。

## 当前版本

`4.1.1` 在生成 javadoc 文档时，请使用此版本。

## 架构

### 多模块结构

```
+--- ':project-integration-test' - 集成测试 (临时)
\--- ':projects-main'
     +--- ':projects-main:bayonet-bom' - BOM
     +--- ':projects-main:bayonet-boot4-starter-common' - 基础工具和公共代码
     +--- ':projects-main:bayonet-boot4-starter-actuator' - SpringBoot Actuator 增强
     +--- ':projects-main:bayonet-boot4-starter-captcha' - 图形验证码增强
     +--- ':projects-main:bayonet-boot4-starter-freemarker' - FreeMarker模板引擎增强
     +--- ':projects-main:bayonet-boot4-starter-config-hocon' - HOCON配置文件增强
     +--- ':projects-main:bayonet-boot4-starter-config-toml' - TOML配置文件增强
     +--- ':projects-main:bayonet-boot4-starter-jwt' - JWT增强
     +--- ':projects-main:bayonet-boot4-starter-jwt-sm2' - JWT增强 - SM2加密
     +--- ':projects-main:bayonet-boot4-starter-security' - SpringSecurity增强
     +--- ':projects-main:bayonet-boot4-starter-security-sm3' - SpringSecurity增强 - SM3加密
     +--- ':projects-main:bayonet-boot4-starter-validation' - Validation增强
     +--- ':projects-main:bayonet-boot4-starter-webcli' - WebClient增强
     +--- ':projects-main:bayonet-boot4-starter-webcli-apache5' - WebClient增强 - Apache5支持
     +--- ':projects-main:bayonet-boot4-starter-webmvc' - WebMvc增强
     \--- ':projects-main:bayonet-boot4-starter-zxing' - 二维码条形码增强
```

## 编码风格

适用于 `**/*.java`。

### 语言级别

- Java 17, 禁止 preview 特性
- 不影响可读性时局部变量优先 `var`
- 遵循 Google Java Style Guide（google-java-format）
- 缩进 4 空格，行宽 100，禁止 tab，禁止尾随空格

### 依赖

- 禁止直接引入不在 `gradle/libs.versions.toml` 管理的第三方依赖

### 版权

- 禁止在源文件中添加版权声明或许可证头部注释
- 合规信息统一放在根目录 `LICENSE.txt`

### 命名

- 类名 UpperCamelCase，方法/变量 lowerCamelCase
- 常量 `UPPER_SNAKE_CASE`

### Lombok

- 使用 `@Slf4j`、`@RequiredArgsConstructor`、`@Getter`、`@Setter`

### 返回值

- 集合返回 empty 集合，禁止返回 null
- 单值可能不存在 → `Optional<T>`，但禁止字段类型和方法参数用 Optional

### Javadoc

- 代码片段使用 `{@code ...}`（而非 `<code>...</code>`），`{@code}` 内 `@`、`<`、`>` 等特殊字符按字面量处理， **禁止**使用 HTML
  实体转义（如 `&#64;`、`&lt;` 等）
- 示例代码块用 `<pre>{@code ...}</pre>` 包裹，内部 `@` 直接书写，禁止 `&#64;` 或 `&#064;`

### 避免 NPE

- 每个 package 必须含 `package-info.java`，使用 `@NullMarked`
- 使用 `org.jspecify.annotations.Nullable`，不使用其他变体
- 所有可能返回 `null` 的方法必须标注 `@Nullable`，不可遗漏

### 类型选择

- 在表达TTL等时间跨度时，使用 `Duration` 而不是 `long` 型毫秒
- 在使用 `long` 型字面变量时，使用 'L' 后缀: 10_000L
- 在使用 `double` 型字面变量时，使用 'D' 后缀: 1.0D

### 线程安全

- 本库有可能用户生产，要考虑到线程安全的情况。
- 尽量不要在方法上使用 `synchronized` 关键字。使用 DCL + volatile 模式

## Git 操作规范

适用于所有文件。

### 总原则

- **所有 git 操作必须经过用户确认**, 不得自动执行 commit、push、merge 等操作
- 提交前必须运行完整测试 (`make test`), 确保绿色通过
- 禁止对 `main` / `master` / `release/*` 分支直接 push, 必须通过 PR/MR

### 提交信息格式

采用 Conventional Commits 规范:

#### type 取值

- `feat`: 新功能
- `fix`: Bug修复
- `refactor`: 重构（不新增功能也不修 bug）
- `test`: 增加测试
- `docs`: 文档变更
- `chore`: 构建/CI/工具变更
- `style`: 代码格式（不影响逻辑）
- `perf`: 性能优化

#### subject 要求

- 使用简体中文
- 祈使句
- 不超过72字符

#### body (可选)

- 解释 Why 而非 What
- 关联 issue 编号：`Closes #123`

### 提交粒度

- **一个提交只做一件事**：不要混合 feat + fix + refactor
- 如果一次对话生成了多个独立改动，应拆分为多个提交，而不是一个大提交
- 提交前先 `git diff --stat` 确认改动范围合理

### 分支命名

- 功能分支：`feat/<issue-number>-<short-desc>`（如 `feat/456-coupon`）
- Bug 修复分支：`fix/<issue-number>-<short-desc>`
- 禁止在 `main` 上直接开发，必须先切分支

### 合并策略

- 优先使用 **rebase** 而非 merge，保持历史线性
- 合并前确保分支基于最新的 `main` (`git rebase main`)
- 合并后删除远程分支 (`git push origin --delete <branch>`)

### 检查清单（每次 commit 前自查）

- 是否已运行测试并通过？
- 提交信息是否符合 Conventional Commits 格式？
- 本次改动是否只聚焦一个目的？
- 是否有未跟踪的敏感文件？
- 当前分支是否是 `main`？如果是，拒绝直接提交。

### 不要做什么

- 没有我的明确要求不要提交代码，更不要推送

### 重要提示

- **所有 git 操作（commit、push、merge、rebase 等）都必须经过我明确授权**，即使规则文件写了可以做，也必须先问过我
- 没有我的口头或书面命令，不要执行任何提交或推送操作

## Gradle 规范

适用于所有通过 Bash 工具调用 Gradle（`gradle` / `gradlew`）的场景：构建、测试、依赖解析、任务查询等。

### 核心规则

**所有 Gradle 调用必须追加 `--console=plain`**，禁用 ANSI 颜色、光标移动和进度条重绘，产出纯文本输出。

- 纯文本日志解析稳定，不会被转义序列污染
- 无进度条重绘，避免同一行反复覆盖导致输出错乱
- 这是 Gradle 在非交互终端下的官方推荐模式

### 标准调用模板

```bash
# 编译
./gradlew classes --console=plain

# 构建
./gradlew build --console=plain

# 运行测试
./gradlew test --console=plain

# 查看任务
./gradlew tasks --console=plain

# 清理
./gradlew clean --console=plain

# 单个测试
./gradlew :projects-main:bayonet-boot4-starter-common:test --tests "com.github.yingzhuo.SomeTest" --console=plain

./gradlew :projects-main:bayonet-boot4-starter-common:test --tests "com.github.yingzhuo.SomeTest#testMethod" --console=plain
```

### 日志级别配合

根据任务类型选择合适的日志级别，避免噪音淹没关键信号：

| 场景                | 参数                                  | 说明                   |
|---------------------|---------------------------------------|------------------------|
| 常规构建/测试       | （默认 `lifecycle`）                  | 进度信息但不冗余       |
| 需要诊断失败原因    | `--info`                              | 详细流程信息           |
| 排查依赖/类路径问题 | `--debug`                             | 全量调试输出，谨慎使用 |
| 报错需堆栈          | `--stacktrace` 或 `--full-stacktrace` | 追根因时用             |

示例：

```bash
./gradlew build --console=plain --stacktrace
./gradlew test --console=plain --info
```

### 需要避开的陷阱

- **不要依赖 `NO_COLOR` 环境变量**替代 `--console=plain`。`NO_COLOR` 只抑制颜色， **进度条和光标移动等富输出仍然会输出**
  ，照样干扰解析。
- **不要使用 `--console=rich` 或 `--console=auto`**。这两种模式会启用进度条和动画，在 Agent 的 Bash 工具里表现为乱码式的重绘序列。
- **不要在命令中混入 `--console=colored` 除非用户明确要求保留颜色**。`colored` 模式有颜色无进度条，对人类终端友好，但对
  Agent 仍有多余的 ANSI 转义。

### 失败处理流程

当 Gradle 构建失败时，按以下顺序排查：

1. 先看末尾的 `BUILD FAILED` 摘要，定位失败任务
2. 对失败任务追加 `--stacktrace` 重跑，获取完整堆栈
3. 若是测试失败，查看 `build/reports/tests/` 下的 HTML 报告
4. 若是依赖问题，用 `dependencies --console=plain` 输出依赖树

### 例外情况

仅在用户明确要求"我要看颜色/进度条"或当前会话确认是交互式人类终端时，才可不带 `--console=plain`。除此之外一律默认加上。

## 测试规范

适用于 `**/*Test.java,**/*IT.java,**/*Tests.java,**/*IntegrationTest.java`。

### 测试分级

| 层级     | 后缀                                      | 范围                     | 技术栈                      |
|----------|-------------------------------------------|--------------------------|-----------------------------|
| 单元测试 | `*Test.java`                              | 单个类/方法，不启 Spring | JUnit 5 + Mockito + AssertJ |
| 切片测试 | `*Test.java` (@WebMvcTest / @DataJpaTest) | 单一 Spring 层           | Spring Boot Test            |

### 通用约定

- 测试框架： **JUnit 5**
- 断言： **AssertJ**（禁止 JUnit 的 `assertEquals` 链式不够直观的那套）
- Mock： **Mockito**，配合 `@ExtendWith(MockitoExtension.class)`
- 测试类名 = 被测类 + `Test`，方法名用 `should_动作_when_条件` 或 backtick 写法

### 覆盖率与质量

- 门槛: 行覆盖 > 80%, 分支覆盖 > 70%

## 常用构建命令

仓库提供 `makefile` 封装，优先使用：

| 命令           | 作用                            |
|----------------|---------------------------------|
| `make compile` | 编译主代码                      |
| `make test`    | 运行测试                        |
| `make build`   | 编译并打包（跳过测试）          |
| `make install` | 发布到本地 Maven 仓库           |
| `make publish` | 发布到 Maven 中央仓库（需确认） |
| `make clean`   | 删除构建产物                    |
