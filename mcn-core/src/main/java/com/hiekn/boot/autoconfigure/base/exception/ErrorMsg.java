package com.hiekn.boot.autoconfigure.base.exception;

import com.google.common.collect.Lists;
import com.hiekn.boot.autoconfigure.base.util.McnUtils;

import java.util.List;
import java.util.Properties;

public abstract class ErrorMsg implements ExceptionKeys {

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
