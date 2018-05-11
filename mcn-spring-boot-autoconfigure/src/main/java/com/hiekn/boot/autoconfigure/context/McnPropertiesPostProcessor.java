package com.hiekn.boot.autoconfigure.context;

import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class McnPropertiesPostProcessor implements EnvironmentPostProcessor,Ordered {

    private static final String APP_BASE_PACKAGE_KEY = "jersey.swagger.base-package";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        //add map config
        Map<String, Object> mapProp = Maps.newHashMap();
        mapProp.put(APP_BASE_PACKAGE_KEY,ClassUtils.getPackageName(application.getMainApplicationClass()));
        propertySources.addLast(new MapPropertySource("mcn-map",mapProp));

        try {
            String[] activeProfiles = environment.getActiveProfiles();
            //add global config
            String globalConfigName = "mcn-global";
            if(Objects.nonNull(activeProfiles)){
                globalConfigName += "-"+activeProfiles[0];
            }
            propertySources.addLast(new ResourcePropertySource("mcn-global","classpath:"+globalConfigName+".properties"));
        } catch (Exception e) {
            //ignore file not found
        }

        try {
            //add mcn default config
            String path = this.getClass().getResource("").getPath();
            path = path.replaceFirst("file:", "jar:file:");
            path = path.replace(ClassUtils.getPackageName(this.getClass()).replace(".", "/"), "META-INF");
            propertySources.addLast(new PropertiesPropertySource("mcn-prop",PropertiesLoaderUtils.loadProperties(new UrlResource(path+"mcn.properties"))));
        } catch (IOException e) {
            //ignore file not found
        }

    }

    @Override
    public int getOrder() {
        return McnApplicationListener.DEFAULT_ORDER + 1;
    }

}
