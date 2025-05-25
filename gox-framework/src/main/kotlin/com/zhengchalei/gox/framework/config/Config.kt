package com.zhengchalei.gox.framework.config

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.stereotype.Component

@Component
class Config : BeanFactoryPostProcessor {
    @Throws(BeansException::class)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val jimmeBeanDefinition = beanFactory.getBeanDefinition("sqlClient")
        jimmeBeanDefinition.setDependsOn("liquibase")
    }
}