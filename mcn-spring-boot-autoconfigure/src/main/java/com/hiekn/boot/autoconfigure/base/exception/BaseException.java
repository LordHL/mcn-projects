package com.hiekn.boot.autoconfigure.base.exception;


import com.hiekn.boot.autoconfigure.base.exception.handler.ErrorMsg;

/**
 * 异常基类，各个模块的运行期异常均继承与该类 
 */
public class BaseException extends RuntimeException {

    private Integer code;
    private String msg;

    protected BaseException(Integer code) {
        this(code, null);
    }

    protected BaseException(Integer code,String msg) {
        super(msg==null?ErrorMsg.getErrorMsg(code):ErrorMsg.getErrorMsg(code)+msg);
        this.code = code;
        this.msg = msg==null?ErrorMsg.getErrorMsg(code):ErrorMsg.getErrorMsg(code)+msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}