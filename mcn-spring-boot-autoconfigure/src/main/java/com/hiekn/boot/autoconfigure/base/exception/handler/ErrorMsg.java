package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.google.common.collect.Lists;
import com.hiekn.boot.autoconfigure.base.util.McnUtils;
import com.hiekn.boot.autoconfigure.base.util.SpringBeanUtils;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * @Author: DingHao
 * @Date: 2018/11/29 10:44
 */
public abstract class ErrorMsg {

    protected String basePackage = SpringBeanUtils.getBean(JerseySwaggerProperties.class).getBasePackage();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static List<Properties> errMsgProp;

    static {
        errMsgProp = Lists.newArrayList();
        errMsgProp.add(McnUtils.loadProperties("mcn-error-msg.properties"));
        errMsgProp.add(McnUtils.loadProperties("error-msg.properties"));
    }

    public static String getErrorMsg(Integer code){
        for (Properties prop : errMsgProp) {
            String propertyValue = prop.getProperty(code.toString());
            if(!McnUtils.isNullOrEmpty(propertyValue)){
                return propertyValue;
            }
        }
        return null;
    }

}
