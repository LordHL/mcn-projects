package com.hiekn.boot.autoconfigure.mybatis;


import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({DataSource.class,SqlSessionFactory.class})
@ConditionalOnProperty(prefix = "multiply.datasource",name = "name")
public class MultiplyMybatisAutoConfiguration {

    public static final String PREFIX = "multiply.datasource.";

    @Import(MapperScannerRegistry.class)
    static class MultiplyMapperScanner{

    }

}
