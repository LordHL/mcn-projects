package com.hiekn.boot.autoconfigure.web.exception.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * describe about this class
 *
 * @author DingHao
 * @date 2019/1/3 14:41
 */
@Provider
public class JsonParseExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<JsonParseException> {

    @Override
    public Response toResponse(JsonParseException exception) {
        //只打印业务代码异常栈
        exception.setStackTrace(Lists.newArrayList(exception.getStackTrace()).stream().filter(s -> s.getClassName().contains(basePackage)).collect(Collectors.toList()).toArray(new StackTraceElement[]{}));
        logger.error("ErrorMsg = {}",getErrorMsg(JSON_PARSE_ERROR),exception);
        return Response.ok(buildErrorMessage(JSON_PARSE_ERROR)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
