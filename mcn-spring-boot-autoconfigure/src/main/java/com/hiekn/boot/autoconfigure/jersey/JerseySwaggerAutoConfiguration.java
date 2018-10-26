package com.hiekn.boot.autoconfigure.jersey;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.hiekn.boot.autoconfigure.base.exception.handler.BaseExceptionHandler;
import com.hiekn.boot.autoconfigure.base.exception.handler.ExceptionHandler;
import com.hiekn.boot.autoconfigure.base.exception.handler.ValidationExceptionMapper;
import com.hiekn.boot.autoconfigure.base.exception.handler.WebApplicationExceptionHandler;
import com.hiekn.boot.autoconfigure.base.filter.CheckCertificateFilter;
import com.hiekn.boot.autoconfigure.base.rest.SwaggerView;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.apache.ibatis.session.SqlSessionFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
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
import java.util.Objects;
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

            Set<Class<?>> allClasses = Sets.newHashSet(JacksonJsonProvider.class,ValidationExceptionMapper.class,ExceptionHandler.class,BaseExceptionHandler.class, WebApplicationExceptionHandler.class);
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
    @ConditionalOnClass(name={"io.swagger.jaxrs.listing.ApiListingResource"})
    @ConditionalOnProperty(prefix = "jersey.swagger", name = {"init"}, havingValue = "true", matchIfMissing = true)
    public ResourceConfigCustomizer initSwagger2() {
        return config -> {
            config.registerClasses(ApiListingResource.class, SwaggerSerializers.class);
            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setVersion(jersey.getVersion());
            beanConfig.setTitle(jersey.getTitle());
            if(Objects.nonNull(jersey.getHost())){
                beanConfig.setHost(jersey.getHost());
            }else{
                beanConfig.setHost(jersey.getIp() + ":" + jersey.getPort());
            }
            beanConfig.setBasePath(jersey.getBasePath());
            beanConfig.setResourcePackage(jersey.getResourcePackage());
            beanConfig.setScan();
        };
    }

    @Bean
    @ConditionalOnClass(name={"org.glassfish.jersey.media.multipart.MultiPartFeature"})
    public ResourceConfigCustomizer registerMultiPartFeature() {
        return config -> config.registerClasses(MultiPartFeature.class);
    }

    @Bean
    @ConditionalOnClass(name={"org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature"})
    @ConditionalOnBean(name = "initSwagger2")
    public ResourceConfigCustomizer initSwagger2UI() {
        return config ->
            config.property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "META-INF/resources/swagger")
                .property(FreemarkerMvcFeature.CACHE_TEMPLATES, new Boolean(false))
                .registerClasses(FreemarkerMvcFeature.class,SwaggerView.class)
        ;
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnClass(name = "org.springframework.web.cors.CorsConfigurationSource")
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
    @ConditionalOnClass(name = "com.hiekn.licence.verify.VerifyLicense")
    public FilterRegistrationBean checkCertificateFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CheckCertificateFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    @ConditionalOnClass(name={"org.glassfish.jersey.client.JerseyClient","org.glassfish.jersey.media.multipart.MultiPartFeature"})
    @ConditionalOnMissingBean(name = "jerseyHttp")
    public JerseyHttp jerseyHttp(JerseyClientProperties clientProperties) {
        return new JerseyHttp(clientProperties);
    }

    @Configuration
    @ConditionalOnClass({DataSource.class,SqlSessionFactory.class,MapperScan.class})
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    @ConditionOnSingleDatasource
    public static class QuickConfigMapperScan{

    }

}
