package com.ruoyi.framework.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.config.properties.DruidProperties;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource.dynamic")
public class DynamicDataSourceProperties {

    private Map<String, DataSourceProperties> datasource;
    private String primary;

    public void validateDataSource(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String validationQuery = "SELECT 1";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(validationQuery)) {
                if (!(rs.next() && rs.getInt(1) == 1)) {
                    throw new RuntimeException("数据源连接验证失败：查询结果不正确");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据源连接验证失败", e);
        }
    }

    protected Properties build(DataSourceProperties dataSourceProperties) {
        Properties prop = new Properties();
        DruidProperties druidProperties = SpringUtils.getBean(DruidProperties.class);
        prop.put("url", dataSourceProperties.getUrl());
        prop.put("username", dataSourceProperties.getUsername());
        prop.put("password", dataSourceProperties.getPassword());
        prop.put("initialSize", druidProperties.getInitialSize());
        prop.put("minIdle", druidProperties.getMinIdle());
        prop.put("maxActive", druidProperties.getMaxActive());
        prop.put("maxWait", druidProperties.getMaxWait());
        prop.put("timeBetweenEvictionRunsMillis", druidProperties.getTimeBetweenEvictionRunsMillis());
        prop.put("minEvictableIdleTimeMillis", druidProperties.getMinEvictableIdleTimeMillis());
        prop.put("maxEvictableIdleTimeMillis", druidProperties.getMaxEvictableIdleTimeMillis());
        prop.put("validationQuery", druidProperties.getValidationQuery());
        prop.put("testWhileIdle", druidProperties.isTestWhileIdle());
        prop.put("testOnBorrow", druidProperties.isTestOnBorrow());
        prop.put("testOnReturn", druidProperties.isTestOnReturn());
        return prop;
    }

    protected void setProperties(DruidDataSource dataSource, Properties prop) {
        dataSource.setUrl(prop.getProperty("url"));
        dataSource.setUsername(prop.getProperty("username"));
        dataSource.setPassword(prop.getProperty("password"));
        if (prop.getProperty("initialSize") != null) {
            dataSource.setInitialSize(Integer.parseInt(prop.getProperty("initialSize")));
        }
        if (prop.getProperty("minIdle") != null) {
            dataSource.setMinIdle(Integer.parseInt(prop.getProperty("minIdle")));
        }
        if (prop.getProperty("maxActive") != null) {
            dataSource.setMaxActive(Integer.parseInt(prop.getProperty("maxActive")));
        }
        if (prop.getProperty("maxWait") != null) {
            dataSource.setMaxWait(Long.parseLong(prop.getProperty("maxWait")));
        }
        if (prop.getProperty("timeBetweenEvictionRunsMillis") != null) {
            dataSource.setTimeBetweenEvictionRunsMillis(
                    Long.parseLong(prop.getProperty("timeBetweenEvictionRunsMillis")));
        }
        if (prop.getProperty("minEvictableIdleTimeMillis") != null) {
            dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(prop.getProperty("minEvictableIdleTimeMillis")));
        }
        if (prop.getProperty("maxEvictableIdleTimeMillis") != null) {
            dataSource.setMaxEvictableIdleTimeMillis(Long.parseLong(prop.getProperty("maxEvictableIdleTimeMillis")));
        }
        if (prop.getProperty("validationQuery") != null) {
            dataSource.setValidationQuery(prop.getProperty("validationQuery"));
        }
        if (prop.getProperty("testWhileIdle") != null) {
            dataSource.setTestWhileIdle(Boolean.parseBoolean(prop.getProperty("testWhileIdle")));
        }
        if (prop.getProperty("testOnBorrow") != null) {
            dataSource.setTestOnBorrow(Boolean.parseBoolean(prop.getProperty("testOnBorrow")));
        }
        if (prop.getProperty("testOnReturn") != null) {
            dataSource.setTestOnReturn(Boolean.parseBoolean(prop.getProperty("testOnReturn")));
        }
    }

    public Map<String, DataSourceProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, DataSourceProperties> datasource) {
        this.datasource = datasource;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

}
