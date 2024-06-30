package com.ruoyi.framework.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.ruoyi.common.interceptor.mybatis.CreateSqlSessionFactory;

@Configuration
public class SqlSessionFactoryConfig {

    @Autowired
    CreateSqlSessionFactory createSqlSessionFactory;

    @Bean(name = "sqlSessionFactoryMaster")
    @Primary
    public SqlSessionFactory sqlSessionFactoryMaster(Environment env,
            @Qualifier("masterDataSource") DataSource dataSource) throws Exception {
        return createSqlSessionFactory.createSqlSessionFactory(env, dataSource);
    }

    @Bean(name = "sqlSessionFactorySlave")
    @ConditionalOnBean(name = "slaveDataSource")
    public SqlSessionFactory sqlSessionFactorySlave(Environment env,
            @Qualifier("slaveDataSource") DataSource dataSource) throws Exception {
        return createSqlSessionFactory.createSqlSessionFactory(env, dataSource);
    }
}
