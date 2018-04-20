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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class McnPropertiesPostProcessor implements EnvironmentPostProcessor,Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        //add map config
        Map<String, Object> mapProp = Maps.newHashMap();
        String name = application.getMainApplicationClass().getName();
        String pkg = name.substring(0,name.lastIndexOf("."));
        mapProp.put("jersey.swagger.base-package",pkg);
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

        }
        try {
            //add mcn default config
            String path=this.getClass().getResource("").getPath();
            path=path.replace("file:", "jar:file:");
            path=path.replace("com/hiekn/boot/autoconfigure/context", "META-INF");
            propertySources.addLast(new PropertiesPropertySource("mcn-prop",PropertiesLoaderUtils.loadProperties(new UrlResource(path+"mcn.properties"))));
        } catch (IOException e) {

        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }

}
