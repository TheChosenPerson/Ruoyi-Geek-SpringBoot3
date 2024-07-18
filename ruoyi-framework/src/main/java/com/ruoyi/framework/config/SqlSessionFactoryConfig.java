package com.ruoyi.framework.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.atomikos.util.IntraVmObjectRegistry;
import com.ruoyi.common.interceptor.mybatis.CreateSqlSessionFactory;
import com.ruoyi.framework.datasource.DynamicSqlSessionTemplate;

@Configuration
public class SqlSessionFactoryConfig {

    @Autowired
    CreateSqlSessionFactory createSqlSessionFactory;

    @Autowired
    DynamicDataSourceProperties dataSourceProperties;

    @Bean(name = "sqlSessionTemplate")
    public DynamicSqlSessionTemplate sqlSessionTemplate(Environment env,List<SqlSessionFactory> sqlSessionFactoryList) throws Exception {
        Map<Object, SqlSessionFactory> sqlSessionFactoryMap = new HashMap<>();
        Map<String, DataSource> targetDataSources = dataSourceProperties.getTargetDataSources();
        for (Map.Entry<String, DataSource> entry : targetDataSources.entrySet()) {
            SqlSessionFactory sessionFactory = createSqlSessionFactory.createSqlSessionFactory(env, entry.getValue());
            sqlSessionFactoryList.add(sessionFactory);
            sqlSessionFactoryMap.put(entry.getKey(), sessionFactory);
            // 应对热重载的特殊处理
            Object ret = com.atomikos.icatch.config.Configuration.removeResource(entry.getKey());
            if (ret != null) {
                IntraVmObjectRegistry.removeResource(entry.getKey());
            }
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
