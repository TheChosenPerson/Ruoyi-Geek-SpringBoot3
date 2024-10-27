package com.ruoyi.framework.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.spring.AtomikosDataSourceBean;
import com.ruoyi.common.service.datasource.CreateDataSource;
import com.ruoyi.framework.config.AtomikosConfig;
import com.ruoyi.framework.config.DruidConfig;
import com.ruoyi.framework.config.DynamicDataSourceProperties;

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
        ds.setXaDataSource(dataSource);
        ds.setXaProperties(prop);
        ds.setMaxPoolSize(dataSource.getMaxActive());
        ds.setMinPoolSize(dataSource.getMinIdle());
        properties.validateDataSource(ds);
        logger.info("数据源：{} 链接成功", name);
        return dataSource;
    }
}
