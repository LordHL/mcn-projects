package com.hiekn.boot.autoconfigure.context;

import com.hiekn.boot.autoconfigure.base.util.BeanUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;

@Order(McnApplicationListener.DEFAULT_ORDER)
public class McnContextInitializer implements ApplicationContextInitializer{

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

        configurableApplicationContext.addApplicationListener((ContextRefreshedEvent event) -> {
            new BeanUtils().setApplicationContext(event.getApplicationContext());
        });

    }
}
