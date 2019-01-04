package com.hiekn.boot.autoconfigure.base.exception;

import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import com.hiekn.boot.autoconfigure.base.util.McnUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class ErrorMsg implements ExceptionKeys {

    private static List<Properties> errMsgProp;

    static {
        errMsgProp = new ArrayList<>();
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

    public static RestResp invalidCertificate(){
        int code = ExceptionKeys.INVALID_CERTIFICATE_ERROR;
        return new RestResp(code, ErrorMsg.getErrorMsg(code));
    }

    public static RestResp permissionNotEnough(){
        int code = ExceptionKeys.PERMISSION_NOT_ENOUGH_ERROR;
        return new RestResp(code, ErrorMsg.getErrorMsg(code));
    }

    public static RestResp authenticationError(){
        int code = ExceptionKeys.AUTHENTICATION_ERROR;
        return new RestResp(code, ErrorMsg.getErrorMsg(code));
    }

}
