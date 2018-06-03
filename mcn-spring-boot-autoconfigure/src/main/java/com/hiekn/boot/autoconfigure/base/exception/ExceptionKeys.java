package com.hiekn.boot.autoconfigure.base.exception;
/*
    3xxxx:通用错误码定义
    5xxxx:业务相关错误码定义
    7xxxx:未知错误码
    8xxxx:Http相关错误码定义
    9xxxx:统一错误码及第三方服务错误码定义
 */
public interface ExceptionKeys {

    Integer PARAM_PARSE_ERROR = 30001;
    Integer JSON_PARSE_ERROR = 30002;
    Integer INIT_ERROR = 30003;
    Integer VERIFY_CODE_ERROR = 30004;
    Integer USER_EXIST_ERROR = 30005;
    Integer USER_NOT_FOUND_ERROR = 30006;
    Integer USER_PWD_ERROR = 30007;
    Integer PWD_ERROR = 30008;
    Integer GET_MOBILE_CODE_ERROR = 30009;
    Integer VERIFY_MOBILE_CODE_ERROR = 30010;
    Integer UPLOAD_ERROR = 30011;
    Integer PERMISSION_NOT_ENOUGH_ERROR = 30012;

    Integer UN_LOGIN_ERROR = 50001;
    Integer AUTHENTICATION_ERROR = 50002;

    Integer UNKNOWN_ERROR = 70001;

    Integer HTTP_ERROR = 80001;

    Integer SERVICE_ERROR = 90000;
    Integer THIRD_PARTY_ERROR = 90001;
    Integer REMOTE_SERVICE_ERROR = 90002;
    Integer REMOTE_DATA_PARSE_ERROR = 90003;

}
