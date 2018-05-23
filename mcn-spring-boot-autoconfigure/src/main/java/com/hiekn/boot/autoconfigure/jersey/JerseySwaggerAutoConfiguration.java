package com.hiekn.boot.autoconfigure.jersey;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.hiekn.boot.autoconfigure.base.exception.handler.BaseExceptionHandler;
import com.hiekn.boot.autoconfigure.base.exception.handler.ExceptionHandler;
import com.hiekn.boot.autoconfigure.base.exception.handler.ValidationExceptionMapper;
import com.hiekn.boot.autoconfigure.base.exception.handler.WebApplicationExceptionHandler;
import com.hiekn.boot.autoconfigure.context.McnPropertiesPostProcessor;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import javax.ws.rs.Path;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(
        name = {"org.glassfish.jersey.server.spring.SpringComponentProvider", "javax.servlet.ServletRegistration"}
)
@ConditionalOnMissingBean(
        type = {"org.glassfish.jersey.server.ResourceConfig"}
)
@EnableConfigurationProperties({JerseySwaggerProperties.class, JerseyClientProperties.class})
@AutoConfigureBefore(JerseyAutoConfiguration.class)
public class JerseySwaggerAutoConfiguration extends ResourceConfig {

    private final JerseySwaggerProperties jersey;

    public JerseySwaggerAutoConfiguration(JerseySwaggerProperties jersey) {
        this.jersey = jersey;
    }

    @Bean
    public ResourceConfigCustomizer resourceRegister() {
        return config -> {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
            scanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));
            String otherResourcePackage = jersey.getOtherResourcePackage();
            Set<String> packages = Sets.newHashSet(jersey.getBasePackage());
            if (StringUtils.hasLength(otherResourcePackage)) {
                for (String className : StringUtils.tokenizeToStringArray(otherResourcePackage, ",")) {
                    packages.add(className);
                }
            }

            Set<Class<?>> allClasses = Sets.newHashSet(MultiPartFeature.class, JacksonJsonProvider.class,ValidationExceptionMapper.class,ExceptionHandler.class,BaseExceptionHandler.class, WebApplicationExceptionHandler.class);

            for (String pkg : packages) {
                Set<Class<?>> collect = scanner.findCandidateComponents(pkg).stream()
                        .map(beanDefinition -> ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), this.getClassLoader()))
                        .collect(Collectors.toSet());
                allClasses.addAll(collect);
            }

            //添加异常处理器
            String classes = jersey.getSingleResource();
            if (StringUtils.hasLength(classes)) {
                allClasses.add(getExceptionHandlerClass(classes));
            }

            config.registerClasses(allClasses)
                    .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
                    .property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
        };
    }

    private Class<?> getExceptionHandlerClass(String className) throws LinkageError {
        try {
            Class<?> exceptionClass = ClassUtils.forName(className,
                    ClassUtils.getDefaultClassLoader());
            Assert.isAssignable(ExceptionMapper.class, exceptionClass);
            return exceptionClass;
        } catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load exception handler class [" + className + "]", ex);
        }
    }

    @Bean
    @ConditionalOnClass({ApiListingResource.class, SwaggerSerializers.class})
    @ConditionalOnProperty(prefix = "jersey.swagger", name = {"init"}, havingValue = "true", matchIfMissing = true)
    public BeanConfig initSwagger(JerseySwaggerAutoConfiguration jerseySwaggerConfig) {
        jerseySwaggerConfig.registerClasses(Sets.newHashSet(ApiListingResource.class, SwaggerSerializers.class));
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(jersey.getVersion());
        beanConfig.setTitle(jersey.getTitle());
        beanConfig.setHost(jersey.getIp() + ":" + jersey.getPort());
        beanConfig.setBasePath(jersey.getBasePath());
        beanConfig.setResourcePackage(jersey.getResourcePackage());
        beanConfig.setScan();
        return beanConfig;
    }

    @Bean
    @ConditionalOnClass(UrlBasedCorsConfigurationSource.class)
    @ConditionalOnProperty(prefix = "filter", name = {"cross"}, havingValue = "true", matchIfMissing = true)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean
    @ConditionalOnClass(JerseyClient.class)
    @ConditionalOnMissingBean(name = "jerseyHttp")
    public JerseyHttp jerseyHttp(JerseyClientProperties clientProperties) {
        return new JerseyHttp(clientProperties);
    }

    @Configuration
    @Import({ AutoConfiguredMapperScannerRegistrar.class })
    @ConditionalOnClass({DataSource.class,SqlSessionFactory.class,MapperScan.class})
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class QuickConfigMapperScan{

    }

    public static class AutoConfiguredMapperScannerRegistrar
            implements EnvironmentAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private Environment environment;

        private ResourceLoader resourceLoader;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            scanner.registerFilters();
            String daoPackage = environment.getProperty((McnPropertiesPostProcessor.APP_BASE_PACKAGE_PROPERTY))+".dao";
            scanner.doScan(daoPackage);
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }
    }
}
