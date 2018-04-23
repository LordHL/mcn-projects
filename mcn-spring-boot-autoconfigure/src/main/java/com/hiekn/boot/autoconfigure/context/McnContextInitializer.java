package com.hiekn.boot.autoconfigure.context;

import com.google.gson.Gson;
import com.hiekn.boot.autoconfigure.base.util.JsonUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE + 9)
public class McnContextInitializer implements ApplicationContextInitializer{

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        Gson gson = new Gson();
        configurableApplicationContext.getBeanFactory().registerSingleton("gson",gson);
        configurableApplicationContext.getBeanFactory().registerSingleton("jsonUtils",new JsonUtils(gson));

//        configurableApplicationContext.addApplicationListener(event -> {
//        });
    }
}
