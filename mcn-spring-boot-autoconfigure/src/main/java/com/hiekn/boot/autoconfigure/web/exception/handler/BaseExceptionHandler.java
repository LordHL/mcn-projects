package com.hiekn.boot.autoconfigure.web.exception.handler;

import com.google.common.collect.Lists;
import com.hiekn.boot.autoconfigure.base.exception.BaseException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
public final class BaseExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<BaseException> {

    @Override
    public Response toResponse(final BaseException exception) {
        Integer code = exception.getCode();
        String errMsg = exception.getMsg();
        //只打印业务代码异常栈
        exception.setStackTrace(Lists.newArrayList(exception.getStackTrace()).stream().filter(s -> s.getClassName().contains(basePackage)).collect(Collectors.toList()).toArray(new StackTraceElement[]{}));
        logger.error("ErrorMsg = {}",errMsg,exception);
        return Response.ok(buildErrorMessage(code,errMsg)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
