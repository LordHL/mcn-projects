package com.hiekn.boot.autoconfigure.context;

import com.hiekn.boot.autoconfigure.base.service.Autowired2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * mainly deal annotation @Autowired2
 */
public class McnBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof AutowiredAnnotationBeanPostProcessor){
            AutowiredAnnotationBeanPostProcessor abf = (AutowiredAnnotationBeanPostProcessor) bean;
            Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(5);
            autowiredAnnotationTypes.add(Autowired.class);
            autowiredAnnotationTypes.add(Value.class);
            autowiredAnnotationTypes.add(Autowired2.class);
            try {
                autowiredAnnotationTypes.add((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
            }
            catch (ClassNotFoundException ex) {
                // JSR-330 API not available - simply skip.
            }
            abf.setAutowiredAnnotationTypes(autowiredAnnotationTypes);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}