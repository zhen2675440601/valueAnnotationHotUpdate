package com.zheng.value.valueannoationhotupdate.core.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听配置事件
 */
@Component
public class ValueRefreshListener {

    private static final Logger logger = LoggerFactory.getLogger(ValueRefreshListener.class);


    @Autowired
    private ValueBeanCache valueBeanCache;

    /**
     * 监听Environment
     *
     * @param event 变更事件
     * @author zhengwl
     * @date 2025/9/9
     */
    @EventListener
    public void onEnvironmentChange(EnvironmentChangeEvent event) {
        logger.info("配置环境变化: {}", event.getKeys());
        // 触发 @Value 重新绑定
        valueBeanCache.rebind(event.getKeys());
    }
}