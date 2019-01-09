package com.hiekn.boot.autoconfigure.jersey;

import com.google.common.collect.Sets;
import com.hiekn.boot.autoconfigure.web.filter.JerseyXssFilter;
import com.hiekn.boot.autoconfigure.web.rest.SwaggerView;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

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

    private static final String EXCEPTION_HANDLER_PACKAGE = "com.hiekn.boot.autoconfigure.web.exception.handler";

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
            Set<String> packages = Sets.newHashSet(jersey.getBasePackage(),EXCEPTION_HANDLER_PACKAGE);
            if (StringUtils.hasLength(otherResourcePackage)) {
                for (String className : StringUtils.tokenizeToStringArray(otherResourcePackage, ",")) {
                    packages.add(className);
                }
            }
            Set<Class<?>> allClasses = Sets.newHashSet(JacksonFeature.class);//JacksonJsonProvider.class
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

            //register MultiPartFeature
            if(ClassUtils.isPresent("org.glassfish.jersey.media.multipart.MultiPartFeature",null)){
                config.registerClasses(MultiPartFeature.class);
            }

            //register MultiPartFeature
            if(jersey.getXss() && ClassUtils.isPresent("org.jsoup.Jsoup",null)){
                config.registerClasses(JerseyXssFilter.class);
            }

            //init swagger
            initSwagger(config);

        };
    }

    private Class<?> getExceptionHandlerClass(String className) throws LinkageError {
        try {
            Class<?> exceptionClass = ClassUtils.forName(className, null);
            Assert.isAssignable(ExceptionMapper.class, exceptionClass);
            return exceptionClass;
        } catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load exception handler class [" + className + "]", ex);
        }
    }

    private void initSwagger(ResourceConfig config){
        if(jersey.getInit() && ClassUtils.isPresent("io.swagger.jaxrs.listing.ApiListingResource",null)){
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
            if(ClassUtils.isPresent("org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature",null)){
                config.property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, "META-INF/resources/Swagger")
                        .property(FreemarkerMvcFeature.CACHE_TEMPLATES, new Boolean(false))
                        .registerClasses(FreemarkerMvcFeature.class,SwaggerView.class);
            }
        }
    }

    @Bean
    @ConditionalOnClass(name={"org.glassfish.jersey.client.JerseyClient","org.glassfish.jersey.media.multipart.MultiPartFeature"})
    @ConditionalOnMissingBean(name = "jerseyHttp")
    public JerseyHttp jerseyHttp(JerseyClientProperties clientProperties) {
        return new JerseyHttp(clientProperties);
    }

}
