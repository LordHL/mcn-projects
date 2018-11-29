package com.hiekn.boot.autoconfigure.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Author: DingHao
 * @Date: 2018/11/28 10:32
 */
@Configuration
@ConditionalOnClass({DataSource.class,SqlSessionFactory.class,MapperScan.class})
@ConditionalOnMissingBean(MapperFactoryBean.class)
@ConditionOnSingleDatasource
public class SingleDatasourceAutoConfiguration {
}
