package com.hiekn.boot.autoconfigure.db;

import com.hiekn.boot.autoconfigure.context.McnPropertiesPostProcessor;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

class MapperScannerRegistry implements ImportBeanDefinitionRegistrar,EnvironmentAware,ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String[] dbs = environment.getProperty(MultipleMybatisAutoConfiguration.PREFIX+"name", String[].class);
        String daoBasePackage = environment.getProperty((McnPropertiesPostProcessor.APP_BASE_PACKAGE_PROPERTY))+".dao.";
        for (String db : dbs) {
            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
            if (resourceLoader != null) {
                scanner.setResourceLoader(resourceLoader);
            }
            scanner.setSqlSessionFactoryBeanName(db+"SqlSessionFactory");
            scanner.registerFilters();
            scanner.doScan(daoBasePackage+db);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
