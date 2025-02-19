package com.ruoyi.framework.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
// import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.ruoyi.common.service.datasource.CreateDataSource;
import com.ruoyi.framework.config.DruidConfig;
import com.ruoyi.framework.config.DynamicDataSourceProperties;

@Configuration
public class DataSourceCreate implements CreateDataSource {

    @Autowired
    private DynamicDataSourceProperties properties;

    @Autowired
    private DruidConfig druidConfig;

    @Value("${spring.datasource.dynamic.xa}")
    private boolean xa;

    public DataSource createDataSource(String name, Properties prop) {
        DruidDataSource dataSource = null;
        if (xa) {
            dataSource = new DruidXADataSource();
        } else {
            dataSource = new DruidDataSource();
        }
        druidConfig.getDruidDataSources().add(dataSource);
        dataSource.setConnectProperties(prop);
        properties.setProperties(dataSource, prop);
        return dataSource;
    }

}
