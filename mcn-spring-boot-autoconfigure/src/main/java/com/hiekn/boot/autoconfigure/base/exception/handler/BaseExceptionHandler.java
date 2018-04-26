package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.google.common.collect.Lists;
import com.hiekn.boot.autoconfigure.base.exception.BaseException;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import com.hiekn.boot.autoconfigure.base.util.BeanUtils;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.stream.Collectors;

public final class BaseExceptionHandler implements ExceptionMapper<BaseException> {

    private final Log logger = LogFactory.getLog(BaseExceptionHandler.class);

    private String basePackage = BeanUtils.getBean(JerseySwaggerProperties.class).getBasePackage();

    @Override
    public Response toResponse(final BaseException exception) {
        Integer code = exception.getCode();
        String errMsg = exception.getMsg();
        //只打印业务代码异常栈
        exception.setStackTrace(Lists.newArrayList(exception.getStackTrace()).stream().filter(s -> s.getClassName().contains(basePackage)).collect(Collectors.toList()).toArray(new StackTraceElement[]{}));
        logger.error(code, exception);
        return Response.ok(new RestResp<>(code, errMsg)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}