package com.hiekn.boot.autoconfigure.context;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.DefaultResourceLoader;

import java.beans.Introspector;

public class McnApplicationListener implements GenericApplicationListener {

    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;
    private int order = DEFAULT_ORDER;

    private static Class<?>[] EVENT_TYPES = { ApplicationStartingEvent.class, ApplicationEnvironmentPreparedEvent.class,
            ApplicationPreparedEvent.class, ApplicationFailedEvent.class };

    private static Class<?>[] SOURCE_TYPES = { SpringApplication.class };

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if(applicationEvent instanceof ApplicationStartingEvent){

        }
        if(applicationEvent instanceof ApplicationEnvironmentPreparedEvent){

        }
        if(applicationEvent instanceof ApplicationPreparedEvent){
            onApplicationPreparedEvent(applicationEvent);
        }
        if(applicationEvent instanceof ApplicationFailedEvent){

        }
    }

    private void onApplicationPreparedEvent(ApplicationEvent applicationEvent) {
        SpringApplication application = ((ApplicationPreparedEvent) applicationEvent).getSpringApplication();
        ConfigurableApplicationContext context = ((ApplicationPreparedEvent) applicationEvent).getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        String name = application.getMainApplicationClass().getSimpleName();
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(Introspector.decapitalize(name));
        if(beanDefinition instanceof AnnotatedGenericBeanDefinition){
            AnnotatedGenericBeanDefinition df = (AnnotatedGenericBeanDefinition)beanDefinition;
            if(!df.getMetadata().hasAnnotation(MapperScan.class.getName())){//check is quick config mapper scanner
                ClassPathMapperScanner scanner = new ClassPathMapperScanner((BeanDefinitionRegistry)beanFactory);
                scanner.setResourceLoader(new DefaultResourceLoader(context.getClassLoader()));
                scanner.registerFilters();
                String daoPackage = context.getEnvironment().getProperty((McnPropertiesPostProcessor.APP_BASE_PACKAGE_PROPERTY))+".dao";
                scanner.doScan(daoPackage);
            }
        }
    }

    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
