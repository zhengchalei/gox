package com.zhengchalei.gox.framework.config

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.stereotype.Component

@Component
class LiquibaseBeforeJimmerConfig : BeanFactoryPostProcessor {
    @Throws(BeansException::class)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // 强制让 sqlClient Bean 的初始化 依赖于另一个名称完全等于 "liquibase" 的 Bean
        val jimmeBeanDefinition = beanFactory.getBeanDefinition("sqlClient")
        jimmeBeanDefinition.setDependsOn("liquibase")
    }
}