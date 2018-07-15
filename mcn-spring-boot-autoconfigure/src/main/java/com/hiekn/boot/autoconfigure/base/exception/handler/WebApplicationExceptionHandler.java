package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import com.hiekn.boot.autoconfigure.base.util.ErrorMsgUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public final class WebApplicationExceptionHandler  implements ExceptionMapper<WebApplicationException> {

    private static final Log logger = LogFactory.getLog(WebApplicationExceptionHandler.class);

    @Override
    public Response toResponse(final WebApplicationException exception) {
        Integer code = ExceptionKeys.HTTP_ERROR;
        String errMsg = ErrorMsgUtil.getErrMsg(code);
        Response.Status statusCode = Response.Status.OK;
        if (exception instanceof NotFoundException) {
            statusCode = Response.Status.NOT_FOUND;
        } else if (exception instanceof NotAllowedException) {
            statusCode = Response.Status.METHOD_NOT_ALLOWED;
        } else if (exception instanceof NotAcceptableException) {
            statusCode = Response.Status.NOT_ACCEPTABLE;
        } else if (exception instanceof InternalServerErrorException) {
            statusCode = Response.Status.INTERNAL_SERVER_ERROR;
        }
        logger.error(code, exception);
        return Response.ok(new RestResp<>(code, errMsg)).status(statusCode).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
