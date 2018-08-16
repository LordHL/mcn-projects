package com.hiekn.boot.autoconfigure.base.filter;

import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import com.hiekn.boot.autoconfigure.base.model.result.RestResp;
import com.hiekn.boot.autoconfigure.base.util.ErrorMsgUtil;
import com.hiekn.boot.autoconfigure.base.util.JsonUtils;
import com.hiekn.licence.verify.VerifyLicense;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CheckCertificateFilter implements Filter {

    private static VerifyLicense vLicense;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        vLicense = new VerifyLicense("lic-verify.properties");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        if(!vLicense.verify()){
            int code = ExceptionKeys.INVALID_CERTIFICATE_ERROR;
            response.getWriter().write(JsonUtils.toJson(new RestResp(code,ErrorMsgUtil.getErrMsg(code))));
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

}
