package com.ruoyi.framework.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.ruoyi.common.service.datasource.CreateDataSource;
import com.ruoyi.framework.config.DruidConfig;
import com.ruoyi.framework.config.DynamicDataSourceProperties;

@Component
public class DataSourceCreate implements CreateDataSource {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceCreate.class);
    @Autowired
    private DynamicDataSourceProperties properties;

    @Autowired
    private DruidConfig druidConfig;

    public DataSource createDataSource(String name, Properties prop) {
        DruidXADataSource dataSource = new DruidXADataSource();
        druidConfig.getDruidDataSources().add(dataSource);
        dataSource.setConnectProperties(prop);
        properties.setProperties(dataSource, prop);
        return dataSource;
    }
}
