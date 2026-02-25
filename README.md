# Value Annotation Hot Update Spring Boot Starter

## 项目简介

本项目是一个基于 Spring Boot 的 Starter，旨在为 `@Value` 注解提供热更新功能。通过监听配置变更事件，动态刷新使用 `@Value` 注解注入的字段值，无需重启应用即可生效。

## 功能特性

- ✅ 支持 `@Value` 注解的热更新  
- ✅ 自动监听配置变更事件  
- ✅ 无需重启应用即可刷新配置  
- ✅ 支持默认值配置  
- ✅ 易于集成到现有 Spring Boot 项目中  

## 快速开始

### 1. 添加依赖

在你的pom.xml中添加以下依赖：

```
xml
<dependency>
<groupId>com.zheng.value</groupId>
<artifactId>value-annotation-hotupdate-spring-boot-starter</artifactId>
<version>1.0.0</version>
</dependency>
```
### 2. 配置文件

在 `application.yml` 或 `application.properties` 中启用热更新功能：

```
yaml
value:
annotation:
hotupdate:
enabled: true
```
### 3. 使用示例

在你的 Controller 或 Service 中使用 `@Value` 注解：

```
java
@RestController
public class TestController {

    @Value("${test.color:red}")
    private String color;

    @GetMapping("/test")
    public String test() {
        return "Color: " + color;
    }
}
```
当配置发生变化时，字段会自动更新，无需重启应用。

## 配置项说明

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `value.annotation.hotupdate.enabled` | Boolean | `true` | 是否启用热更新功能 |

## 核心组件

- [ValueBeanCache](file://D:\zword\otherJavaProject\ValueAnnoationHotUpdate\src\main\java\com\zheng\value\valueannoationhotupdate\core\listener\ValueBeanCache.java#L33-L176): 缓存所有使用 `@Value` 注解的字段信息，并在配置变更时重新绑定值。  
- [ValueRefreshListener](file://D:\zword\otherJavaProject\ValueAnnoationHotUpdate\src\main\java\com\zheng\value\valueannoationhotupdate\core\listener\ValueRefreshListener.java#L12-L34): 监听配置变更事件，触发 [ValueBeanCache](file://D:\zword\otherJavaProject\ValueAnnoationHotUpdate\src\main\java\com\zheng\value\valueannoationhotupdate\core\listener\ValueBeanCache.java#L33-L176) 的重新绑定操作。  

## 开发指南

### 本地构建

```
bash
mvn clean install
```
### 运行测试

```
bash
mvn test
```
## 贡献

欢迎提交 Issue 和 Pull Request 来改进本项目！

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源协议。

## 联系方式

如有问题，请联系：[2675440601@qq.com](mailto:2675440601@qq.com)
```