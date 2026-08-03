/*
 * 关于 Spring Boot Actuator 自定义端点机制的心得（Spring Boot 4.1.1 源码确认）：
 *
 * 1. 端点 id 必须以小写字母开头（EndpointId 构造器断言）。
 *    - 写成 "securityProviders"（大写 S 开头）会抛 IllegalArgumentException，应用启动直接失败。
 *    - 因此 id 约定为全小写连写（securityproviders），与内置 configprops、threaddump 一致。
 *    - 连字符形式（security-providers）属于 legacy 命名，会触发警告日志，
 *      且开启 management.endpoints.migrate-legacy-ids 时连字符会被移除。
 *
 * 2. 操作方法参数名的解析来源不止 -parameters 一个。
 *    - DefaultParameterNameDiscoverer = -parameters 编译标志 + debug 信息的 LocalVariableTable。
 *    - javac 默认生成 LocalVariableTable（除非显式 -g:none），
 *      所以常规构建（Maven/Gradle/IDE 默认）即使不依赖 -parameters 也能解析出参数名。
 *    - 若两者都缺失（-g:none 且无 -parameters），OperationMethodParameters 构造里的
 *      Assert.state(parameterNames != null) 会抛异常 → 端点启动失败。
 *
 * 3. @Name 注解在 Spring Boot 4.1 已移除，没有替代注解。
 *    - 端点操作参数只支持简单类型（String / int / boolean 等），Map、POJO 不能作为输入参数，
 *      因此不存在"显式声明参数名"的注解方案。
 *
 * 4. 参数是否必填由 @Nullable 注解决定（OperationMethodParameter.isMandatory()）。
 *    - 该方法识别任意包的 @Nullable（包括 org.jspecify.annotations.Nullable）。
 *    - 标注 @Nullable → 参数可选，缺失时传 null（如本方法的 excludeSun）。
 *    - 不标注 → 参数必填，缺失时 web 端点返回 HTTP 500 "missing required parameters"。
 */
@NullMarked
package com.github.yingzhuo.bayonet.actuator;

import org.jspecify.annotations.NullMarked;
