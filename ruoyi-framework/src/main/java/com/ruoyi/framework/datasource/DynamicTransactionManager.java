package com.ruoyi.framework.datasource;

import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Component
@EnableTransactionManagement(proxyTargetClass = true)
public class DynamicTransactionManager extends JdbcTransactionManager {

    @Autowired
    DataSourceManagement dataSourceManagement;

    @Override
    public DataSource getDataSource() {
        DataSource dataSource = dataSourceManagement.getDataSource(DynamicDataSourceContextHolder.getDataSourceType());
        if (!Objects.isNull(dataSource)) {
            return dataSource;
        } else {
            return dataSourceManagement.getPrimaryDataSource();
        }
    }
}
