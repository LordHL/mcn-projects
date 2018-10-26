package com.hiekn.boot.autoconfigure.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.hiekn.boot.autoconfigure.base.exception.handler.JwtAuthenticationEntryPoint;
import com.hiekn.boot.autoconfigure.base.filter.JwtAuthenticationTokenFilter;
import com.hiekn.boot.autoconfigure.base.util.CommonHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import java.util.List;
import java.util.Objects;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ AbstractSecurityWebApplicationInitializer.class, SessionCreationPolicy.class,JWT.class })
@ConditionalOnProperty(prefix = "jwt.security", name = "login", havingValue = "true")
@EnableConfigurationProperties
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private Environment environment;
    private JwtProperties jwtProperties;

    public WebSecurityAutoConfiguration( Environment environment,JwtProperties jwtProperties) {
        this.environment = environment;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        boolean filterCross = environment.getProperty("filter.cross", Boolean.class, true);
        if(filterCross){
            http.cors();
        }
        http
            .csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint())
        .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .anyRequest().authenticated()
        .and()
            .addFilterBefore(new JwtAuthenticationTokenFilter(authenticationEntryPoint()),UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        String basePath = CommonHelper.parsePath(environment.getProperty("spring.jersey.application-path"));
        List<String> defaultIgnoreUrls = Lists.newArrayList(basePath+"/swagger.json",basePath+"/Swagger.html","/swagger/**");
        List<String> ignoreUrls = jwtProperties.getSecurity().getIgnoreUrls();
        if(Objects.nonNull(ignoreUrls) && !ignoreUrls.isEmpty()){
            for (String ignoreUrl : ignoreUrls) {
                defaultIgnoreUrls.add(basePath+CommonHelper.parsePath(ignoreUrl));
            }
        }
        web.ignoring().antMatchers(defaultIgnoreUrls.toArray(new String[defaultIgnoreUrls.size()]));
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtProperties jwtProperties(){
        return new JwtProperties();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new JwtAuthenticationEntryPoint(new ObjectMapper());
    }

}