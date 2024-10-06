package com.ruoyi.common.service.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public interface CreateDataSource {
    DataSource createDataSource(String name, DataSourceProperties dataSourceProperties);
}
