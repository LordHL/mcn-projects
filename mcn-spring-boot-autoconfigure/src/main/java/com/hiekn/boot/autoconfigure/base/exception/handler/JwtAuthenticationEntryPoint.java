package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import com.hiekn.boot.autoconfigure.base.exception.InvalidAuthenticationTokenException;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper mapper;

    public JwtAuthenticationEntryPoint(ObjectMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpStatus status;
        RestResp restResp = new RestResp();
        restResp.setActionStatus(RestResp.ActionStatusMethod.FAIL.toString());
        if (authException instanceof InvalidAuthenticationTokenException) {
            status = HttpStatus.UNAUTHORIZED;
            restResp.setErrorCode(ExceptionKeys.AUTHENTICATION_ERROR);
            restResp.setErrorInfo(ErrorMsg.getErrorMsg(ExceptionKeys.AUTHENTICATION_ERROR));
        } else {
            status = HttpStatus.FORBIDDEN;
            restResp.setErrorCode(ExceptionKeys.PERMISSION_NOT_ENOUGH_ERROR);
            restResp.setErrorInfo(ErrorMsg.getErrorMsg(ExceptionKeys.PERMISSION_NOT_ENOUGH_ERROR));
        }
//      restResp.setErrorInfo(authException.getMessage());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        mapper.writeValue(response.getWriter(), restResp);
    }
}