package com.hiekn.boot.autoconfigure.base.util;

import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;

public class ErrorMsgUtil {

    private static PropertiesPropertySource propertiesPropertySource;

    static {
        try {
            propertiesPropertySource = new PropertiesPropertySource("errMsg",PropertiesLoaderUtils.loadAllProperties("mcn-error-msg.properties"));
        } catch (IOException e) {

        }
    }

    public static String getErrMsg(Integer code){
        return propertiesPropertySource.getProperty(code.toString()).toString();
    }

}
