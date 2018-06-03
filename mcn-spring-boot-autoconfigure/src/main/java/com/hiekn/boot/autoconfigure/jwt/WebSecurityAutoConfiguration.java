package com.hiekn.boot.autoconfigure.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiekn.boot.autoconfigure.base.exception.handler.JwtAuthenticationEntryPoint;
import com.hiekn.boot.autoconfigure.base.filter.JwtAuthenticationTokenFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ AbstractSecurityWebApplicationInitializer.class, SessionCreationPolicy.class,JWT.class })
@ConditionalOnProperty(prefix = "mcn.security", name = "login", havingValue = "true")
@EnableConfigurationProperties
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private Environment environment;

    public WebSecurityAutoConfiguration( Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
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
        String[] ignoreUrls = environment.getProperty("ignore_url", String[].class, new String[]{});
        web.ignoring().antMatchers(ignoreUrls);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new JwtAuthenticationEntryPoint(new ObjectMapper());
    }

}