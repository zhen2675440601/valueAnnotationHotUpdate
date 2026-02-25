package com.zheng.value.valueannoationhotupdate.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhengwl
 * @date 2026/2/25
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "value.annotation.hotupdate")
public class ValueAnnotationProperties {
    /**
     * 是否启用
     */
    private boolean enabled = true;

}

