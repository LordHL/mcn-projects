package com.hiekn.boot.autoconfigure.jersey;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hiekn.boot.autoconfigure.base.exception.BaseException;
import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import com.hiekn.boot.autoconfigure.base.util.ErrorMsgUtil;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(
        name = {"org.glassfish.jersey.server.spring.SpringComponentProvider", "javax.servlet.ServletRegistration"}
)
@ConditionalOnMissingBean(
        type = {"org.glassfish.jersey.server.ResourceConfig"}
)
@EnableConfigurationProperties({JerseySwaggerProperties.class,JerseyClientProperties.class})
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
            System.out.println(isRegistered(ExceptionHandler.class));
            System.out.println(isRegistered(ExceptionMapper.class));
            if(StringUtils.hasLength(otherResourcePackage)){
                for (String className : StringUtils.tokenizeToStringArray(otherResourcePackage, ",")) {
                    packages.add(className);
                }
            }
            for (String pkg : packages) {
                Set<Class<?>> collect = scanner.findCandidateComponents(pkg).stream()
                        .map(beanDefinition -> ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), this.getClassLoader()))
                        .collect(Collectors.toSet());
                registerClasses(collect);
            }
            registerClasses(MultiPartFeature.class, JacksonJsonProvider.class);

            //添加异常处理器
            String classes = jersey.getSingleResource();
            if (StringUtils.hasLength(classes)) {
                Class<?> cls = getExceptionHandlerClass(classes);
                registerClasses(cls);
            }
        };
    }

    private Class<?> getExceptionHandlerClass(String className) throws LinkageError {
        try {
            Class<?> exceptionClass = ClassUtils.forName(className,
                    ClassUtils.getDefaultClassLoader());
            Assert.isAssignable(ExceptionMapper.class, exceptionClass);
            return exceptionClass;
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException(
                    "Failed to load exception handler class [" + className + "]", ex);
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
        beanConfig.setHost(jersey.getIp()+":"+jersey.getPort());
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
    @ConditionalOnClass(JerseyHttp.class)
    @ConditionalOnMissingBean(name="jerseyHttp")
    public JerseyHttp jerseyHttp(JerseyClientProperties clientProperties) {
        return new JerseyHttp(clientProperties);
    }

    @Configuration
    @ConditionalOnMissingBean(type={"javax.ws.rs.ext.ExceptionMapper"})
    private class ExceptionHandler implements ExceptionMapper<Exception> {

        private final Log logger = LogFactory.getLog(ExceptionHandler.class);

        private String basePackage = jersey.getBasePackage();

        @Override
        public Response toResponse(Exception exception) {
            Integer code = ExceptionKeys.SERVICE_ERROR;
            Response.Status statusCode = Response.Status.OK;
            String errMsg = "";

            if (exception instanceof BaseException) {
                code = ((BaseException) exception).getCode();
                errMsg = ((BaseException) exception).getMsg();
            } else if (exception instanceof WebApplicationException) {
                code = ExceptionKeys.HTTP_ERROR;
                if (exception instanceof NotFoundException) {
                    statusCode = Response.Status.NOT_FOUND;
                } else if (exception instanceof NotAllowedException) {
                    statusCode = Response.Status.METHOD_NOT_ALLOWED;
                } else if (exception instanceof NotAcceptableException) {
                    statusCode = Response.Status.NOT_ACCEPTABLE;
                } else if (exception instanceof InternalServerErrorException) {
                    statusCode = Response.Status.INTERNAL_SERVER_ERROR;
                }
            }

            errMsg = org.apache.commons.lang3.StringUtils.isBlank(errMsg) ? ErrorMsgUtil.getErrMsg(code) : errMsg;

            //只打印业务代码异常栈
            exception.setStackTrace(Lists.newArrayList(exception.getStackTrace()).stream().filter(s -> s.getClassName().contains(basePackage)).collect(Collectors.toList()).toArray(new StackTraceElement[]{}));
            logger.error(code, exception);
            return Response.ok(new RestResp<>(code, errMsg)).status(statusCode).build();
        }

        @Bean
        public ResourceConfigCustomizer registerExceptionHandler() {
            return config -> {
                registerClasses(ExceptionHandler.class);
            };
        }
    }
}
