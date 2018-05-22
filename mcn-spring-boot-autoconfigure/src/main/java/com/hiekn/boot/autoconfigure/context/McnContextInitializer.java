package com.hiekn.boot.autoconfigure.context;

import com.hiekn.boot.autoconfigure.base.util.BeanUtils;
import com.hiekn.boot.autoconfigure.mybatis.MultiplyDataSourceRegistryPostProcessor;
import com.hiekn.boot.autoconfigure.mybatis.MultiplyMybatisAutoConfiguration;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

@Order(McnApplicationListener.DEFAULT_ORDER)
public class McnContextInitializer implements ApplicationContextInitializer{

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        //set beanUtils
        configurableApplicationContext.addApplicationListener((ContextRefreshedEvent event) -> new BeanUtils().setApplicationContext(event.getApplicationContext()));

        //check is config multiply datasource
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        String[] dbs = environment.getProperty(MultiplyMybatisAutoConfiguration.PREFIX+"name", String[].class);
        if(dbs != null && dbs.length > 1){
            configurableApplicationContext.addBeanFactoryPostProcessor(new MultiplyDataSourceRegistryPostProcessor(configurableApplicationContext.getEnvironment()));
        }
    }
}
