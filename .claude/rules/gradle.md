---
description: 规范 Gradle 调用方式，确保输出对 Claude Code 友好
---

# Gradle 规范

## 适用范围

本规则适用于所有通过 Bash 工具调用 Gradle（`gradle` / `gradlew`）的场景：构建、测试、依赖解析、任务查询等。

## 核心规则

**所有 Gradle 调用必须追加 `--console=plain`**，禁用 ANSI 颜色、光标移动和进度条重绘，产出纯文本输出。

- 纯文本日志解析稳定，不会被转义序列污染
- 无进度条重绘，避免同一行反复覆盖导致输出错乱
- 这是 Gradle 在非交互终端下的官方推荐模式

## 标准调用模板

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

## 日志级别配合

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

## 需要避开的陷阱

- **不要依赖 `NO_COLOR` 环境变量**替代 `--console=plain`。`NO_COLOR` 只抑制颜色， **进度条和光标移动等富输出仍然会输出**
  ，照样干扰解析。
- **不要使用 `--console=rich` 或 `--console=auto`**。这两种模式会启用进度条和动画，在 Agent 的 Bash 工具里表现为乱码式的重绘序列。
- **不要在命令中混入 `--console=colored` 除非用户明确要求保留颜色**。`colored` 模式有颜色无进度条，对人类终端友好，但对
  Agent 仍有多余的 ANSI 转义。

## 失败处理流程

当 Gradle 构建失败时，按以下顺序排查：

1. 先看末尾的 `BUILD FAILED` 摘要，定位失败任务
2. 对失败任务追加 `--stacktrace` 重跑，获取完整堆栈
3. 若是测试失败，查看 `build/reports/tests/` 下的 HTML 报告
4. 若是依赖问题，用 `dependencies --console=plain` 输出依赖树

## 例外情况

仅在用户明确要求"我要看颜色/进度条"或当前会话确认是交互式人类终端时，才可不带 `--console=plain`。除此之外一律默认加上。
