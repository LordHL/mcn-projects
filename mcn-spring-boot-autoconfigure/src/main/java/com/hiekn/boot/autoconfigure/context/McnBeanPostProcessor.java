package com.hiekn.boot.autoconfigure.context;

import com.hiekn.boot.autoconfigure.base.service.Autowired2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * mainly deal annotation @Autowired2
 */
public class McnBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof AutowiredAnnotationBeanPostProcessor){
            AutowiredAnnotationBeanPostProcessor abf = (AutowiredAnnotationBeanPostProcessor) bean;
            ReflectionUtils.doWithLocalFields(abf.getClass(),(f) -> {
                if(f.getName().equals("autowiredAnnotationTypes")){
                    ReflectionUtils.makeAccessible(f);
                    Set<Class<? extends Annotation>> autowiredAnnotationTypes = (Set<Class<? extends Annotation>>)f.get(abf);
                    autowiredAnnotationTypes.add(Autowired2.class);
                }
            });
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}