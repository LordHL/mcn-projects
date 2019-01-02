package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public final class WebApplicationExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(final WebApplicationException exception) {
        Integer code = ExceptionKeys.HTTP_ERROR_500;
        Response.Status statusCode = Response.Status.OK;
        if (exception instanceof NotFoundException) {
            statusCode = Response.Status.NOT_FOUND;
            code = ExceptionKeys.HTTP_ERROR_404;
        } else if (exception instanceof NotAllowedException) {
            statusCode = Response.Status.METHOD_NOT_ALLOWED;
            code = ExceptionKeys.HTTP_ERROR_405;
        } else if (exception instanceof NotAcceptableException) {
            statusCode = Response.Status.NOT_ACCEPTABLE;
            code = ExceptionKeys.HTTP_ERROR_406;
        } else if (exception instanceof InternalServerErrorException) {
            statusCode = Response.Status.INTERNAL_SERVER_ERROR;
        }
        String errMsg = getErrorMsg(code);
        logger.error("ErrorMsg = {}",errMsg,exception);
        return Response.ok(new RestResp<>(code, errMsg)).status(statusCode).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
