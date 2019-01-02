package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.hiekn.boot.autoconfigure.base.util.SpringBeanUtils;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * common
 *
 * @author DingHao
 * @date 2019/1/2 19:04
 */
public class AbstractExceptionHandler extends ErrorMsg{

    protected String basePackage = SpringBeanUtils.getBean(JerseySwaggerProperties.class).getBasePackage();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

}
