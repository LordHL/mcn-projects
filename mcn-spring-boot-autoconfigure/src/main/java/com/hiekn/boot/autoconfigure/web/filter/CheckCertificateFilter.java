package com.hiekn.boot.autoconfigure.web.filter;

import com.hiekn.boot.autoconfigure.base.exception.ErrorMsg;
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
        vLicense.verify();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        if(!vLicense.verify()){
            response.getWriter().write(JsonUtils.toJson(ErrorMsg.invalidCertificate()));
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }

}
