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

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class McnPropertiesPostProcessor implements EnvironmentPostProcessor,Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, Object> source = Maps.newHashMap();
        String name = application.getMainApplicationClass().getName();
        String pkg = name.substring(0,name.lastIndexOf("."));
        source.put("jersey.swagger.base-package",pkg);
        MapPropertySource mapPropertySource = new MapPropertySource("mcn-map",source);
        try {
            String path=this.getClass().getResource("").getPath();
            path=path.replace("file:", "jar:file:");
            path=path.replace("com/hiekn/boot/autoconfigure/context", "META-INF");
            Properties properties = PropertiesLoaderUtils.loadProperties(new UrlResource(path+"mcn.properties"));
            PropertiesPropertySource propertySource = new PropertiesPropertySource("mcn-prop",properties);
            propertySources.addLast(propertySource);
        } catch (IOException e) {
        }
        propertySources.addLast(mapPropertySource);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }

}
