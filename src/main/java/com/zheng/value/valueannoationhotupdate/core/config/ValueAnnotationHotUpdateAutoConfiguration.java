package com.zheng.value.valueannoationhotupdate.core.config;

import com.zheng.value.valueannoationhotupdate.core.listener.ValueBeanCache;
import com.zheng.value.valueannoationhotupdate.core.listener.ValueRefreshListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhengwl
 * @date 2026/2/25
 */
@Configuration
@ConditionalOnClass({ValueBeanCache.class, ValueRefreshListener.class})
@EnableConfigurationProperties(ValueAnnotationProperties.class)
public class ValueAnnotationHotUpdateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ValueBeanCache valueBeanCache() {
        return new ValueBeanCache();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValueRefreshListener valueRefreshListener() {
        return new ValueRefreshListener();
    }
}

