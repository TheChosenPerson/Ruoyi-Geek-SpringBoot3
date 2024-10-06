package com.ruoyi.framework.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.ruoyi.common.service.mybatis.CreateSqlSessionFactory;
import com.ruoyi.framework.datasource.DataSourceManagement;
import com.ruoyi.framework.datasource.DynamicSqlSessionTemplate;

@Configuration
public class SqlSessionFactoryConfig {

    @Autowired
    CreateSqlSessionFactory createSqlSessionFactory;

    @Autowired
    DynamicDataSourceProperties dataSourceProperties;

    @Autowired
    DataSourceManagement dataSourceManagement;

    @Bean(name = "sqlSessionTemplate")
    public DynamicSqlSessionTemplate sqlSessionTemplate(Environment env) throws Exception {
        Map<Object, SqlSessionFactory> sqlSessionFactoryMap = new HashMap<>();
        Map<String, DataSource> targetDataSources = dataSourceManagement.getDataSourcesMap();
        for (Map.Entry<String, DataSource> entry : targetDataSources.entrySet()) {
            SqlSessionFactory sessionFactory = createSqlSessionFactory.createSqlSessionFactory(env, entry.getValue());
            sqlSessionFactoryMap.put(entry.getKey(), sessionFactory);
        }
        SqlSessionFactory factoryMaster = sqlSessionFactoryMap.get(dataSourceProperties.getPrimary());
        if (factoryMaster == null) {
            throw new RuntimeException("找不到主库配置" + dataSourceProperties.getPrimary());
        }
        DynamicSqlSessionTemplate customSqlSessionTemplate = new DynamicSqlSessionTemplate(factoryMaster);
        customSqlSessionTemplate.setTargetSqlSessionFactorys(sqlSessionFactoryMap);
        return customSqlSessionTemplate;
    }
}
