package com.hiekn.boot.autoconfigure.base.exception;

import com.google.common.collect.Lists;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import com.hiekn.boot.autoconfigure.base.util.BeanUtils;
import com.hiekn.boot.autoconfigure.base.util.ErrorMsgUtil;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.stream.Collectors;

public class ExceptionHandler implements ExceptionMapper<Exception> {

    private final Log logger = LogFactory.getLog(ExceptionHandler.class);

    private String basePackage = BeanUtils.getBean(JerseySwaggerProperties.class).getBasePackage();

    @Override
    public Response toResponse(Exception exception) {
        Integer code = ExceptionKeys.SERVICE_ERROR;
        Response.Status statusCode = Response.Status.OK;
        String errMsg = "";

        if (exception instanceof BaseException) {
            code = ((BaseException) exception).getCode();
            errMsg = ((BaseException) exception).getMsg();
        } else if (exception instanceof WebApplicationException) {
            code = ExceptionKeys.HTTP_ERROR;
            if (exception instanceof NotFoundException) {
                statusCode = Response.Status.NOT_FOUND;
            } else if (exception instanceof NotAllowedException) {
                statusCode = Response.Status.METHOD_NOT_ALLOWED;
            } else if (exception instanceof NotAcceptableException) {
                statusCode = Response.Status.NOT_ACCEPTABLE;
            } else if (exception instanceof InternalServerErrorException) {
                statusCode = Response.Status.INTERNAL_SERVER_ERROR;
            }
        }

        errMsg = StringUtils.hasLength(errMsg) ? errMsg : ErrorMsgUtil.getErrMsg(code);

        //只打印业务代码异常栈
        exception.setStackTrace(Lists.newArrayList(exception.getStackTrace()).stream().filter(s -> s.getClassName().contains(basePackage)).collect(Collectors.toList()).toArray(new StackTraceElement[]{}));
        logger.error(code, exception);
        return Response.ok(new RestResp<>(code, errMsg)).status(statusCode).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
