package com.zheng.value.valueannoationhotupdate.core.listener;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参照 ConfigurationPropertiesRebinder，为 @Value 注解实现热更新
 * 通过监听 EnvironmentChangeEvent 事件触发重新绑定
 *
 * @author zhengwl
 * @date 2025/9/10
 */
@Component
public class ValueBeanCache implements BeanPostProcessor, ApplicationContextAware, PriorityOrdered {

    private ApplicationContext applicationContext;


    // 缓存所有需要处理 @Value 的 Bean 信息
    private final Map<String, BeanValueMetadata> beanValueCache = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public int getOrder() {
        // 设置高优先级，确保在其他 BeanPostProcessor 之前执行
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        // 跳过 @RefreshScope注解的bean
        if (targetClass.isAnnotationPresent(RefreshScope.class)) {
            return bean;
        }
        // 扫描所有字段
        ReflectionUtils.doWithFields(targetClass, field -> {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (valueAnnotation != null) {
                // 提取 ${...} 或 ${...:...} 中的属性名
                String expression = resolveExpression(valueAnnotation.value());
                if (StrUtil.isNotBlank(expression)) {
                    // 缓存字段和其 @Value 表达式
                    beanValueCache.computeIfAbsent(expression, k -> new BeanValueMetadata(beanName, field));
                }
            }
        });
        // 返回原 Bean
        return bean;
    }

    /**
     * 重新绑定Bean 的 @Value 值
     *
     * @param keys 变更配置key值
     * @author zhengwl
     * @date 2025/9/10
     */
    public void rebind(Set<String> keys) {
        for (String key : keys) {
            BeanValueMetadata beanValueMetadata = beanValueCache.get(key);
            if (beanValueMetadata != null) {
                Field field = beanValueMetadata.getField();
                Value valueAnnotation = field.getAnnotation(Value.class);
                Object beanInstance = applicationContext.getBean(beanValueMetadata.getBeanName());
                if (valueAnnotation != null) {
                    try {
                        ReflectionUtils.makeAccessible(field);
                        // 获取原始值
                        String expression = valueAnnotation.value();
                        // 获取最新值
                        Object resolvedValue = resolveValue(expression);
                        // 获得原始值的类型
                        Class<?> fieldType = field.getType();
                        // 转换类型并通过反射更新数据
                        field.set(beanInstance, Convert.convert(fieldType, resolvedValue));

                    } catch (Exception e) {
                        // 报错不更新数据
                    }
                }
            }
        }
    }


    /**
     * 提取 ${...} 或 ${...:...} 中的属性名
     *
     * @param expression 表达式
     * @return java.lang.String
     * @author zhengwl
     * @date 2025/9/11
     */
    private String resolveExpression(String expression) {
        if (!StringUtils.hasText(expression)) {
            return null;
        }

        // 提取 ${...} 或 ${...:...} 中的属性名
        if ((expression.startsWith("${") || expression.startsWith("#{")) && expression.endsWith("}")) {
            int colonIndex = expression.indexOf(':');
            if (colonIndex != -1) {
                // 存在默认值，提取属性名部分
                return expression.substring(2, colonIndex);
            } else {
                // 不存在默认值
                return expression.substring(2, expression.length() - 1);
            }
        }
        return null;
    }


    /**
     * 根据 @Value 表达式从 Environment 解析出实际值
     *
     * @param valueExpression 表达式
     * @return java.lang.Object
     * @author zhengwl
     * @date 2025/9/15
     */
    private Object resolveValue(String valueExpression) {
        if (!StringUtils.hasText(valueExpression)) {
            return null;
        }

        try {
            ConfigurableListableBeanFactory beanFactory =
                    ((ConfigurableApplicationContext) applicationContext).getBeanFactory();

            // 这会将 "${my.property}" 或 "#{someExpression}" 解析为实际的字符串、数字、布尔值等
            return beanFactory.resolveEmbeddedValue(valueExpression);

        } catch (Exception e) {
            // 解析异常返回原始表达式
            return valueExpression;
        }
    }

    /**
     * 内部类：缓存单个 Bean 的 @Value 元数据
     */
    @Data
    @AllArgsConstructor
    private static class BeanValueMetadata {
        private String beanName;
        private Field field;
    }
}
