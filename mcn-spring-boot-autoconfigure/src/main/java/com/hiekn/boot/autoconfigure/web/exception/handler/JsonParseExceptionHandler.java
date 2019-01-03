package com.hiekn.boot.autoconfigure.web.exception.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;

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
        Integer code = ExceptionKeys.JSON_PARSE_ERROR;
        String errMsg = getErrorMsg(code);
        //只打印业务代码异常栈
        exception.setStackTrace(Lists.newArrayList(exception.getStackTrace()).stream().filter(s -> s.getClassName().contains(basePackage)).collect(Collectors.toList()).toArray(new StackTraceElement[]{}));
        logger.error("ErrorMsg = {}",errMsg,exception);
        return Response.ok(new RestResp<>(code, errMsg)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
