package com.ruoyi.framework.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.ruoyi.common.service.datasource.CreateDataSource;

@Component
@DependsOn({ "transactionManager" })
public class AtomikosDataSourceCreate implements CreateDataSource {

    private static final Logger logger = LoggerFactory.getLogger(AtomikosDataSourceCreate.class);
    @Autowired
    private DynamicDataSourceProperties properties;

    @Autowired
    private DruidConfig druidConfig;

    @Autowired
    private AtomikosConfig atomikosConfig;

    public DataSource createDataSource(String name, DataSourceProperties dataSourceProperties) {
        Properties prop = properties.build(dataSourceProperties);
        DruidXADataSource dataSource = new DruidXADataSource();
        druidConfig.getDruidDataSources().add(dataSource);
        dataSource.setConnectProperties(prop);
        properties.setProperties(dataSource, prop);
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        atomikosConfig.getAtomikosDataSourceBeans().add(ds);
        ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        ds.setUniqueResourceName(name);
        ds.setXaProperties(prop);
        ds.setXaDataSource(dataSource);
        properties.validateDataSource(ds);
        logger.info("数据源：{} 链接成功", name);
        return ds;
    }
}
