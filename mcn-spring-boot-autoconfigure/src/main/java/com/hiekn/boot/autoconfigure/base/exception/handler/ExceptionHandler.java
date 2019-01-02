package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public final class ExceptionHandler extends ErrorMsg implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(final Exception exception) {
        Integer code = ExceptionKeys.SERVICE_ERROR;
        String errMsg = getErrorMsg(code);
        logger.error("ErrorMsg = {}",errMsg,exception);
        return Response.ok(new RestResp<>(code, errMsg)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
