package com.hiekn.boot.autoconfigure.base.exception.handler;

import com.hiekn.boot.autoconfigure.base.util.BeanUtils;
import com.hiekn.boot.autoconfigure.jersey.JerseySwaggerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: DingHao
 * @Date: 2018/11/29 10:44
 */
public abstract class AbstractExceptionHandler {

    protected String basePackage = BeanUtils.getBean(JerseySwaggerProperties.class).getBasePackage();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

}
