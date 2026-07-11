---
description: 编码风格
paths: "**/*.java"
---

# 编码风格

## 语言级别

- Java 17, 禁止 preview 特性
- 不影响可读性时局部变量优先 `var`
- 遵循 Google Java Style Guide（google-java-format）
- 缩进 4 空格，行宽 100，禁止 tab，禁止尾随空格
- 导入顺序：static → java → javax → org → com → 其他

## 依赖

- 禁止直接引入不在 BOM 管理的第三方依赖

## 版权

- 禁止在源文件中添加版权声明或许可证头部注释
- 合规信息统一放在根目录 `LICENSE.txt`

## 命名

- 类名 UpperCamelCase，方法/变量 lowerCamelCase
- 常量 `UPPER_SNAKE_CASE`
- 禁止魔法值，提取为常量或枚举

## Lombok

- 使用 `@Slf4j`、`@RequiredArgsConstructor`、`@Getter`、`@Setter`
- `@Builder` 限 DTO/VO/BO；Entity 不建或用 `@Builder(toBuilder = true)`

## 返回值

- 集合返回 empty 集合，禁止返回 null
- 单值可能不存在 → `Optional<T>`，但禁止字段类型和方法参数用 Optional

## 避免 NPE

- 每个 package 必须含 `package-info.java`，使用 `@NullMarked`
- 使用 `org.jspecify.annotations.Nullable`，不使用其他变体
