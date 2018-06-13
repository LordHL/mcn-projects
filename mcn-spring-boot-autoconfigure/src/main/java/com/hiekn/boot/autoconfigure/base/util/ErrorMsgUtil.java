package com.hiekn.boot.autoconfigure.base.util;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public class ErrorMsgUtil {

    private static MutablePropertySources mutablePropertySources;

    static {
        mutablePropertySources = new MutablePropertySources();
        ResourcePropertySource resourcePropertySource;
        try {
            resourcePropertySource = new ResourcePropertySource("error-msg.properties");
            mutablePropertySources.addLast(resourcePropertySource);
        } catch (IOException e) {

        }
        try {
            resourcePropertySource = new ResourcePropertySource("mcn-error-msg.properties");
            mutablePropertySources.addLast(resourcePropertySource);
        } catch (IOException e) {

        }
    }

    public static String getErrMsg(Integer code){
        for (PropertySource<?> propertySource : mutablePropertySources) {
            Object value = propertySource.getProperty(code.toString());
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }


}
