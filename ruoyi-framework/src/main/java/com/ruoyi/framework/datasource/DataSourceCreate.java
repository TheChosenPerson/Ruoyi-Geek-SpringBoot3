package com.ruoyi.framework.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;
// import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.ruoyi.common.service.datasource.CreateDataSource;
import com.ruoyi.framework.config.DruidConfig;
import com.ruoyi.framework.config.DynamicDataSourceProperties;

@Component
public class DataSourceCreate implements CreateDataSource {

    @Autowired
    private DynamicDataSourceProperties properties;

    @Autowired
    private DruidConfig druidConfig;

    public DataSource createDataSource(String name, Properties prop) {
        DruidDataSource dataSource = new DruidDataSource();
        druidConfig.getDruidDataSources().add(dataSource);
        
        // 设置基础属性
        dataSource.setConnectProperties(prop);
        properties.setProperties(dataSource, prop);
        
        try {
            dataSource.init();
        } catch (Exception e) {
            throw new RuntimeException("初始化数据源失败", e);
        }
        
        return dataSource;
    }
}
