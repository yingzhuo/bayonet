---
description: Java 测试规范（JUnit 5 + Mockito + AssertJ）
paths: "**/*Test.java,**/*IT.java,**/*Tests.java,**/*IntegrationTest.java"
---

# Java测试规范

## 测试分级

| 层级   | 后缀                                        | 范围               | 技术栈                         |
|------|-------------------------------------------|------------------|-----------------------------|
| 单元测试 | `*Test.java`                              | 单个类/方法，不启 Spring | JUnit 5 + Mockito + AssertJ |
| 切片测试 | `*Test.java` (@WebMvcTest / @DataJpaTest) | 单一 Spring 层      | Spring Boot Test            |

---

## 通用约定

- 测试框架：**JUnit 5**
- 断言：**AssertJ**（禁止 JUnit 的 `assertEquals` 链式不够直观的那套）
- Mock：**Mockito**，配合 `@ExtendWith(MockitoExtension.class)`
- 测试类名 = 被测类 + `Test`，方法名用 `should_动作_when_条件` 或 backtick 写法

## 覆盖率与质量

- 门槛: 行覆盖 > 80%, 分支覆盖 > 70%
