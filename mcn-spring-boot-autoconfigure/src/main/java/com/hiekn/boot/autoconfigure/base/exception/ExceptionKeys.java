package com.hiekn.boot.autoconfigure.base.exception;

public interface ExceptionKeys {
    //3xxxx:通用错误码定义
    //5xxxx:业务相关错误码定义
    //7xxxx:未知错误码
    //8xxxx:Http相关错误码定义
    //9xxxx:统一错误码及第三方服务错误码定义

    Integer PARAM_PARSE_ERROR = 30001;
    Integer JSON_PARSE_ERROR = 30002;

    Integer EXISTED_ERROR = 50001;
    Integer USER_NOT_FOUND_ERROR = 50002;
    Integer PWD_ERROR = 50003;
    Integer GET_CODE_ERROR = 50004;
    Integer VERIFY_MOBILE_CODE_ERROR = 50005;
    Integer VERIFY_CODE_ERROR = 50006;
    Integer USER_EXIST_ERROR = 50007;
    Integer UN_LOGIN_ERROR = 50008;
    Integer USER_PWD_ERROR = 50009;
    Integer INIT_ERROR = 50010;
    Integer UPLOAD_ERROR = 50011;
    Integer AUTHENTICATION_ERROR = 50012;
    Integer TOKEN_CREATE_ERROR = 50013;

    Integer UNKNOWN_ERROR = 70001;

    Integer HTTP_ERROR = 80001;

    Integer SERVICE_ERROR = 90000;
    Integer THIRD_PARTY_ERROR = 90001;
    Integer REMOTE_SERVICE_ERROR = 90002;

}
