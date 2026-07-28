# CLAUDE.md

本文件为 ClaudeCode 在此仓库中工作时提供指引。

## 当前版本

`4.1.1` 在生成javadoc文档时，请使用此版本。

## 架构

### 多模块结构

```
+--- ':project-integration-test' - 集成测试 (临时)
\--- ':projects-main'
     +--- ':projects-main:bayonet-bom' - BOM
     +--- ':projects-main:bayonet-boot4-starter-common' - 基础工具和公共代码
     +--- ':projects-main:bayonet-boot4-starter-captcha' - 图形验证码增强
     +--- ':projects-main:bayonet-boot4-starter-freemarker' - FreeMarker模板引擎增强
     +--- ':projects-main:bayonet-boot4-starter-hocon' - HOCON配置文件增强
     +--- ':projects-main:bayonet-boot4-starter-jdbc' - JDBC增强
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
